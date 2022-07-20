package com.heandroid.ui.account.creation.step4.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.NonUKVehicleModel
import com.heandroid.data.model.account.ValidVehicleCheckRequest
import com.heandroid.databinding.FragmentBusinessVehicleNonUkDetailsBinding
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
import com.heandroid.ui.account.creation.step4.businessaccount.dialog.AddBusinessVehicleListener
import com.heandroid.ui.account.creation.step4.businessaccount.dialog.BusinessAddConfirmDialog
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.addvehicle.dialog.VehicleAddConfirmDialog
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessVehicleNonUKClassFragment: BaseFragment<FragmentBusinessVehicleNonUkDetailsBinding>(),
    View.OnClickListener, AddBusinessVehicleListener {

    private var requestModel: CreateAccountRequestModel? = null
    private var nonUKVehicleModel: NonUKVehicleModel?= null
    private var mClassType = ""

    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountVehicleViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBusinessVehicleNonUkDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.vehicleRegNum.text = getString(R.string.vehicle_reg_num, requestModel?.vehicleNo)
        nonUKVehicleModel = arguments?.getParcelable(Constants.NON_UK_VEHICLE_DATA)

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if (requestModel?.accountType == Constants.BUSINESS_ACCOUNT) {
            binding.groupNameLayout.visible()
            binding.groupNameTitle.visible()
        } else {
            binding.groupNameLayout.gone()
            binding.groupNameTitle.gone()
        }

        binding.classARadioButton.isChecked = true
        mClassType = "1"
        binding.classADesc.visible()
    }

    override fun initCtrl() {

        binding.classARadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                classARadioCheck()
        }

        binding.classBRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                classBRadioCheck()
        }

        binding.classCRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                classCRadioCheck()
        }

        binding.classDRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                classDRadioCheck()
        }
        binding.continueButton.setOnClickListener(this@BusinessVehicleNonUKClassFragment)
    }

    override fun observer() {
        observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
    }

    private fun apiResponseValidVehicle(resource: Resource<String?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when(resource) {
            is Resource.Success -> {

                // UK vehicle Valid from DVLA and Valid from duplicate vehicle check,move to next screen

                val nonUKVehicleModelLocal = NonUKVehicleModel()
                nonUKVehicleModelLocal.vehicleMake = nonUKVehicleModel?.vehicleMake
                nonUKVehicleModelLocal.vehicleModel = nonUKVehicleModel?.vehicleModel
                nonUKVehicleModelLocal.vehicleColor = nonUKVehicleModel?.vehicleColor
                nonUKVehicleModelLocal.vehicleClassDesc = VehicleClassTypeConverter.toClassName(mClassType)

//                val bundle = Bundle()
//                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
//                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                BusinessAddConfirmDialog.newInstance(resources.getString(R.string.str_do_you_want_the_below), "",this@BusinessVehicleNonUKClassFragment).show(childFragmentManager, VehicleAddConfirmDialog.TAG)

//                findNavController().navigate(R.id.action_businessNonUKDetailsFragment_to_businessVehicleDetailFragment, bundle)
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)

                findNavController().navigate(R.id.action_businessNonUkMakeFragment_to_findYourVehicleFragment, arguments)

            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.continueButton -> {


                binding.apply {
                    when {


                        cbDeclare.isChecked && mClassType.isNotEmpty() ->{

                            val vehicleValidReqModel = ValidVehicleCheckRequest(
                                requestModel?.vehicleNo, requestModel?.countryType, "STANDARD",
                                "2022", nonUKVehicleModel?.vehicleModel,  nonUKVehicleModel?.vehicleMake, nonUKVehicleModel?.vehicleColor, "2", "HE")
                            viewModel.validVehicleCheck(vehicleValidReqModel, Constants.AGENCY_ID.toInt())

                        }

                        !cbDeclare.isChecked && mClassType.isNotEmpty() ->
                            Snackbar.make(classAView, "Please select the checkbox", Snackbar.LENGTH_LONG).show()

                        cbDeclare.isChecked && mClassType.isEmpty() ->
                            Snackbar.make(classAView, "Please select the class", Snackbar.LENGTH_LONG).show()

                        else ->
                            Snackbar.make(classAView, "Please select the class and checkbox", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun classARadioCheck() {
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

    private fun classBRadioCheck() {
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

    private fun classCRadioCheck() {
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

    private fun classDRadioCheck() {
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

    override fun onAddClick() {
        binding.apply {

            nonUKVehicleModel?.apply {
                vehicleClassDesc = VehicleClassTypeConverter.toClassName(mClassType)
                vehicleGroup = groupName.text.toString()
            }

            val bundle = Bundle()
            bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
            bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA,  nonUKVehicleModel)
            findNavController().navigate(R.id.action_businessNonUKDetailsFragment_to_businessVehicleDetailFragment, bundle)
        }
    }
}