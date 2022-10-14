package com.conduent.nationalhighways.ui.bottomnav.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentDashboardBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.topup.ManualTopUpActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)


    override fun init() {
        binding.tvCrossingCount.text = getString(R.string.str_two_crossing, "0")
        binding.tvVehicleCount.text = getString(R.string.str_two_vehicle, "0")
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun onResume() {
        super.onResume()
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        getDashBoardAllData()
    }

    private fun getDashBoardAllData() {
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 5,
            transactionType = Constants.TOLL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "", //"11/01/2021" mm/dd/yyyy
            endDate = DateUtils.currentDate() ?: "" //"11/30/2021" mm/dd/yyyy
        )
        dashboardViewModel.getDashboardAllData(request)
    }

    override fun initCtrl() {

        binding.viewVehicle.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean(Constants.FROM_DASHBOARD_TO_VEHICLE_LIST, true)
            }
            findNavController().navigate(
                R.id.action_dashBoardFragment_to_vehicleListFragment2,
                bundle
            )
        }
        binding.viewAllNotifi.setOnClickListener {
            if (requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain)
                    .dataBinding.bottomNavigationView.setActiveNavigationIndex(2)
            }
        }
        binding.viewCrossings.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.FROM, Constants.FROM_DASHBOARD_TO_CROSSING_HISTORY)
            findNavController().navigate(
                R.id.action_dashBoardFragment_to_crossingHistoryFragment,
                bundle
            )
        }

        binding.tvManualTopUp.setOnClickListener {
            requireActivity().startNormalActivity(ManualTopUpActivity::class.java)
        }
    }

    override fun observer() {
        observe(dashboardViewModel.vehicleListVal, ::vehicleListResponse)
        observe(dashboardViewModel.crossingHistoryVal, ::crossingHistoryResponse)
        observe(dashboardViewModel.getAlertsVal, ::handleAlertsData)
        observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetailsResponse)
//        observe(dashboardViewModel.thresholdAmountVal, ::handleThresholdAmountData)
    }

    private fun crossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    it.transactionList?.count?.let { count ->
                        binding.tvCrossingCount.text =
                            getString(R.string.str_two_crossing, count)
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
                    if (it.isNotEmpty()) {
                        binding.tvVehicleCount.text =
                            getString(R.string.str_two_vehicle, it.size.toString())
                    }
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

                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun setAccountDetailsView(data: AccountResponse) {
        binding.apply {
            tvAvailableBalance.text = data.replenishmentInformation?.currentBalance?.run {
                get(0) + " " + drop(1)
            }
            tvAccountNumber.text = data.accountInformation?.number
//            tvAccountStatus.text = data.accountInformation?.accountStatus
//            tvTopUpType.text = data.accountInformation?.accountFinancialstatus
//            tvAccountType.text = data.accountInformation?.type
            data.accountInformation?.accountStatus?.let {
                DashboardUtils.setAccountStatus(it, tvAccountStatus)
            }
            data.accountInformation?.accountFinancialstatus?.let {
                DashboardUtils.setAccountFinancialStatus(it, tvTopUpType)
            }
            data.accountInformation?.type?.let {
                DashboardUtils.setAccountType(it, data.accountInformation.accSubType, tvAccountType)
                sessionManager.saveSubAccountType(data?.accountInformation!!.accSubType!!)
                sessionManager.saveAccountType(it)


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