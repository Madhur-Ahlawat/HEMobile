package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleClassesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleClassesFragment : BaseFragment<FragmentAddVehicleClassesBinding>(),
    AddVehicleListener {

    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private lateinit var mVehicleDetails: VehicleResponse
    private var loader: LoaderDialog? = null
    private var mClassType = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleClassesBinding.inflate(inflater, container, false)


    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        binding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"
    }

    override fun initCtrl() {
        binding.classARadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.apply {
                    classBRadioButton.isChecked = false
                    classCRadioButton.isChecked = false
                    classDRadioButton.isChecked = false
                    classADesc.visible()
                    classBDesc.gone()
                    classCDesc.gone()
                    classDDesc.gone()
                }
                mClassType = "1"
            }
        }

        binding.classBRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.apply {
                    classARadioButton.isChecked = false
                    classCRadioButton.isChecked = false
                    classDRadioButton.isChecked = false
                    classADesc.gone()
                    classBDesc.visible()
                    classCDesc.gone()
                    classDDesc.gone()
                }
                mClassType = "2"
            }
        }
        binding.classCRadioButton.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                binding.apply {
                    classARadioButton.isChecked = false
                    classBRadioButton.isChecked = false
                    classDRadioButton.isChecked = false
                    classADesc.gone()
                    classBDesc.gone()
                    classCDesc.visible()
                    classDDesc.gone()
                }
                mClassType = "3"
            }
        }
        binding.classDRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.apply {
                    classARadioButton.isChecked = false
                    classBRadioButton.isChecked = false
                    classCRadioButton.isChecked = false
                    classADesc.gone()
                    classBDesc.gone()
                    classCDesc.gone()
                    classDDesc.visible()
                }
                mClassType = "4"
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.continueButton.setOnClickListener {
            if (binding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                mVehicleDetails.vehicleInfo.vehicleClassDesc = mClassType

                VehicleAddConfirmDialog.newInstance(
                    mVehicleDetails,
                    this
                ).show(childFragmentManager, VehicleAddConfirmDialog.TAG)
            } else if (!binding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                Snackbar.make(
                    binding.classAView,
                    "Please select the checkbox",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (binding.classVehicleCheckbox.isChecked && mClassType.isEmpty()) {
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
        mVehicleDetails.apply {
            plateInfo.state = "HE"
            plateInfo.type = "STANDARD"
            plateInfo.vehicleGroup = ""
            plateInfo.vehicleComments = "new Vehicle"
            plateInfo.planName = ""
            vehicleInfo.year = "2022"
            vehicleInfo.typeId = null
            vehicleInfo.typeDescription = "REGULAR"
        }

        loader?.show(requireActivity().supportFragmentManager, "")
        vehicleMgmtViewModel.addVehicleApi(mVehicleDetails)
    }

    private fun addVehicleApiCall(status: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (status) {
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