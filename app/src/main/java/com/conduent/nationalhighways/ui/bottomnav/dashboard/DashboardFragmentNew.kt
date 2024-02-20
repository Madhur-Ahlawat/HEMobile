package com.conduent.nationalhighways.ui.bottomnav.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.account.LRDSResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.auth.login.AuthResponseModel
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.FragmentDashboardNewBinding
import com.conduent.nationalhighways.ui.auth.logout.OnLogOutListener
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.dateRangeModel
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.paymentHistoryListData
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.transactions.adapter.TransactionsInnerAdapterDashboard
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.DateUtils.compareDates
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.DashboardUtils
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecoratorDashboardParentAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragmentNew : BaseFragment<FragmentDashboardNewBinding>(), OnLogOutListener,
    View.OnClickListener, BackPressListener {
    private var goToSuccessPage: Boolean = false
    private var paymentHistoryDatesList: MutableList<String> = mutableListOf()
    private var paymentHistoryHashMap: MutableMap<String, MutableList<TransactionData>> =
        hashMapOf()
    private var transactionsAdapter: TransactionsInnerAdapterDashboard? = null
    val dfDate = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private var topup: String? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var ProfileDetailModel: ProfileDetailModel? = null
    private var loader: LoaderDialog? = null
    private val countPerPage = 20
    private var startIndex = 1
    private var noOfPages = 1

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as HomeActivityMain).showHideToolbar(false)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentDashboardNewBinding.inflate(inflater, container, false)

    override fun init() {
        initLoaderDialog()
        setBackPressListener(this)
    }

    private fun initLoaderDialog() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    private fun initTransactionsRecyclerView() {
        transactionsAdapter = TransactionsInnerAdapterDashboard(
            this@DashboardFragmentNew, paymentHistoryListData,
            dashboardViewModel.accountInformationData.value?.accSubType?:""
        )
        mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        binding.rvRecenrTransactions.layoutManager = mLayoutManager
        binding.rvRecenrTransactions.adapter = transactionsAdapter

        binding.rvRecenrTransactions.run {
            if (itemDecorationCount == 0) {
                addItemDecoration(RecyclerViewItemDecoratorDashboardParentAdapter(3, 0))
            }
        }
    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as HomeActivityMain).showHideToolbar(false)
        dashboardViewModel.getLRDSResponse()
    }


    override fun initCtrl() {
        if (arguments?.containsKey(Constants.NAV_FLOW_KEY) == true) {
            navFlowFrom = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        }

        if (arguments?.containsKey(Constants.GO_TO_SUCCESS_PAGE) == true) {
            goToSuccessPage = arguments?.getBoolean(Constants.GO_TO_SUCCESS_PAGE, false)!!
        }
        binding.labelViewAll.setOnClickListener {
            (requireActivity() as HomeActivityMain).viewAllTransactions()
        }
        binding.logout.setOnClickListener {
//            LogoutDialog.newInstance(
//                this
//            ).show(childFragmentManager, Constants.LOGOUT_DIALOG)
//            dashboardViewModel.logout()
            logOutOfAccount()
        }
        binding.tvAvailableBalance.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_notificationsFrament)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observer() {
        observe(dashboardViewModel.logout, ::handleLogout)
        observe(dashboardViewModel.paymentHistoryLiveData, ::handlePaymentResponse)
        observe(dashboardViewModel.alertLivData, ::handleAlertResponse)
        observe(dashboardViewModel.lrdsVal, ::handleLrdsResposne)
        dashboardViewModel.accountType.observe(this@DashboardFragmentNew, Observer { it ->
            handleAccountType(it)
        })
    }

    private fun handleLrdsResposne(resource: Resource<LRDSResponse?>?) {
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.srApprovalStatus?.uppercase().equals("APPROVED")) {
                    requireActivity().startNewActivityByClearingStack(LandingActivity::class.java) {
                        putString(Constants.SHOW_SCREEN, Constants.LRDS_SCREEN)
                    }
                }
            }

            else -> {

            }
        }
    }

    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.messageList?.isNullOrEmpty() == false) {
//                    setPriorityNotifications()
//                    setNotificationAlert(resource.data?.messageList)

                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
//                binding.notificationsRecyclerview.gone()
//                binding.noNotificationsTxt.visible()

            }

            else -> {
                // do nothing
            }
        }
    }

    private fun handleAccountType(ProfileDetailModel: ProfileDetailModel) {
        HomeActivityMain.accountDetailsData?.personalInformation =
            ProfileDetailModel.personalInformation
        HomeActivityMain.accountDetailsData = ProfileDetailModel
        ProfileDetailModel.apply {
            if (accountInformation?.accountType.equals(
                    "BUSINESS",
                    true
                ) || ((accountInformation?.accSubType.equals(
                    "STANDARD", true
                ) && accountInformation?.accountType.equals(
                    "PRIVATE", true
                )))
            ) {
                showNonPayGUI(this)
            } else if (accountInformation?.accSubType.equals(Constants.PAYG)) {
                showPayGUI(this)
            } else if (accountInformation?.accSubType.equals(Constants.EXEMPT_PARTNER)) {
                showExemptPartnerUI(this)
            }
        }
        if (navFlowFrom == Constants.BIOMETRIC_CHANGE && goToSuccessPage) {
            var bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_KEY, navFlowFrom)
            bundle.putParcelable(
                Constants.PERSONALDATA,
                HomeActivityMain.accountDetailsData?.personalInformation
            )
            findNavController().navigate(
                R.id.action_dashBoardFragment_to_accountManagementFragment,
                bundle
            )
            HomeActivityMain.changeBottomIconColors(requireActivity(), 3)
        } else {
            HomeActivityMain.changeBottomIconColors(requireActivity(), 0)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.transactionList?.count?.let {
                    noOfPages = if (it.toInt() % countPerPage == 0) {
                        it.toInt() / countPerPage
                    } else {
                        (it.toInt() / countPerPage) + 1
                    }
                }
                resource.data?.transactionList?.transaction?.let {
                    if (it.isNotEmpty()) {
                        binding.ivNoTransactions.gone()
                        binding.tvNoTransactions.gone()
                        binding.boxViewAll.visible()
                        binding.rvRecenrTransactions.visible()
                        paymentHistoryListData.clear()
                        paymentHistoryHashMap.clear()
                        it.forEachIndexed { index, transactionData ->
                            if (index <= 1) {
                                paymentHistoryListData.add(transactionData)
                            } else {
                                return@forEachIndexed
                            }
                        }
                        paymentHistoryListData =
                            Utils.sortTransactionsDateWiseDescending(paymentHistoryListData)
                                .toMutableList()
                        paymentHistoryDatesList.clear()
                        initTransactionsRecyclerView()
                        transactionsAdapter?.notifyDataSetChanged()
                    } else {
                        binding.rvRecenrTransactions.gone()
                        binding.ivNoTransactions.visible()
                        binding.tvNoTransactions.visible()
                    }
                } ?: run {
                    binding.rvRecenrTransactions.gone()
                    binding.ivNoTransactions.visible()
                    binding.tvNoTransactions.visible()
                }
            }

            is Resource.DataError -> {
                binding.rvRecenrTransactions.gone()
                binding.ivNoTransactions.visible()
                binding.tvNoTransactions.visible()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }
    }

    fun getDatesList(transactionsList: MutableList<TransactionData>) {
        var tempDate: String? = null

        var limitedList = 2
        if (ProfileDetailModel?.accountInformation?.accSubType.equals(Constants.PAYG)) {
            limitedList = 5
        }
        var transactionSize = transactionsList.size
        if (transactionsList.size > limitedList) {
            transactionSize = limitedList
        }
        for (i in 0 until transactionSize) {
            val model = transactionsList.get(i)
            if (tempDate == null) {
                tempDate = model.transactionDate
                paymentHistoryDatesList.clear()
                paymentHistoryDatesList.add(model.transactionDate!!)

            } else {
                if (!dfDate.parse(tempDate).equals(dfDate.parse(model.transactionDate))) {
                    paymentHistoryDatesList.add(model.transactionDate!!)
                    tempDate = model.transactionDate
                }
            }
            var transactionsListTemp =
                paymentHistoryHashMap.get(model.transactionDate) ?: mutableListOf()
            var transactionExistesInTempList = false
            transactionsListTemp.forEach { it2 ->
                if (it2.transactionNumber.equals(model.transactionNumber)) {
                    transactionExistesInTempList = true
                }
            }
            if (transactionExistesInTempList == false) {
                transactionsListTemp.add(model)
            }
            paymentHistoryHashMap.remove(model.transactionDate!!)
            paymentHistoryHashMap.put(model.transactionDate, transactionsListTemp)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactionsDateWiseDescending(transactions: MutableList<TransactionData?>): MutableList<TransactionData> {
        var transactionListSorted: MutableList<TransactionData> = mutableListOf()
        for (transaction in transactions) {
            if (transactionListSorted.isEmpty() == true) {
                transactionListSorted.add(transaction!!)
            } else {
                if (compareDates(
                        transactionListSorted.last().transactionDate + " " + transactionListSorted.last().exitTime,
                        transaction?.transactionDate + " " + transaction?.exitTime
                    )
                ) {
                    transactionListSorted.add(transactionListSorted.size - 1, transaction!!)

                } else {
                    transactionListSorted.add(transaction!!)
                }
            }
        }
        return transactionListSorted
    }

    private fun hideNotification() {
        if (requireActivity() is HomeActivityMain) {
            HomeActivityMain.dataBinding!!.bottomNavigationView.navigationItems.let { list ->
                val badgeCountBtn = list[2].view.findViewById<AppCompatButton>(R.id.badge_btn)
                badgeCountBtn.gone()
            }
        }
    }

    private fun showPayGUI(data: ProfileDetailModel) {
        HomeActivityMain.accountDetailsData = data

        binding.apply {
            tvAvailableBalanceHeading.gone()
            tvAvailableBalance.gone()

            tvAccountStatusHeading.visible()
            cardIndicatorAccountStatus.visible()

            boxTopupAmount.gone()
            boxLowBalanceThreshold.gone()

            boxTopupMethod.gone()
            buttonTopup.gone()

            setGuideLinePercent(0.25F, R.dimen.margin_15dp)

            tvAccountNumberHeading.visible()
            tvAccountNumberValue.text = data.personalInformation?.accountNumber

            data.let {
                it.accountInformation?.let {
                    it.accountStatus?.let {
                        boxCardType.visible()
                        if (data.accountInformation?.paymentTypeInfo.toString().takeLast(4)
                                .lowercase() == "cash"
                        ) {
                            cardNumber.text =
                                requireActivity().resources.getString(R.string.str_cash)
                        } else if (data.accountInformation?.paymentTypeInfo.toString().length >= 4) {
                            cardNumber.text =
                                Utils.maskCardNumber(data.accountInformation?.paymentTypeInfo.toString())
                        } else {
                            cardNumber.text = data.accountInformation?.paymentTypeInfo ?: ""
                        }

                        cardNumber.setTypeface(null, Typeface.NORMAL)

                        DashboardUtils.setAccountStatusNew(
                            it, indicatorAccountStatus, binding.cardIndicatorAccountStatus, 2
                        )
                    }
                    it.type?.let {
                        sessionManager.saveSubAccountType(data.accountInformation?.accSubType)
                        sessionManager.saveAccountType(data.accountInformation?.accountType)
                    }
                }
                it.personalInformation?.emailAddress?.let { email ->
                    sessionManager.saveAccountEmailId(email)
                }
            }

            val cardType = data.replenishmentInformation?.reBillPayType?.uppercase()
            if (cardType.equals("CASH")) {
                cardLogo.gone()
            } else {
                cardLogo.visible()
                cardLogo.setImageResource(
                    Utils.setCardImage(
                        data.accountInformation?.paymentTypeInfo ?: ""
                    )
                )
            }
        }
        getPaymentHistoryList(startIndex,Constants.TOLL_TRANSACTION)
    }

    private fun setGuideLinePercent(percent: Float, margin0dp: Int) {

        val layoutParams = binding.guideline1.layoutParams as ConstraintLayout.LayoutParams

        layoutParams.guidePercent = percent
        binding.guideline1.layoutParams = layoutParams

        val paddingInPx =
            resources.getDimensionPixelSize(margin0dp) // You can define your padding size in resources
        val padding15InPx =
            resources.getDimensionPixelSize(R.dimen.margin_15dp) // You can define your padding size in resources
        binding.boxAccountInformation.setPadding(
            padding15InPx,
            paddingInPx,
            padding15InPx,
            padding15InPx
        )

    }

    private fun showExemptPartnerUI(data: ProfileDetailModel) {
        HomeActivityMain.accountDetailsData = data

        binding.apply {
            tvAvailableBalanceHeading.visible()
            tvAvailableBalance.visible()

            tvAvailableBalance.apply {
                visible()
                text = data.replenishmentInformation?.currentBalance?.run {
                    get(0) + "" + drop(1)
                }
            }



            tvAccountStatusHeading.visible()
            cardIndicatorAccountStatus.visible()

            boxLowBalanceThreshold.visible()
            valueLowBalanceThreshold.text = getString(R.string.str_zero_euro)

            boxTopupAmount.visible()
            valueTopupAmount.text = getString(R.string.str_zero_euro)

            boxTopupMethod.visible()
            valueAutopay.text = getString(R.string.exempt)

            buttonTopup.gone()
            setGuideLinePercent(0.25F, R.dimen.margin_15dp)

            tvAccountNumberHeading.visible()
            tvAccountNumberValue.text = data.personalInformation?.accountNumber
            boxCardType.visible()
            cardNumber.text = getString(R.string.no_payment_method_required)
            cardNumber.setTypeface(null, Typeface.BOLD)
            data.let {
                it.accountInformation?.let {
                    it.accountStatus?.let {
                        DashboardUtils.setAccountStatusNew(
                            it, indicatorAccountStatus, binding.cardIndicatorAccountStatus, 3
                        )
                    }

                    it.type?.let {
                        sessionManager.saveSubAccountType(data.accountInformation?.accSubType)
                        sessionManager.saveAccountType(data.accountInformation?.accountType)
                    }
                }
                it.personalInformation?.emailAddress?.let { email ->
                    sessionManager.saveAccountEmailId(email)
                }
            }

            cardLogo.gone()
            boxViewAll.visible()
            getPaymentHistoryList(startIndex)
            rvRecenrTransactions.visible()
        }
    }

    private fun showNonPayGUI(data: ProfileDetailModel) {
        binding.apply {
            tvAvailableBalanceHeading.visible()
            tvAvailableBalance.visible()
            tvAvailableBalance.apply {
                visible()
                text = data.replenishmentInformation?.currentBalance?.run {
                    get(0) + "" + drop(1)
                }
            }


            tvAccountStatusHeading.visible()
            cardIndicatorAccountStatus.visible()

            boxTopupAmount.visible()
            valueTopupAmount.text = data.replenishmentInformation?.replenishAmount

            boxLowBalanceThreshold.visible()
            valueLowBalanceThreshold.text = data.replenishmentInformation?.replenishThreshold

            boxTopupMethod.visible()

            buttonTopup.visible()
            setGuideLinePercent(0.2F, R.dimen.margin_0dp)

            binding.buttonTopup.setOnClickListener {
                val bundle = Bundle()
                val title = requireActivity().findViewById<TextView>(R.id.title_txt)

                title.text = getString(R.string.top_up)

                if (data.accountInformation?.status.equals(Constants.SUSPENDED, true)) {
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
                    bundle.putString(
                        Constants.CURRENTBALANCE, data.replenishmentInformation?.currentBalance
                    )
                } else {
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYMENT_TOP_UP)
                }

                bundle.putString(Constants.NAV_FLOW_FROM, Constants.DASHBOARD)
                bundle.putParcelable(
                    Constants.PERSONALDATA,
                    HomeActivityMain.accountDetailsData?.personalInformation
                )
                bundle.putParcelable(
                    Constants.ACCOUNTINFORMATION,
                    HomeActivityMain.accountDetailsData?.accountInformation
                )

                findNavController().navigate(
                    R.id.action_dashBoardFragment_to_accountSuspendedPaymentFragment, bundle
                )
            }

            tvAccountNumberHeading.visible()
            tvAccountNumberValue.text = data.personalInformation?.accountNumber
            val cardType = data.accountInformation?.paymentTypeInfo?.uppercase()
            data.let {
                it.accountInformation?.let {
                    it.accountStatus?.let {
                        boxCardType.visible()
                        if (data.accountInformation?.paymentTypeInfo.toString().takeLast(4)
                                .lowercase() == "cash"
                        ) {
                            cardNumber.text =
                                requireActivity().resources.getString(R.string.str_cash)
                        } else if (data.accountInformation?.paymentTypeInfo.toString().length >= 4) {
                            cardNumber.text =
                                Utils.maskCardNumber(data.accountInformation?.paymentTypeInfo.toString())
                        } else {
                            cardNumber.text = data.accountInformation?.paymentTypeInfo ?: ""
                        }

                        cardNumber.setTypeface(null, Typeface.NORMAL)
                        DashboardUtils.setAccountStatusNew(
                            it, indicatorAccountStatus, binding.cardIndicatorAccountStatus, 1
                        )
                    }

                    valueAutopay.text = resources.getString(R.string.str_auto_pay)
                    it.type?.let {
                        sessionManager.saveSubAccountType(data.accountInformation?.accSubType)
                        sessionManager.saveAccountType(data.accountInformation?.accountType)
                    }
                }
                it.personalInformation?.emailAddress?.let { email ->
                    sessionManager.saveAccountEmailId(email)
                }
            }

            if (cardType.equals("CASH")) {
                cardLogo.gone()
            } else {
                viewCard.visible()
                cardLogo.visible()
                cardLogo.setImageResource(
                    Utils.setCardImage(
                        data.accountInformation?.paymentTypeInfo ?: ""
                    )
                )
            }
            getPaymentHistoryList(startIndex)
        }
    }

    private fun getPaymentHistoryList(
        index: Int?=0,
        transactionType:String?=Constants.ALL_TRANSACTION
    ) {
        if (loader?.isVisible == false && loader?.isAdded == true) {
            loader?.showsDialog
        }
        dateRangeModel = PaymentDateRangeModel(
            filterType = Constants.PAYMENT_FILTER_SPECIFIC,
            DateUtils.lastPriorDate(-30),
            DateUtils.currentDate(),
            ""
        )
        val request = AccountPaymentHistoryRequest(
            index, transactionType, countPerPage,
            endDate = DateUtils.currentDateAs(DateUtils.dd_mm_yyyy),
            startDate = DateUtils.getLast90DaysDate(DateUtils.dd_mm_yyyy)
        )
        dashboardViewModel.paymentHistoryDetails(request)
    }

    private fun handleLogout(status: Resource<AuthResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                logOutOfAccount()
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    private fun logOutOfAccount() {
//        sessionManager.clearAll()
        sessionManager.saveBooleanData(SessionManager.SendAuthTokenStatus, false)
        sessionManager.saveBooleanData(SessionManager.LOGGED_OUT_FROM_DASHBOARD, true)
        Intent(requireActivity(), LandingActivity::class.java).apply {
            putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
        requireActivity().finish()
    }

    override fun onLogOutClick() {
        dashboardViewModel.logout()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.button_topup -> {

            }
        }
    }

    override fun onBackButtonPressed() {
        Utils.onBackPressed(requireContext())
    }
}



