package com.conduent.nationalhighways.ui.account.creation.step5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.*
import com.conduent.nationalhighways.databinding.FragmentCreateAccountFindVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(),
    View.OnClickListener {

    private var isAccountVehicle = false
    private var requestModel: CreateAccountRequestModel? = null
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var isObserverBack = false
    private var loader: LoaderDialog? = null
    private var retrieveVehicle: RetrievePlateInfoDetails? = null
    private var nonUKVehicleModel: NonUKVehicleModel? = null
    private var time = (1 * 1000).toLong()
    private var mFromKey = 0


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isObserverBack = true
    }

    override fun init() {
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 5, 6)
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        mFromKey =
            arguments?.getInt(Constants.FROM_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE, 0)!!
        if (mFromKey == Constants.FROM_CREATE_ACCOUNT_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE) {
            nonUKVehicleModel = arguments?.getParcelable(Constants.NON_UK_VEHICLE_DATA)
            binding.addVrmInput.setText(
                nonUKVehicleModel?.vehiclePlate ?: "",
                TextView.BufferType.EDITABLE
            )
            Logg.logging(
                "NotVehicle",
                "bundle CreateAccountFindVehicleFragment nonUKVehicleModel   if cond mFromKey"
            )

        } else {

        }

        Logg.logging(
            "NotVehicle",
            "bundle CreateAccountFindVehicleFragment nonUKVehicleModel data $nonUKVehicleModel"
        )
        Logg.logging(
            "NotVehicle",
            "bundle CreateAccountFindVehicleFragment mFromKey data $mFromKey"
        )

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {

        binding.apply {
            addVrmInput.onTextChanged {
                binding.isEnable = addVrmInput.length() > 1
            }
            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 5, 6)
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

                if (mFromKey == Constants.FROM_CREATE_ACCOUNT_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE) {

                } else {
                    val country: String
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
    }

    private fun businessAccountVehicle(country: String) {
        requestModel?.plateCountryType = country
        requestModel?.vehicleNo = binding.addVrmInput.text.toString()

        val bundle = Bundle()
        bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
        Logg.logging("NotVehicle", "bundle CreateAccountFindVehicleFragment country $country")
//        findNavController().navigate(
//            R.id.action_findVehicleFragment_to_businessVehicleUKListFragment,
//            bundle
//        )

        if (country == "UK")
            getVehicleDataFromDVRM()
        else {
            val bundle = Bundle()
            bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
            findNavController().navigate(
                R.id.action_findVehicleFragment_to_businessVehicleNonUKMakeFragment,
                arguments
            )
        }
    }

    private fun getVehicleDataFromDVRM() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        isObserverBack = true
        viewModel.getVehicleData(requestModel?.vehicleNo, Constants.AGENCY_ID.toInt())
    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        if (isObserverBack) {
            when (resource) {
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
                    findNavController().navigate(
                        R.id.action_findVehicleFragment_to_businessVehicleNonUKMakeFragment,
                        bundle
                    )
                }
                else -> {
                }
            }
        }
    }

    private fun checkForDuplicateVehicle(plateInfo: RetrievePlateInfoDetails) {
        retrieveVehicle = plateInfo
        plateInfo.apply {
            val vehicleValidReqModel = ValidVehicleCheckRequest(
                plateNumber, requestModel?.plateCountryType, "STANDARD",
                "2022", vehicleModel, vehicleMake, vehicleColor, "2", "HE"
            )
            viewModel.validVehicleCheck(vehicleValidReqModel, Constants.AGENCY_ID.toInt())
        }
    }

    private fun apiResponseValidVehicle(resource: Resource<String?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {

                // UK vehicle Valid from DVLA and Valid from duplicate vehicle check,move to next screen
                nonUKVehicleModel?.vehicleMake = retrieveVehicle?.vehicleMake
                nonUKVehicleModel?.vehicleModel = retrieveVehicle?.vehicleModel
                nonUKVehicleModel?.vehicleColor = retrieveVehicle?.vehicleColor
                nonUKVehicleModel?.vehicleClassDesc = retrieveVehicle?.vehicleClass?.let {
                    VehicleClassTypeConverter.toClassName(
                        it
                    )
                }

                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                // prasad commneted

/*
                findNavController().navigate(
                    R.id.action_findYourVehicleFragment_to_businessVehicleDetailFragment,
                    bundle
                )
*/
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                findNavController().popBackStack()
            }
            else -> {
            }
        }
    }

}