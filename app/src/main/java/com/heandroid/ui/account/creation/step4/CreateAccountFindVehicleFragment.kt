package com.heandroid.ui.account.creation.step4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.*
import com.heandroid.databinding.FragmentCreateAccountFindVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.BUSINESS_ACCOUNT
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(), View.OnClickListener {

    private var isAccountVehicle = false
    private var requestModel: CreateAccountRequestModel? = null
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var isObserverBack = false
    private var loader: LoaderDialog? = null
    private var retrieveVehicle: RetrievePlateInfoDetails? = null
    private var nonUKVehicleModel: NonUKVehicleModel? = null
    private var time = (1 * 1000).toLong()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isObserverBack = true
    }

    override fun init() {
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 4, 5)
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {

        binding.apply {
            addVrmInput.onTextChanged {
                binding.isEnable = addVrmInput.length() > 1
            }
            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 4, 5)
            continueBtn.setOnClickListener(this@CreateAccountFindVehicleFragment)
        }
    }

    override fun observer() {
        observe(viewModel.findVehicleLiveData, ::apiResponseDVRM)
        observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.continue_btn -> {

                binding.continueBtn.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.continueBtn.isEnabled = true
                }, time)

                var country = "UK"
                if (binding.addVrmInput.text.toString().isNotEmpty()) {
                    country = if (!binding.switchView.isChecked) {
                        "Non-UK"
                    } else {
                        "UK"
                    }
                    requestModel?.plateCountryType = country

                    businessAccountVehicle(country)

                } else {
                    requireContext().showToast("Please enter your vehicle number")
                }
            }
        }
    }

    private fun businessAccountVehicle(country: String) {
          requestModel?.plateCountryType = country
          requestModel?.vehicleNo = binding.addVrmInput.text.toString()

           val bundle = Bundle()
           bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)

           if(country == "UK")
                findNavController().navigate(R.id.action_findVehicleFragment_to_businessVehicleUKListFragment, bundle)
             else
               getVehicleDataFromDVRM()
    }

    private fun getVehicleDataFromDVRM() {
        loader?.show(requireActivity().supportFragmentManager, "")
        isObserverBack = true
        viewModel.getVehicleData(requestModel?.vehicleNo, Constants.AGENCY_ID.toInt())
    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        if(isObserverBack) {
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        checkForDuplicateVehicle(resource.data.retrievePlateInfoDetails!!)
                    }
                }

                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)

                    isObserverBack = false
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                    findNavController().navigate(R.id.action_findVehicleFragment_to_businessVehicleNonUKMakeFragment, bundle)
                }
            }
        }
    }

    private fun checkForDuplicateVehicle(plateInfo: RetrievePlateInfoDetails) {
        retrieveVehicle = plateInfo
        plateInfo.apply {
            val vehicleValidReqModel = ValidVehicleCheckRequest(
                plateNumber, requestModel?.plateCountryType, "STANDARD",
                "2022", vehicleModel, vehicleMake, vehicleColor, "2", "HE")
            viewModel.validVehicleCheck(vehicleValidReqModel, Constants.AGENCY_ID.toInt())
        }
    }

    private fun apiResponseValidVehicle(resource: Resource<String?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when(resource) {
            is Resource.Success -> {

                // UK vehicle Valid from DVLA and Valid from duplicate vehicle check,move to next screen
                nonUKVehicleModel?.vehicleMake = retrieveVehicle?.vehicleMake
                nonUKVehicleModel?.vehicleModel = retrieveVehicle?.vehicleModel
                nonUKVehicleModel?.vehicleColor = retrieveVehicle?.vehicleColor
                nonUKVehicleModel?.vehicleClassDesc = VehicleClassTypeConverter.toClassName(retrieveVehicle?.vehicleClass!!)

                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)

                findNavController().navigate(R.id.action_findYourVehicleFragment_to_businessVehicleDetailFragment, bundle)
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                findNavController().popBackStack()
            }
        }
    }

}