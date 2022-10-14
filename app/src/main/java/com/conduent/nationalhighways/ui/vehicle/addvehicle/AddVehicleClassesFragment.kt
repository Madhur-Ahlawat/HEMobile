package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentAddVehicleClassesBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog.AddVehicleListener
import com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog.VehicleAddConfirmDialog
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
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
        binding.title.text = getString(R.string.vehicle_reg_num, mVehicleDetails?.plateInfo?.number)

        binding.classBRadioButton.isChecked = true
        mClassType = "2"
        binding.classBDesc.visible()
        checkButton()
    }

    private fun checkButton() {
        binding.model = binding.classVehicleCheckbox.isChecked
    }

    override fun initCtrl() {
        binding.classVehicleCheckbox.setOnCheckedChangeListener { _, _ ->
            checkButton()
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

            if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
                mVehicleDetails?.vehicleInfo?.vehicleClassDesc = mClassType
                val vehicleData = mVehicleDetails
                vehicleData?.apply {
                    vehicleInfo?.vehicleClassDesc =
                        VehicleClassTypeConverter.toClassName(mClassType)
                    vehicleInfo?.effectiveStartDate = Utils.currentDateAndTime()
                }
                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA, vehicleData)
                    putInt(
                        Constants.UK_VEHICLE_DATA_NOT_FOUND_KEY,
                        Constants.UK_VEHICLE_DATA_NOT_FOUND
                    )
                    putInt(
                        Constants.VEHICLE_SCREEN_KEY,
                        Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT
                    )
                }
                findNavController().navigate(
                    R.id.action_addVehicleClassesFragment_to_addVehicleDoneFragment,
                    bundle
                )
                return@setOnClickListener
            } else {
                mVehicleDetails?.vehicleInfo?.vehicleClassDesc = mClassType
                VehicleAddConfirmDialog.newInstance(
                    mVehicleDetails,
                    this
                ).show(childFragmentManager, VehicleAddConfirmDialog.TAG)
            }
        }
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.addVehicleApiVal, ::addVehicleApiCall)
        observe(vehicleMgmtViewModel.validVehicleLiveData, ::apiResponseValidVehicle)
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

//        if (mScreeType==Constants.VEHICLE_SCREEN_TYPE_ADD) {
//            val vehicleValidReqModel = ValidVehicleCheckRequest(
//                details.plateInfo?.number, details.plateInfo?.country, "STANDARD",
//                "2022", details.vehicleInfo?.make, details.vehicleInfo?.model, details.vehicleInfo?.color, "2", "HE")
//            vehicleMgmtViewModel.validVehicleCheck(vehicleValidReqModel, Constants.AGENCY_ID.toInt())
//        }else {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        vehicleMgmtViewModel.addVehicleApi(mVehicleDetails)
//        }
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

    private fun apiResponseValidVehicle(resource: Resource<String?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA, mVehicleDetails)
                    putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                }
                findNavController().navigate(
                    R.id.action_addVehicleClassesFragment_to_addVehicleFragment,
                    bundle
                )
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                val bundle = Bundle().apply {
                    putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                }
                findNavController().navigate(
                    R.id.action_addVehicleClassesFragment_to_addVehicleFragment,
                    bundle
                )
            }
            else -> {
            }
        }
    }
}



