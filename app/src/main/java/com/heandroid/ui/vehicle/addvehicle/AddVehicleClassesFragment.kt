package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.data.model.response.EmptyApiResponse
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentAddVehicleClassesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleClassesFragment : BaseFragment<FragmentAddVehicleClassesBinding>(), AddVehicleListener {

    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private lateinit var mVehicleDetails: VehicleResponse
    private var mClassType = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleClassesBinding.inflate(inflater, container, false)


    override fun init() {
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        binding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"
        binding.classBRadioButton.isChecked = true
        mClassType = "Class B"
    }

    override fun initCtrl() {
        binding.classARadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.classBRadioButton.isChecked = false
                binding.classCRadioButton.isChecked = false
                binding.classDRadioButton.isChecked = false
                mClassType = "Class A"
            }
        }

        binding.classBRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.classARadioButton.isChecked = false
                binding.classCRadioButton.isChecked = false
                binding.classDRadioButton.isChecked = false
                mClassType = "Class B"
            }
        }
        binding.classCRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                binding.classARadioButton.isChecked = false
                binding.classBRadioButton.isChecked = false
                binding.classDRadioButton.isChecked = false
                mClassType = "Class C"
            }
        }
        binding.classDRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.classARadioButton.isChecked = false
                binding.classBRadioButton.isChecked = false
                binding.classCRadioButton.isChecked = false
                mClassType = "Class D"
            }
        }

        binding.cancelButton.setOnClickListener {

        }

        binding.continueButton.setOnClickListener {
            if (binding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                mVehicleDetails.vehicleInfo.vehicleClassDesc = mClassType

                var dialog = VehicleAddConfirmDialog.newInstance(
                    mVehicleDetails,
                    this
                ).show(childFragmentManager, VehicleAddConfirmDialog.TAG)
            } else if (!binding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                Snackbar.make(
                    binding.classAView,
                    "Please select the checkbox",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (binding.classVehicleCheckbox.isChecked && !mClassType.isNotEmpty()) {
                Snackbar.make(
                    binding.classAView,
                    "Please select the class",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                Snackbar.make(
                    binding.classAView,
                    "Please select the class and checkbox",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.addVehicleApiVal, ::addVehicleApiCall)
    }

    override fun onAddClick(details: VehicleResponse) {
//        val request = mVehicleDetails.apply {
//            plateInfo.state = "HE"
//            plateInfo.type = "STANDARD"
//            plateInfo.vehicleGroup = ""
//            plateInfo.vehicleComments = "new Vehicle"
//            plateInfo.planName = ""
//            vehicleInfo.year = "2022"
//            vehicleInfo.typeId = null
//            vehicleInfo.typeDescription = "REGULAR"
//        }
//        mVehicleDetails = request
//        vehicleMgmtViewModel.addVehicleApi(request)

        navigateToAddVehicleDoneScreen()
    }

    private fun addVehicleApiCall(status: Resource<EmptyApiResponse?>?) {
        when(status){
            is Resource.Success -> {
                navigateToAddVehicleDoneScreen()
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun navigateToAddVehicleDoneScreen() {
        val bundle = Bundle().apply {
            putSerializable(Constants.DATA, mVehicleDetails)
            putInt(Constants.VEHICLE_SCREEN_KEY, Constants.VEHICLE_SCREEN_TYPE_ADD)
        }
        findNavController().navigate(R.id.addVehicleDoneFragment, bundle)
    }

}