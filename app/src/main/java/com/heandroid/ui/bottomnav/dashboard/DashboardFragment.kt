package com.heandroid.ui.bottomnav.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentDashboardBinding
import com.heandroid.ui.auth.login.LoginViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private var loader: LoaderDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDashboardBinding = FragmentDashboardBinding.inflate(inflater, container, false)


    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, "")
        dashboardViewModel.getVehicleInformationApi()
    }

    override fun initCtrl() {
    }

    override fun observer() {
        observe(dashboardViewModel.vehicleListVal, ::dashBoardResponse)
    }

    private val TAG = "DashboardFragment"
    private fun dashBoardResponse(status: Resource<List<VehicleResponse>?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                Logg.logging(TAG, "Vehicle Data ${status.data}")

                dashboardViewModel.getAlertsApi(Constants.LANGUAGE)
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }

    }

}