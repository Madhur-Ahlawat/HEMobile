package com.heandroid.ui.bottomnav.dashboard

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.account.ThresholdAmountApiResponse
import com.heandroid.data.model.account.ThresholdAmountData
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentDashboardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.ui.bottomnav.dashboard.topup.ManualTopUpActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.startNormalActivity
import com.heandroid.utils.extn.visible
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
    ): FragmentDashboardBinding = FragmentDashboardBinding.inflate(inflater, container, false)


    override fun init() {
        binding.tvCrossingCount.text = getString(R.string.str_two_crossing, "0")
        binding.tvVehicleCount.text = getString(R.string.str_two_vehicle, "0")
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        getDashBoardAllData()
    }

    private fun getDashBoardAllData() {
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 1,
            transactionType = Constants.ALL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "", //"11/01/2021" mm/dd/yyyy
            endDate = DateUtils.currentDate() ?: "" //"11/30/2021" mm/dd/yyyy
        )
        dashboardViewModel.getDashboardAllData(request)
    }

    override fun initCtrl() {

        binding.tvViewVehicle.setOnClickListener {
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
        binding.tvViewCrossings.setOnClickListener {
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
        observe(dashboardViewModel.thresholdAmountVal, ::handleThresholdAmountData)
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
                        binding.notificationView.visible()
                        binding.viewAllNotifi.text =
                            getString(R.string.str_view_all, alerts.size.toString())
                        binding.viewAllNotifi.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                        if (requireActivity() is HomeActivityMain) {
                            (requireActivity() as HomeActivityMain).dataBinding.bottomNavigationView.navigationItems.let { list ->
                                val badgeCountBtn = list[2].view.findViewById<AppCompatButton>(R.id.badge_btn)
                                badgeCountBtn.visible()
                                badgeCountBtn.text = alerts.size.toString()
                            }
                        }
                    } else {
                        if (requireActivity() is HomeActivityMain) {
                            (requireActivity() as HomeActivityMain).dataBinding.bottomNavigationView.navigationItems.let { list ->
                                val badgeCountBtn = list[2].view.findViewById<AppCompatButton>(R.id.badge_btn)
                                badgeCountBtn.gone()
                            }
                        }
                    }
                    //setNotificationAdapter(it.messageList)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }

    }

    private fun setNotificationAdapter(notificationList: List<AlertMessage?>?) {
        binding.rvNotification.apply {
            adapter = DashboardNotificationAdapter(requireActivity(), notificationList)
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }
    }

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
            tvAccountStatus.text = data.accountInformation?.accountStatus
            tvTopUpType.text = data.accountInformation?.accountFinancialstatus
            tvAccountType.text = data.accountInformation?.type

        }
    }

    private fun handleThresholdAmountData(status: Resource<ThresholdAmountApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    it.thresholdAmountVo?.let { amount ->
                        //stViewBalance(amount)
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

    private fun stViewBalance(thresholdAmountData: ThresholdAmountData) {
        binding.apply {
            tvTitle.text = requireActivity().getString(
                R.string.str_threshold_val_msg,
                thresholdAmountData.customerAmount,
                thresholdAmountData.thresholdAmount
            )
        }
    }
}