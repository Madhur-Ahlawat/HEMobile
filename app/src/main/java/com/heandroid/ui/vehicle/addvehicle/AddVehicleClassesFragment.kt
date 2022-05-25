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
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleClassesFragment : BaseFragment<FragmentAddVehicleClassesBinding>(),
    AddVehicleListener {

    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var mVehicleDetails: VehicleResponse? = null
    private var loader: LoaderDialog? = null
    private var mClassType = ""
    private var mScreeType = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleClassesBinding.inflate(inflater, container, false)


    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        mVehicleDetails = arguments?.getParcelable(Constants.DATA) as? VehicleResponse?
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }
        Logg.logging("testing", " AddVehicleClassesFragment mScreeType  $mScreeType")

        binding.title.text = "Vehicle registration number: ${mVehicleDetails?.plateInfo?.number}"

        binding.classARadioButton.isChecked = true
        mClassType = "1"
        binding.classADesc.visible()
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
            Logg.logging("testing", " AddVehicleClassesFragment continueButton clicked")
            Logg.logging("testing", " AddVehicleClassesFragment continueButton  mScreeType $mScreeType")
            Logg.logging("testing", " AddVehicleClassesFragment continueButton mClassType $mClassType")
            Logg.logging("testing", " AddVehicleClassesFragment continueButton clicked binding.classVehicleCheckbox.isChecked ${binding.classVehicleCheckbox.isChecked}")

            if (binding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                Logg.logging("testing", " AddVehicleClassesFragment continueButton clicked  if called" )

                if (mScreeType==Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
                    mVehicleDetails?.vehicleInfo?.vehicleClassDesc = mClassType
                    val vehicleData = mVehicleDetails
                    vehicleData?.apply {
                        vehicleInfo?.vehicleClassDesc =
                            VehicleClassTypeConverter.toClassName(mClassType)
                        vehicleInfo?.effectiveStartDate = Utils.currentDateAndTime()
                    }
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, vehicleData)
                        putInt(Constants.UK_VEHICLE_DATA_NOT_FOUND_KEY,Constants.UK_VEHICLE_DATA_NOT_FOUND)
                        putInt(
                            Constants.VEHICLE_SCREEN_KEY,
                            Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT
                        )
                    }
                    findNavController().navigate(R.id.action_addVehicleClassesFragment_to_addVehicleDoneFragment, bundle)
                    return@setOnClickListener
                } else {
                    mVehicleDetails?.vehicleInfo?.vehicleClassDesc = mClassType
                    VehicleAddConfirmDialog.newInstance(
                        mVehicleDetails,
                        this
                    ).show(childFragmentManager, VehicleAddConfirmDialog.TAG)
                }
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
        mVehicleDetails?.apply {
            plateInfo?.state = "HE"
            plateInfo?.type = "STANDARD"
            plateInfo?.vehicleGroup = ""
            plateInfo?.vehicleComments = ""
            plateInfo?.planName = ""
            vehicleInfo?.year = "2022"
            vehicleInfo?.typeId = null
            vehicleInfo?.typeDescription = "REGULAR"
            vehicleInfo?.effectiveStartDate = Utils.currentDateAndTime()
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
        val vehicleData = mVehicleDetails
        vehicleData?.apply {
            vehicleInfo?.vehicleClassDesc = VehicleClassTypeConverter.toClassName(mClassType)
            vehicleInfo?.effectiveStartDate = Utils.currentDateAndTime()
        }
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, vehicleData)
            putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
        }
        findNavController().navigate(
            R.id.action_addVehicleClassesFragment_to_addVehicleDoneFragment,
            bundle
        )
    }

}