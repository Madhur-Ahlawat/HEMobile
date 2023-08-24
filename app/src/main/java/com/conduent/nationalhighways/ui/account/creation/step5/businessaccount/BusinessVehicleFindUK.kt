package com.conduent.nationalhighways.ui.account.creation.step5.businessaccount

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.*
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleFindUkBinding
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessVehicleFindUK : BaseFragment<FragmentBusinessVehicleFindUkBinding>(),
    View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null
    private var retrieveVehicle: RetrievePlateInfoDetails? = null

    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var isObserverBack = false
    var time = (1 * 1000).toLong()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessVehicleFindUkBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isObserverBack = true
    }

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.vehicleNumber.text = requestModel?.vehicleNo
        binding.countryBusiness.text = requestModel?.plateCountryType

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
                binding.findVehicleBusiness.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.findVehicleBusiness.isEnabled = true
                }, time)

                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                isObserverBack = true
                getVehicleDataFromDVRM()
            }
        }
    }

    private fun getVehicleDataFromDVRM() {
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
                        resource.data.retrievePlateInfoDetails?.let { it1 ->
                            checkForDuplicateVehicle(
                                it1
                            )
                        }
                    }
                }

                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)

                    isObserverBack = false
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                    findNavController().navigate(R.id.action_businessUKListFragment_to_businessNonUKMakeFragment, bundle)
                }
                else -> {}
            }
        }
    }

    private fun checkForDuplicateVehicle(plateInfo: RetrievePlateInfoDetails) {
        retrieveVehicle = plateInfo
        plateInfo.apply {
            val vehicleValidReqModel = ValidVehicleCheckRequest(
                plateNumber, requestModel?.countryType, "STANDARD",
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

               val nonUKVehicleModel = NonUKVehicleModel()
               nonUKVehicleModel.vehicleMake = retrieveVehicle?.vehicleMake
               nonUKVehicleModel.vehicleModel = retrieveVehicle?.vehicleModel
               nonUKVehicleModel.vehicleColor = retrieveVehicle?.vehicleColor
               nonUKVehicleModel.vehicleClassDesc = retrieveVehicle?.vehicleClass?.let {
                   VehicleClassTypeConverter.toClassName(
                       it
                   )
               }

                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                isObserverBack = false
                (navData as CrossingDetailsModelsResponse).plateNumber=retrieveVehicle?.plateNumber
                bundle.putParcelable(Constants.NAV_DATA_KEY,
                    navData as CrossingDetailsModelsResponse
                )
                findNavController().navigate(R.id.action_businessUKListFragment_to_businessVehicleDetailFragment, bundle)
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                findNavController().popBackStack()
            }
            else -> {}
        }
    }
}