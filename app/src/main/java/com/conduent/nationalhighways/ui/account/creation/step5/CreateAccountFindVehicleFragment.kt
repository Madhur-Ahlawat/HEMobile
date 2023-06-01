package com.conduent.nationalhighways.ui.account.creation.step5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.account.NonUKVehicleModel
import com.conduent.nationalhighways.data.model.account.RetrievePlateInfoDetails
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentCreateAccountFindVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(),
    View.OnClickListener {

    private var isAccountVehicle = false
    //    private var requestModel: CreateAccountRequestModel? = null
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var isObserverBack = false
    private var loader: LoaderDialog? = null
    private var retrieveVehicle: RetrievePlateInfoDetails? = null
    private var nonUKVehicleModel: NonUKVehicleModel? = null
    private var time = (1 * 1000).toLong()
//    private var mFromKey = 0


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isObserverBack = true
    }

    override fun init() {
        /*requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        mFromKey = arguments?.getInt(Constants.FROM_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE, 0)!!
        if (mFromKey == Constants.FROM_CREATE_ACCOUNT_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE) {
            nonUKVehicleModel = arguments?.getParcelable(Constants.NON_UK_VEHICLE_DATA)
            *//*binding.editNumberPlate.setText(
                nonUKVehicleModel?.vehiclePlate ?: "",
                TextView.BufferType.EDITABLE
            )*//*
            Logg.logging(
                "NotVehicle",
                "bundle CreateAccountFindVehicleFragment nonUKVehicleModel   if cond mFromKey"
            )

        }*/

        /* Logg.logging(
             "NotVehicle",
             "bundle CreateAccountFindVehicleFragment nonUKVehicleModel data $nonUKVehicleModel"
         )
         Logg.logging(
             "NotVehicle",
             "bundle CreateAccountFindVehicleFragment mFromKey data $mFromKey"
         )*/

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetterOrDigit(source[i]) &&
                    source[i].toString() != " " &&
                    source[i].toString() != "-"
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        binding.editNumberPlate.editText.filters = arrayOf(filter)
        binding.editNumberPlate.setMaxLength(10)
        binding.editNumberPlate.editText.addTextChangedListener { isEnable() }
        binding.findVehicle.setOnClickListener(this)
    }

    private fun isEnable() {
        val length = binding.editNumberPlate.getText()?.length
        if (length != null) {
            if(length>2) {
                binding.findVehicle.isEnabled = true
                binding.editNumberPlate.error = ""
            }else{
                binding.findVehicle.isEnabled = false
                binding.editNumberPlate.error = getString(R.string.enter_the_vehicle_registration_number_plate_of_your_vehicle)
            }
        }

    }

    override fun observer() {
        observe(viewModel.findNewVehicleLiveData, ::apiResponseDVRM)
        observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.findVehicle -> {

                binding.findVehicle.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.findVehicle.isEnabled = true
                }, time)

//                businessAccountVehicle(binding.editNumberPlate.getText().toString().trim())
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                isObserverBack = true
                viewModel.getNewVehicleData(binding.editNumberPlate.getText().toString().trim(), Constants.AGENCY_ID.toInt())

                /*if (mFromKey == Constants.FROM_CREATE_ACCOUNT_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE) {

                } else {
                    val country = ""
                    if (binding.editNumberPlate.getText().toString().isNotEmpty()) {
                        requestModel?.plateCountryType = country

                        businessAccountVehicle(country)

                    } else {
                        requireContext().showToast("Please enter your vehicle number")
                    }
                }*/

            }
        }
    }

    private fun businessAccountVehicle(country: String) {
//        requestModel?.plateCountryType = country
//        requestModel?.vehicleNo = binding.editNumberPlate.getText().toString()

//        val bundle = Bundle()
//        bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
//        Logg.logging("NotVehicle", "bundle CreateAccountFindVehicleFragment country $country")
//        findNavController().navigate(
//            R.id.action_findVehicleFragment_to_businessVehicleUKListFragment,
//            bundle
//        )

        /*if (country == "UK")
            getVehicleDataFromDVRM()
        else {
            val bundle = Bundle()
            bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
            findNavController().navigate(
                R.id.action_findVehicleFragment_to_businessVehicleNonUKMakeFragment,
                arguments
            )
        }*/
    }

    /*   private fun getVehicleDataFromDVRM() {
           loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
           isObserverBack = true
           viewModel.getVehicleData(requestModel?.vehicleNo, Constants.AGENCY_ID.toInt())
       }*/

    private fun apiResponseDVRM(resource: Resource<List<NewVehicleInfoDetails?>?>) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        if (isObserverBack) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        resource.data.let { it1 ->

                            val bundle = Bundle()
//                            bundle.putParcelableArrayList(Constants.CREATE_ACCOUNT_DATA, it1)
//                            bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                            // prasad commneted

                            findNavController().navigate(
                                R.id.action_findYourVehicleFragment_to_businessVehicleDetailFragment,
                                bundle
                            )
                            /*checkForDuplicateVehicle(
                                it1
                            )*/
                        }
                    }
                }

                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)

                    isObserverBack = false
                    /*val bundle = Bundle()
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                    findNavController().navigate(
                        R.id.action_findVehicleFragment_to_businessVehicleNonUKMakeFragment,
                        bundle
                    )*/
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
                plateNumber, /*requestModel?.plateCountryType*/"UK", "STANDARD",
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
//                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                // prasad commneted

                findNavController().navigate(
                    R.id.action_findYourVehicleFragment_to_businessVehicleDetailFragment,
                    bundle
                )
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