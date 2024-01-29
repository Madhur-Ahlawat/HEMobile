package com.conduent.nationalhighways.ui.account.creation.step5.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.NonUKVehicleModel
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleNonUkDetailsBinding
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.account.creation.step5.businessaccount.dialog.AddBusinessVehicleListener
import com.conduent.nationalhighways.ui.account.creation.step5.businessaccount.dialog.BusinessAddConfirmDialog
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog.VehicleAddConfirmDialog
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessVehicleNonUKClassFragment :
    BaseFragment<FragmentBusinessVehicleNonUkDetailsBinding>(),
    View.OnClickListener, AddBusinessVehicleListener {

    private var requestModel: CreateAccountRequestModel? = null
    private var nonUKVehicleModel: NonUKVehicleModel? = null
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
            binding.groupNameTitle.text = getString(R.string.group_name_field)
            binding.groupNameLayout.hint = getString(R.string.group_name_optional)
        } else {
            binding.groupNameTitle.text = getString(R.string.free_txt_name_field)
            binding.groupNameLayout.hint = getString(R.string.free_txt_name_optional)

        }

        binding.classBRadioButton.isChecked = true
        mClassType = "2"
        binding.classBDesc.visible()
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
        when (resource) {
            is Resource.Success -> {

                // UK vehicle Valid from DVLA and Valid from duplicate vehicle check,move to next screen

                val nonUKVehicleModelLocal = NonUKVehicleModel()
                nonUKVehicleModelLocal.vehicleMake = nonUKVehicleModel?.vehicleMake
                nonUKVehicleModelLocal.vehicleModel = nonUKVehicleModel?.vehicleModel
                nonUKVehicleModelLocal.vehicleColor = nonUKVehicleModel?.vehicleColor
                nonUKVehicleModelLocal.vehicleClassDesc =
                    VehicleClassTypeConverter.toClassName(mClassType)

//                val bundle = Bundle()
//                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
//                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                BusinessAddConfirmDialog.newInstance(
                    resources.getString(R.string.str_do_you_want_the_below),
                    "",
                    this@BusinessVehicleNonUKClassFragment
                ).show(childFragmentManager, VehicleAddConfirmDialog.TAG)

//                findNavController().navigate(R.id.action_businessNonUKDetailsFragment_to_businessVehicleDetailFragment, bundle)
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
                    displaySessionExpireDialog(resource.errorModel)
                }else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)

                    findNavController().navigate(
                        R.id.action_businessNonUkMakeFragment_to_findYourVehicleFragment,
                        arguments
                    )
                }

            }
            else -> {
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.continueButton -> {


                binding.apply {
                    when {


                        cbDeclare.isChecked && mClassType.isNotEmpty() -> {

                            val vehicleValidReqModel = ValidVehicleCheckRequest(
                                requestModel?.vehicleNo,
                                requestModel?.countryType,
                                "STANDARD",
                                "2022",
                                nonUKVehicleModel?.vehicleModel,
                                nonUKVehicleModel?.vehicleMake,
                                nonUKVehicleModel?.vehicleColor,
                                "2",
                                "HE"
                            )
                            viewModel.validVehicleCheck(
                                vehicleValidReqModel,
                                Constants.AGENCY_ID.toInt()
                            )

                        }

                        !cbDeclare.isChecked && mClassType.isNotEmpty() ->
                            Snackbar.make(
                                classAView,
                                "Please select the checkbox",
                                Snackbar.LENGTH_LONG
                            ).show()

                        cbDeclare.isChecked && mClassType.isEmpty() ->
                            Snackbar.make(
                                classAView,
                                "Please select the class",
                                Snackbar.LENGTH_LONG
                            ).show()

                        else ->
                            Snackbar.make(
                                classAView,
                                "Please select the class and checkbox",
                                Snackbar.LENGTH_LONG
                            ).show()
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
                if (requestModel?.accountType == Constants.BUSINESS_ACCOUNT)
                    vehicleGroup = groupName.getText().toString()
                else
                    vehicleComments = groupName.getText().toString()

            }
            Logg.logging("ClassesVehicle", "create acccount Vehicle details $nonUKVehicleModel")

            val bundle = Bundle()
            bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
            bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
            findNavController().navigate(
                R.id.action_businessNonUKDetailsFragment_to_businessVehicleDetailFragment,
                bundle
            )
        }
    }
}