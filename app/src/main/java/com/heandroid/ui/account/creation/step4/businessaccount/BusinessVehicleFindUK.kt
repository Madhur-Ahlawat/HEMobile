package com.heandroid.ui.account.creation.step4.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.*
import com.heandroid.databinding.FragmentBusinessVehicleFindUkBinding
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessVehicleFindUK : BaseFragment<FragmentBusinessVehicleFindUkBinding>(),
    View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null
    private var retrieveVehicle: RetrievePlateInfoDetails? = null

    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var isObserverBack = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessVehicleFindUkBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isObserverBack = true
    }

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.vehicleNumber.text = requestModel?.vehicleNo
        binding.countryBusiness.text = requestModel?.countryType

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.findVehicleBusiness.setOnClickListener(this@BusinessVehicleFindUK)
    }

    override fun observer() {
        observe(viewModel.findVehicleLiveData, ::apiResponseDVRM)
        observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.findVehicleBusiness -> {
                loader?.show(requireActivity().supportFragmentManager, "")
                isObserverBack = true
                getVehicleDataFromDVRM()
            }
        }
    }

    private fun getVehicleDataFromDVRM() {
        viewModel.getVehicleData(requestModel?.vehicleNo, Constants.AGENCY_ID)
    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        if(isObserverBack) {
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        checkForDuplicateVehicle(resource.data.retrievePlateInfoDetails)
                    }
                }

                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)

                    isObserverBack = false
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                    findNavController().navigate(R.id.action_businessUKListFragment_to_businessNonUKMakeFragment, bundle)
                }
            }
        }
    }

    private fun checkForDuplicateVehicle(plateInfo: RetrievePlateInfoDetails) {
        retrieveVehicle = plateInfo
        plateInfo.apply {
            val vehicleValidReqModel = ValidVehicleCheckRequest(
                plateNumber, requestModel?.countryType, "STANDARD",
                "2022", vehicleModel, vehicleMake, vehicleColor, "2", "HE")
            viewModel.validVehicleCheck(vehicleValidReqModel, Constants.AGENCY_ID)
        }
    }

    private fun apiResponseValidVehicle(resource: Resource<String?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when(resource) {
            is Resource.Success -> {

              // UK vehicle Valid from DVLA and Valid from duplicate vehicle check,move to next screen

               val nonUKVehicleModel = NonUKVehicleModel()
               nonUKVehicleModel.vehicleMake = retrieveVehicle?.vehicleMake
               nonUKVehicleModel.vehicleModel = retrieveVehicle?.vehicleModel
               nonUKVehicleModel.vehicleColor = retrieveVehicle?.vehicleColor
               nonUKVehicleModel.vehicleClassDesc = VehicleClassTypeConverter.toClassName(retrieveVehicle?.vehicleClass!!)


                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)

                findNavController().navigate(R.id.action_businessUKListFragment_to_businessVehicleDetailFragment, bundle)
            }


            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                findNavController().popBackStack()
            }
        }
    }
}