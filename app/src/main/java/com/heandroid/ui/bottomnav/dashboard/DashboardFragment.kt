package com.heandroid.ui.bottomnav.dashboard


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.heandroid.ui.bottomnav.dashboard.topup.ManualTopUpActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtActivity
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private val dashboardViewModel: DashboardViewModel  by  viewModels()

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
        loader?.show(requireActivity().supportFragmentManager, "")
        dashboardViewModel.getVehicleInformationApi()
        getCrossingData()
        getNotificationData()
        getAccountDetailsData()
        dashboardViewModel.getThresholdAmountData()
    }

    private fun getNotificationData() {
        dashboardViewModel.getAlertsApi()
    }

    override fun initCtrl() {
        binding.tvViewVehicle.setOnClickListener {
//            val bundle = Bundle().apply {
//                putBoolean(Constants.DATA, true)
//            }
            startActivity(Intent(requireContext(), VehicleMgmtActivity::class.java).apply {
                putExtra(Constants.VEHICLE_SCREEN_KEY, Constants.VEHICLE_SCREEN_TYPE_LIST)
                putExtra(Constants.FROM_DASHBOARD_TO_VEHICLE_LIST,true)
            })

//            findNavController().navigate(
//                R.id.action_dashBoardFragment_to_vehicleListFragment2,
//                bundle
//            )
        }
        binding.crossingsView.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_crossingHistoryFragment)
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
        try {

            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        Log.e("Testing", "--->dashboard data " + it.transactionList)
                        Log.e("Testing", "--->dashboard " + it.transactionList?.transaction?.size ?: "")
                        // todo getting api count as null, so showing count as 0
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
            }}catch (e: Exception){}
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

    private fun getCrossingData() {
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 1,
            transactionType = Constants.TOLL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "", //"11/01/2021" mm/dd/yyyy
            endDate = DateUtils.currentDate() ?: "" //"11/30/2021" mm/dd/yyyy
        )
        Log.e("Testing", "--->DashBoardFragment request $request")

        dashboardViewModel.crossingHistoryApiCall(request)
    }

    private fun handleAlertsData(status: Resource<AlertMessageApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    var notificationList = it?.messageList as List<AlertMessage>
                    setNotificationAdapter(notificationList)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }

    }

    private fun setNotificationAdapter(notificationList: List<AlertMessage>) {
        var layoutMgr = LinearLayoutManager(requireActivity())
        binding.rvNotification.apply {
            adapter = DashboardNotificationAdapter(requireActivity(), notificationList)
            layoutManager = layoutMgr
            setHasFixedSize(true)
        }
    }


    private fun getAccountDetailsData()
    {
        dashboardViewModel.getAccountDetailsData()
    }

    private fun handleAccountDetailsResponse(status: Resource<AccountResponse?>?)
    {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    var accountDetails = it
                  //  sessionManager.setAccountType(accountDetails.accountInformation.type)
                    setAccountDetailsView(accountDetails)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun setAccountDetailsView(data: AccountResponse)
    {
        binding.apply{
            tvAvailableBalance.text = "${data.replenishmentInformation.currentBalance}"
            tvAccountNumber.text = data.accountInformation.number
            tvAccountStatus.text =  data.accountInformation.accountStatus
            tvTopUpType.text = data.accountInformation.accountFinancialstatus
            tvAccountType.text =  data.accountInformation.type
            tvAccountStatus.text =  data.accountInformation.type

        }
    }

    private fun handleThresholdAmountData(status: Resource<ThresholdAmountApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    var thresholdAmountData = it.thresholdAmountVo
                    stViewBalance(thresholdAmountData)
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
        binding.apply{
            tvTitle.text = requireActivity().getString(R.string.str_threshold_val_msg, thresholdAmountData.customerAmount, thresholdAmountData.thresholdAmount)
        }
    }

}