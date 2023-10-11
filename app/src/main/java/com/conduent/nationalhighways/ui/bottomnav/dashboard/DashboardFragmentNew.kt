package com.conduent.nationalhighways.ui.bottomnav.dashboard//package com.conduent.nationalhighways.ui.bottomnav.dashboard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.auth.login.AuthResponseModel
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.FragmentDashboardNewBinding
import com.conduent.nationalhighways.databinding.ItemRecentTansactionsBinding
import com.conduent.nationalhighways.ui.auth.logout.OnLogOutListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.dateRangeModel
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.paymentHistoryListData
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
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
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.GenericRecyclerViewAdapter
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragmentNew : BaseFragment<FragmentDashboardNewBinding>(), OnLogOutListener,
    View.OnClickListener {

    private var topup: String? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var personalInformation: PersonalInformation? = null
    private var loader: LoaderDialog? = null
    private val countPerPage = 10
    private var startIndex = 1
    private var noOfPages = 1
    private val recentTransactionAdapter: GenericRecyclerViewAdapter<TransactionData> by lazy { createPaymentsHistoryListAdapter() }

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    fun createPaymentsHistoryListAdapter() = GenericRecyclerViewAdapter(
        getViewLayout = { R.layout.item_recent_tansactions },
        areItemsSame = ::areRecentTransactionsSame,
        areItemContentsEqual = ::areRecentTransactionsSame,
        onBind = { recentTransactionItem, viewDataBinding, _ ->
            with(viewDataBinding as ItemRecentTansactionsBinding) {
                viewDataBinding.apply {
                    valueCurrentBalance.text = recentTransactionItem.balance
                    tvTransactionType.text =
                        recentTransactionItem.activity?.substring(0, 1)!!
                            .uppercase(Locale.getDefault())
                            .plus(
                            recentTransactionItem.activity.substring(
                                1,
                                recentTransactionItem.activity.length
                            )!!.lowercase(Locale.getDefault())
                        )
                    if (recentTransactionItem.amount?.contains("-") == false) {
                        verticalStripTransactionType.setBackgroundColor(resources.getColor(R.color.green_status))
                        indicatorIconTransactionType.setImageDrawable(resources.getDrawable(R.drawable.ic_euro_circular_green))
                        topup = "+" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.green_status))
                    } else {
                        verticalStripTransactionType.setBackgroundColor(resources.getColor(R.color.red_status))
                        indicatorIconTransactionType.setImageDrawable(resources.getDrawable(R.drawable.ic_car_grey))
                        topup = "-" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.red_status))
                    }
                    root.setOnClickListener {
                        crossing = recentTransactionItem
                        val bundle = Bundle()
//                        bundle.putInt(Constants.FROM, Constants.FROM_ALL_TRANSACTIONS_TO_DETAILS)
                        if (crossing?.activity.equals("Toll")) {
                            findNavController().navigate(
                                R.id.action_dashBoardFragment_to_tollDetails,
                                bundle
                            )
                        } else {
                            findNavController().navigate(
                                R.id.action_dashBoardFragment_to_topUpDetails,
                                bundle
                            )
                        }
                    }
                }
            }
        }
    )

    fun areRecentTransactionsSame(item1: TransactionData, item2: TransactionData): Boolean {
        return ((item1.transactionNumber == item2.transactionNumber) && (item1.transactionNumber == item2.transactionNumber) && (item1.transactionNumber == item2.transactionNumber))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as HomeActivityMain).showHideToolbar(false)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardNewBinding.inflate(inflater, container, false)

    override fun init() {
        initTransactionsRecyclerView()
        initLoaderDialog()
    }

    private fun initLoaderDialog() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    private fun initTransactionsRecyclerView() {
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvRecenrTransactions.run {
            if (itemDecorationCount == 0) {
                addItemDecoration(RecyclerViewItemDecorator(10, 1))
            }
            binding.rvRecenrTransactions.layoutManager = mLayoutManager
            adapter = recentTransactionAdapter
        }
    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as HomeActivityMain).showHideToolbar(false)

    }


    override fun initCtrl() {
        binding.labelViewAll.setOnClickListener {
            (requireActivity() as HomeActivityMain).viewAllTransactions()
        }
        binding.logout.setOnClickListener {
//            LogoutDialog.newInstance(
//                this
//            ).show(childFragmentManager, Constants.LOGOUT_DIALOG)
            dashboardViewModel.logout()
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
        dashboardViewModel.accountType.observe(this@DashboardFragmentNew, Observer { it ->
            handleAccountType(it)
        })
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

    private fun handleAccountType(accountResponse: AccountResponse) {
        accountResponse.apply {
            if (accountInformation?.accountType.equals("BUSINESS", true)
                || ((accountInformation?.accSubType.equals(
                    "STANDARD",
                    true
                ) && accountInformation?.accountType.equals(
                    "PRIVATE", true
                )))
            ) {
                showNonPayGUI(this)
            } else if (accountInformation?.accSubType.equals(Constants.PAYG)) {
                showPayGUI(this)
            }
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
                        binding.tvNoHistory.gone()
                        binding.boxViewAll.visible()
                        binding.rvRecenrTransactions.visible()
                        paymentHistoryListData?.clear()
                        paymentHistoryListData?.addAll(it)
                        paymentHistoryListData =
                            sortTransactionsDateWiseDescending(
                                paymentHistoryListData ?: ArrayList()
                            ).toMutableList()
                        recentTransactionAdapter.submitList(
                            sortTransactionsDateWiseDescending(
                                paymentHistoryListData!!
                            )
                        )
                    } else {
                        binding.boxViewAll.gone()
                        binding.rvRecenrTransactions.gone()
                        binding.tvNoHistory.visible()
//                        binding.paginationLayout.gone()
                    }
                } ?: run {
                    binding.boxViewAll.gone()
                    binding.rvRecenrTransactions.gone()
                    binding.tvNoHistory.visible()
//                    binding.paginationLayout.gone()
                }
            }

            is Resource.DataError -> {
                binding.boxViewAll.gone()
                binding.rvRecenrTransactions.gone()
                binding.tvNoHistory.visible()
//                binding.paginationLayout.gone()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
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

    private fun handleAlertsData(status: Resource<AlertMessageApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.messageList?.let { alerts ->
                    if (alerts.isNotEmpty()) {
//                        binding.notificationView.visible()
//                        binding.viewAllNotifi.text =
//                            getString(R.string.str_view_all, alerts.size.toString())
//                        binding.viewAllNotifi.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                        if (requireActivity() is HomeActivityMain) {
                            HomeActivityMain.dataBinding!!.bottomNavigationView.navigationItems.let { list ->
                                val badgeCountBtn =
                                    list[2].view.findViewById<AppCompatButton>(R.id.badge_btn)
                                badgeCountBtn.visible()
                                badgeCountBtn.text = alerts.size.toString()
                            }
                        }
                    } else {
                        hideNotification()
                    }
                }
            }

            is Resource.DataError -> {
                if (status.errorModel?.errorCode == Constants.NO_DATA_FOR_NOTIFICATIONS) {
                    hideNotification()
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }

            else -> {

            }
        }

    }

    private fun hideNotification() {
        if (requireActivity() is HomeActivityMain) {
            HomeActivityMain.dataBinding!!.bottomNavigationView.navigationItems.let { list ->
                val badgeCountBtn =
                    list[2].view.findViewById<AppCompatButton>(R.id.badge_btn)
                badgeCountBtn.gone()
            }
        }
    }

    private fun showPayGUI(data: AccountResponse) {
        binding.apply {
            tvAvailableBalanceHeading.gone()
            tvAvailableBalance.gone()
//            tvAvailableBalance.apply {
//                visible()
//                text = data.replenishmentInformation?.currentBalance?.run {
//                    get(0) + " " + drop(1)
//                }
//            }

            tvAccountStatusHeading.visible()
            cardIndicatorAccountStatus.visible()

            boxTopupAmount.gone()
//            valueTopupAmount.text = data.replenishmentInformation?.replenishAmount

            boxLowBalanceThreshold.gone()
//            valueLowBalanceThreshold.text = data.replenishmentInformation?.replenishThreshold

            boxTopupMethod.gone()
            buttonTopup.gone()

            tvAccountNumberHeading.visible()
            tvAccountNumberValue.text = data.personalInformation?.accountNumber
//            tvAccountStatus.text = data.accountInformation?.accountStatus
//            tvTopUpType.text = data.accountInformation?.accountFinancialstatus
//            tvAccountType.text = data.accountInformation?.type
            data.let {
                it.accountInformation?.let {
                    it.accountStatus?.let {
                        boxCardType.visible()
                        cardNumber.text = Utils.setMaskWithDots(
                            requireActivity(),
                            data.accountInformation?.paymentTypeInfo
                        )
                        DashboardUtils.setAccountStatusNew(
                            it,
                            indicatorAccountStatus,
                            binding.cardIndicatorAccountStatus
                        )
                    }
//                    it.accountFinancialstatus?.let {
//                        DashboardUtils.setAccountFinancialStatus(it, valueAutopay)
//                    }
                    it.type?.let {
                        //                DashboardUtils.setAccountType(it, data.accountInformation.accSubType, tvAccountType)
                        sessionManager.saveSubAccountType(data.accountInformation?.accSubType)
                        sessionManager.saveAccountType(data.accountInformation?.accountType)
                    }
                }
                it.personalInformation?.emailAddress?.let { email ->
                    sessionManager.saveAccountEmailId(email)
                }
            }
            when (data.replenishmentInformation?.reBillPayType) {
                Constants.MASTERCARD -> {
                    cardLogo.setImageDrawable(resources.getDrawable(R.drawable.mastercard))
                }

                Constants.VISA -> {
                    cardLogo.setImageDrawable(resources.getDrawable(R.drawable.visablue))

                }

                Constants.MAESTRO -> {
                    cardLogo.setImageDrawable(resources.getDrawable(R.drawable.visablue))
                }
            }
        }
        getPaymentHistoryList(startIndex)
    }

    private fun showNonPayGUI(data: AccountResponse) {
        binding.apply {
            tvAvailableBalanceHeading.visible()
            tvAvailableBalance.visible()
            tvAvailableBalance.apply {
                visible()
                text = data.replenishmentInformation?.currentBalance?.run {
                    get(0) + " " + drop(1)
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
            binding.buttonTopup.setOnClickListener {
                val title = requireActivity().findViewById<TextView>(R.id.title_txt)

                title.text = getString(R.string.top_up)

                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYMENT_TOP_UP)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)

                findNavController().navigate(
                    R.id.action_dashBoardFragment_to_accountSuspendedPaymentFragment,
                    bundle
                )
            }

            tvAccountNumberHeading.visible()
            tvAccountNumberValue.text = data.personalInformation?.accountNumber
//            tvAccountStatus.text = data.accountInformation?.accountStatus
//            tvTopUpType.text = data.accountInformation?.accountFinancialstatus
//            tvAccountType.text = data.accountInformation?.type
            data.let {
                it.accountInformation?.let {
                    it.accountStatus?.let {
                        boxCardType.visible()
                        if (data.accountInformation?.paymentTypeInfo?.length!! >= 4) {
                            cardNumber.text = resources.getString(
                                R.string.str_maskcardnumber,
                                data.accountInformation.paymentTypeInfo.takeLast(4)
                            )
                        } else {
                            cardNumber.text = resources.getString(
                                R.string.str_maskcardnumber,
                                data.accountInformation.paymentTypeInfo
                            )
                        }
                        DashboardUtils.setAccountStatusNew(
                            it,
                            indicatorAccountStatus,
                            binding.cardIndicatorAccountStatus
                        )
                    }
                    it.accountFinancialstatus?.let {
                        DashboardUtils.setAccountFinancialStatus(it, valueAutopay)
                    }
                    it.type?.let {
                        //                DashboardUtils.setAccountType(it, data.accountInformation.accSubType, tvAccountType)
                        sessionManager.saveSubAccountType(data.accountInformation?.accSubType)
                        sessionManager.saveAccountType(data.accountInformation?.accountType)
                    }
                }
                it.personalInformation?.emailAddress?.let { email ->
                    sessionManager.saveAccountEmailId(email)
                }
            }
            when (data.replenishmentInformation?.reBillPayType) {
                Constants.MASTERCARD -> {
                    cardLogo.setImageDrawable(resources.getDrawable(R.drawable.mastercard))
                }

                Constants.VISA -> {
                    cardLogo.setImageDrawable(resources.getDrawable(R.drawable.visablue))

                }

                Constants.MAESTRO -> {
                    cardLogo.setImageDrawable(resources.getDrawable(R.drawable.visablue))
                }
            }
            boxViewAll.visible()
            rvRecenrTransactions.visible()
            getPaymentHistoryList(startIndex)
        }
    }

    private fun getPaymentHistoryList(
        index: Int
    ) {
        if (loader?.isVisible == false && loader?.isAdded == true) {
            loader?.showsDialog
        }
        dateRangeModel =
            PaymentDateRangeModel(
                filterType = Constants.PAYMENT_FILTER_SPECIFIC,
                DateUtils.lastPriorDate(-30), DateUtils.currentDate(), ""
            )
        val request = AccountPaymentHistoryRequest(
            index,
            Constants.PAYMENT,
            countPerPage,
            dateRangeModel?.startDate,
            dateRangeModel?.endDate,
            dateRangeModel?.vehicleNumber
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
        sessionManager.clearAll()
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
}



