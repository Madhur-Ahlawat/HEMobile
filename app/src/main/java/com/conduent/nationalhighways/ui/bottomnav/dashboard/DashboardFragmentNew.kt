package com.conduent.nationalhighways.ui.bottomnav.dashboard//package com.conduent.nationalhighways.ui.bottomnav.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentDashboardNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.adapters.RecentTransactionsAdapter
import com.conduent.nationalhighways.ui.bottomnav.dashboard.topup.ManualTopUpActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.DashboardUtils
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.extn.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragmentNew : BaseFragment<FragmentDashboardNewBinding>() {

    private var layoutManager: LinearLayoutManager?=null
    private var paymentHistoryAdapter: RecentTransactionsAdapter? = null
    private var dateRangeModel: PaymentDateRangeModel? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var paymentHistoryListData: MutableList<TransactionData?> = ArrayList()
    private var loader: LoaderDialog? = null
    private val countPerPage = 10
    private var startIndex = 1
    private var noOfPages = 1
    @Inject
    lateinit var sessionManager: SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardNewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun init() {
        initTransactionsRecyclerView()
        initLoaderDialog()
    }

    private fun initLoaderDialog() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    private fun initTransactionsRecyclerView() {
        paymentHistoryAdapter = RecentTransactionsAdapter(this, paymentHistoryListData)
        layoutManager=LinearLayoutManager(requireContext())
        layoutManager!!.orientation=LinearLayoutManager.VERTICAL
        binding?.rvRecenrTransactions?.layoutManager=layoutManager
        binding.rvRecenrTransactions.adapter = paymentHistoryAdapter    }

    override fun onResume() {
        super.onResume()
        getDashBoardAllData()
        getPaymentHistoryList(startIndex)
    }

    private fun getDashBoardAllData() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 5,
            transactionType = Constants.ALL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "", //"11/01/2021" mm/dd/yyyy
            endDate = DateUtils.currentDate() ?: "" //"11/30/2021" mm/dd/yyyy
        )
        Log.e("XJ220",Gson().toJson(request))
        dashboardViewModel.getDashboardAllData(request)
    }

    private fun getPaymentHistoryList(
        index: Int
    ) {
        binding.progressBar.visibility
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

    override fun initCtrl() {
        binding.labelViewAll.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.FROM, Constants.FROM_DASHBOARD_TO_CROSSING_HISTORY)
            findNavController().navigate(
                R.id.action_dashBoardFragment_to_crossingHistoryFragment,
                bundle
            )
        }

        binding.buttonTopup.setOnClickListener {
            requireActivity().startNormalActivity(ManualTopUpActivity::class.java)
        }
    }

    override fun observer() {
        observe(dashboardViewModel.paymentHistoryLiveData, ::handlePaymentResponse)
        observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetailsResponse)
//        observe(dashboardViewModel.thresholdAmountVal, ::handleThresholdAmountData)
    }


    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        binding.progressBar.gone()
        //binding.paginationLayout.visible()
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
//                    binding.nextBtnModel = selectedPosition != noOfPages
//                    binding.prevBtnModel = selectedPosition != 1
                    if (it.isNotEmpty()) {
                        binding.tvNoHistory.gone()
                        binding.rvRecenrTransactions.visible()
                        paymentHistoryListData.clear()
                        paymentHistoryListData.addAll(it)
                        paymentHistoryAdapter?.notifyDataSetChanged()
//                        binding.paginationLayout.visible()

//                        paginationNumberAdapter?.apply {
//                            setCount(noOfPages)
//                            setSelectedPosit(selectedPosition)
//                        }
//                        binding.paginationNumberRecyclerView.adapter = paginationNumberAdapter
                    } else {
                        binding.rvRecenrTransactions.gone()
                        binding.tvNoHistory.visible()
//                        binding.paginationLayout.gone()
                    }
                } ?: run {
                    binding.rvRecenrTransactions.gone()
                    binding.tvNoHistory.visible()
//                    binding.paginationLayout.gone()
                }
            }

            is Resource.DataError -> {
                binding.rvRecenrTransactions.gone()
                binding.tvNoHistory.visible()
//                binding.paginationLayout.gone()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }
    }


    private fun crossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    it.transactionList?.count?.let { count ->
//                        binding.tvCrossingCount.text =
//                            getString(R.string.str_two_crossing, count)
                    }
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }
    }

    private fun vehicleListResponse(status: Resource<List<VehicleResponse?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.let {
//                    if (it.isNotEmpty()) {
//                        binding.tvVehicleCount.text =
//                            getString(R.string.str_two_vehicle, it.size.toString())
//                    }
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {

            }
        }
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
                            (requireActivity() as HomeActivityMain).dataBinding.bottomNavigationView.navigationItems.let { list ->
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
            (requireActivity() as HomeActivityMain).dataBinding.bottomNavigationView.navigationItems.let { list ->
                val badgeCountBtn =
                    list[2].view.findViewById<AppCompatButton>(R.id.badge_btn)
                badgeCountBtn.gone()
            }
        }
    }


//    private fun setNotificationAdapter(notificationList: List<AlertMessage?>?) {
//        binding.rvNotification.apply {
//            adapter = DashboardNotificationAdapter(requireActivity(), notificationList)
//            layoutManager = LinearLayoutManager(requireActivity())
//            setHasFixedSize(true)
//        }
//    }

    private fun handleAccountDetailsResponse(status: Resource<AccountResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    setAccountDetailsView(it)
                    setTopUpVisibility(it)
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {

            }
        }
    }

    private fun setTopUpVisibility(data: AccountResponse) {
        if (data.accountInformation?.accountType.equals("BUSINESS", true)
            || (data.accountInformation?.accSubType.equals("STANDARD", true) &&
                    data.accountInformation?.accountType.equals("PRIVATE", true))
        ) {
            binding.boxAccountInformation.visible()
        } else {
            binding.boxAccountInformation.gone()
        }
    }

    private fun setAccountDetailsView(data: AccountResponse) {
        binding.apply {
            tvAvailableBalance.text = data.replenishmentInformation?.currentBalance?.run {
                get(0) + " " + drop(1)
            }
            valueTopupAmount.text = data.replenishmentInformation?.replenishAmount
            valueLowBalanceThreshold.text = data.replenishmentInformation?.replenishThreshold

//            tvAccountStatus.text = data.accountInformation?.accountStatus
//            tvTopUpType.text = data.accountInformation?.accountFinancialstatus
//            tvAccountType.text = data.accountInformation?.type
            data.let{
                it.accountInformation?.let {
                    it.accountStatus?.let {
                        cardNumber.text = data.accountInformation?.paymentTypeInfo
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
        }
    }

//    private fun handleThresholdAmountData(status: Resource<ThresholdAmountApiResponse?>?) {
//        if (loader?.isVisible == true) {
//            loader?.dismiss()
//        }
//        when (status) {
//            is Resource.Success -> {
//                status.data?.let {
//                    it.thresholdAmountVo?.let { amount ->
//                        //stViewBalance(amount)
//                    }
//                }
//            }
//            is Resource.DataError -> {
//                ErrorUtil.showError(binding.root, status.errorMsg)
//            }
//            else -> {
//
//            }
//        }
//
//    }

//    private fun stViewBalance(thresholdAmountData: ThresholdAmountData) {
//        binding.apply {
//            tvTitle.text = requireActivity().getString(
//                R.string.str_threshold_val_msg,
//                thresholdAmountData.customerAmount,
//                thresholdAmountData.thresholdAmount
//            )
//        }
//    }
}