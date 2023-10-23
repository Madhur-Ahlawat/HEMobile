package com.conduent.nationalhighways.ui.account.creation.step5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.GetPlateInfoResponseModel
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentCreateAccountFindVehicleBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.splCharsVehicleRegistration
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class
CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(),
    View.OnClickListener {
    private var data: CrossingDetailsModelsResponse? = null
    private var isViewCreated = false
    private var plateNumber = ""
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var time = (1 * 1000).toLong()
    private var isCrossingCall = false
    private var isClicked: Boolean = false


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        isCrossingCall = navFlowCall.equals(Constants.PAY_FOR_CROSSINGS, true)
        arguments?.getString(Constants.PLATE_NUMBER, "").toString()
            .let { plateNumber = it.replace("null", "") }
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        if (data == null) {
            data = CrossingDetailsModelsResponse()
        }

        binding.editNumberPlate.editText.filters = arrayOf(InputFilter.LengthFilter(10))
        binding.editNumberPlate.editText.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        if (!plateNumber.isNullOrEmpty()) {
            binding.editNumberPlate.setText(plateNumber.trim().replace(" ", "").replace("-", ""))

        }
        if (plateNumber.isNotEmpty()) {
            binding.findVehicle.isEnabled = true
        }
        loader = LoaderDialog()
        loader?.setStyle(
            DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle
        )

        NewCreateAccountRequestModel.isExempted = false
        NewCreateAccountRequestModel.isRucEligible = false
        NewCreateAccountRequestModel.isVehicleAlreadyAdded = false
        NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal = false
        NewCreateAccountRequestModel.isMaxVehicleAdded = false
        NewCreateAccountRequestModel.plateNumberIsNotInDVLA = false

        when (navFlowCall) {

            Constants.PAY_FOR_CROSSINGS -> {
                NewCreateAccountRequestModel.vehicleList.clear()
                binding.titleText1.visible()
                binding.titleText2.visible()
                binding.titleText3.visible()
                binding.editNumberPlate.setText(NewCreateAccountRequestModel.plateNumber)
            }

            Constants.TRANSFER_CROSSINGS -> {
                NewCreateAccountRequestModel.vehicleList.clear()
                binding.enterDetailsTxt.text =
                    getString(R.string.what_is_the_vehicle_registration_number_plate_of_the_vehicle_you_would_like_to_transfer_any_remaining_crossings_to)
            }
        }

    }

    override fun initCtrl() {
//        binding.editNumberPlate.setMaxLength(10)
        binding.editNumberPlate.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.findVehicle.setOnClickListener(this)
    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            isEnable()
        }

        override fun afterTextChanged(s: Editable?) {
//            val inputText = s.toString()
//            val capitalizedText = inputText.capitalize() // Capitalize the text
//
//            // Remove the TextWatcher to prevent infinite loop
//            binding.editNumberPlate.editText.removeTextChangedListener(this)
//
//            // Set the capitalized text back to the EditText
//            binding.editNumberPlate.editText.setText(capitalizedText)
//
//            // Move the cursor to the end of the text
//            binding.editNumberPlate.editText.setSelection(capitalizedText.length)
//
//            // Add the TextWatcher back
//            binding.editNumberPlate.editText.addTextChangedListener(this)
        }
    }


    private fun isEnable() {
        val entered_numberplate =
            binding.editNumberPlate.editText.text.toString().trim().replace("-", "")
        if (entered_numberplate.isEmpty()) {
            binding.findVehicle.isEnabled = false
            binding.editNumberPlate.removeError()
        } else {
            if (entered_numberplate.trim().last().toString() == "." || entered_numberplate.first()
                    .toString() == "."
            ) {
                binding.editNumberPlate.setErrorText(resources.getString(R.string.str_vehicle_registration))
                binding.findVehicle.isEnabled = false
            } else if (Utils.hasSpecialCharacters(
                    entered_numberplate.replace(" ", ""),
                    splCharsVehicleRegistration
                )
            ) {
                binding.editNumberPlate.setErrorText(resources.getString(R.string.str_vehicle_registration))
                binding.findVehicle.isEnabled = false
            } else if (binding.editNumberPlate.getText().toString().trim().length > 10) {
                binding.editNumberPlate.setErrorText(requireActivity().resources.getString(R.string.vehicle_registration_number_plate_error))
                binding.findVehicle.isEnabled = false
            } else {
                binding.editNumberPlate.removeError()
                binding.findVehicle.isEnabled = true
            }
        }

    }

    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.findOneOffVehicleLiveData, ::apiResponseDVRM1)
//            observe(viewModel.findVehicleLiveData, ::apiResponseDVRM1)
            observe(viewModel.findVehiclePlateLiveData, ::apiResponsePlateInfo)

            observe(viewModel.findNewVehicleLiveData, ::apiResponseDVRM)
            observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
        }
        isViewCreated = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.findVehicle -> {
                isClicked = true

                val editCall = navFlowCall.equals(Constants.EDIT_SUMMARY, true)

                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                bundle.putString(
                    Constants.PLATE_NUMBER,
                    binding.editNumberPlate.editText.text.toString()
                )

                if (plateNumber.isNotEmpty() && plateNumber == binding.editNumberPlate.editText.text
                        .toString().trim() && isCrossingCall.not()
                ) {
                    if (editCall) {
                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_accountSummaryFragment,
                            bundle
                        )
                    } else {
                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_vehicleListFragment,
                            bundle
                        )
                    }
                    return
                }

                binding.findVehicle.isEnabled = false
                val numberPlate =
                    binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                        .replace("-", "")
                NewCreateAccountRequestModel.plateNumber = numberPlate

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.findVehicle.isEnabled = true
                }, time)


                val addedVehicleList = NewCreateAccountRequestModel.addedVehicleList
                var isVehicleExist = false
                for (obj in addedVehicleList) {
                    if (obj?.plateInfo?.number.equals(numberPlate, true)) {
                        isVehicleExist = true
                    }
                }
                Log.e("TAG", "onClick: isVehicleExist " + isVehicleExist)
                if (isVehicleExist) {
                    NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal = true
                    val bundleData = Bundle()
                    bundleData.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundleData.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                    bundleData.putString(Constants.PLATE_NUMBER, plateNumber)
                    findNavController().navigate(
                        R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                        bundleData
                    )
                } else {
                    val vehicleList = NewCreateAccountRequestModel.vehicleList
                    val size = addedVehicleList.size + vehicleList.size

                    if (navFlowCall.equals(Constants.VEHICLE_MANAGEMENT)) {
                        val accountType =
                            HomeActivityMain.accountDetailsData?.accountInformation?.accountType

                        if (accountType == Constants.BUSINESS_ACCOUNT &&
                            ((size >= BuildConfig.BUSINESS.toInt()) || vehicleList.size >= 10)
                        ) {
                            NewCreateAccountRequestModel.isMaxVehicleAdded = true
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                        } else if (accountType != Constants.BUSINESS_ACCOUNT &&
                            ((size >= BuildConfig.PERSONAL.toInt()) || vehicleList.size >= 10)
                        ) {
                            NewCreateAccountRequestModel.isMaxVehicleAdded = true
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                        } else {
                            checkVehicle(numberPlate)
                        }
                    } else {
                        if (NewCreateAccountRequestModel.personalAccount && size >= BuildConfig.PERSONAL.toInt()) {
                            NewCreateAccountRequestModel.isMaxVehicleAdded = true
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                        } else if (!NewCreateAccountRequestModel.personalAccount && size >= BuildConfig.BUSINESS.toInt()) {
                            NewCreateAccountRequestModel.isMaxVehicleAdded = true
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                        } else {
                            checkVehicle(numberPlate)
                        }
                    }
                }


            }
        }
    }

    private fun checkVehicle(numberPlate: String) {

        if (isCrossingCall) {
            if (edit_summary) {
                if (data?.plateNo.equals(
                        binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                            .replace("-", "")
                    )
                ) {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAY_FOR_CROSSINGS)
                    bundle.putParcelable(
                        Constants.NAV_DATA_KEY,
                        (navData as CrossingDetailsModelsResponse) as Parcelable?
                    )

                    bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)


                    findNavController().navigate(
                        R.id.action_findVehicleFragment_to_crossingCheckAnswersFragment,
                        bundle
                    )
                } else {
                    loader?.show(
                        requireActivity().supportFragmentManager,
                        Constants.LOADER_DIALOG
                    )
                    getOneoffApi()
                }
            } else {
                loader?.show(
                    requireActivity().supportFragmentManager,
                    Constants.LOADER_DIALOG
                )
                getOneoffApi()
            }
        } else {
            loader?.show(
                requireActivity().supportFragmentManager,
                Constants.LOADER_DIALOG
            )
            if (navFlowCall.equals(Constants.TRANSFER_CROSSINGS, true)) {
                viewModel.getVehiclePlateData(numberPlate.uppercase(), Constants.AGENCY_ID.toInt())
            } else {
                checkForDuplicateVehicle(numberPlate)
            }
        }
    }

    private fun getOneoffApi() {
        viewModel.getOneOffVehicleData(
            binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                .replace("-", "").uppercase(),
            Constants.AGENCY_ID.toInt()
        )
    }

    private fun apiResponseDVRM1(resource: Resource<ArrayList<NewVehicleInfoDetails>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        val accountData = NewCreateAccountRequestModel
        val vehicleList = accountData.vehicleList
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    val bundle = Bundle()

                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putString(
                        Constants.PLATE_NUMBER,
                        binding.editNumberPlate.editText.text.toString()
                    )

                    if (it.size > 0) {

                        if (it[0].vehicleClass.equals("E", true) == true) {
                            Log.e("TAG", "apiResponseDVRM1: E " + navFlowCall)
                            NewCreateAccountRequestModel.isExempted = true
                            bundle.putParcelable(Constants.VEHICLE_DETAIL, it[0])
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )

                        } else if (it[0].vehicleClass.equals("A", true) == true) {
                            Log.e("TAG", "apiResponseDVRM1: A ")
                            NewCreateAccountRequestModel.isRucEligible = true
                            if (it.isNotEmpty()) {
                                bundle.putParcelable(
                                    Constants.VEHICLE_DETAIL,
                                    it[0]
                                )
                            }
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )

                        } else {
                            Log.e("TAG", "apiResponseDVRM1: Else ")
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            data?.apply {
                                isExempted = it[0].isExempted
                                isRUCEligible = it[0].isRUCEligible
                                plateCountry = it[0].plateCountry
                                vehicleColor = it[0].vehicleColor
                                vehicleClass = it[0].vehicleClass
                                vehicleMake = it[0].vehicleMake
                                vehicleModel = it[0].vehicleModel
                                plateNo = binding.editNumberPlate.editText.text.toString()
                            }
                            bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                            arguments?.getInt(Constants.VEHICLE_INDEX)
                                ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                            if(!binding.editNumberPlate.getText().toString().trim().replace(" ", "")
                                    .replace("-", "").equals(NewCreateAccountRequestModel.plateNumber)){
                                NewCreateAccountRequestModel.referenceId= ""
                                NewCreateAccountRequestModel.emailAddress= ""
                                NewCreateAccountRequestModel.mobileNumber= ""
                                NewCreateAccountRequestModel.countryCode= ""
                                NewCreateAccountRequestModel.telephoneNumber= ""
                                NewCreateAccountRequestModel.telephone_countryCode=""
                                NewCreateAccountRequestModel.communicationTextMessage=false
                                NewCreateAccountRequestModel.termsCondition=false
                                NewCreateAccountRequestModel.twoStepVerification=false
                                NewCreateAccountRequestModel.personalAccount=false
                                NewCreateAccountRequestModel.firstName=""
                                NewCreateAccountRequestModel.lastName=""
                                NewCreateAccountRequestModel.companyName=""
                                NewCreateAccountRequestModel.addressline1=""
                                NewCreateAccountRequestModel.addressline2=""
                                NewCreateAccountRequestModel.townCity=""
                                NewCreateAccountRequestModel.state=""
                                NewCreateAccountRequestModel.country=""
                                NewCreateAccountRequestModel.zipCode=""
                                NewCreateAccountRequestModel.selectedAddressId=-1
                                NewCreateAccountRequestModel.prePay=false
                                NewCreateAccountRequestModel.plateCountry=""
                                NewCreateAccountRequestModel.plateNumber=""
                                NewCreateAccountRequestModel.plateNumberIsNotInDVLA=false
                                NewCreateAccountRequestModel.vehicleList = mutableListOf<NewVehicleInfoDetails>()
                                NewCreateAccountRequestModel.addedVehicleList = ArrayList<VehicleResponse?>()
                                NewCreateAccountRequestModel.addedVehicleList2 = ArrayList<VehicleResponse?>()
                                NewCreateAccountRequestModel.isRucEligible=false
                                NewCreateAccountRequestModel.isExempted=false
                                NewCreateAccountRequestModel.isVehicleAlreadyAdded=false
                                NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal=false
                                NewCreateAccountRequestModel.isMaxVehicleAdded=false
                                NewCreateAccountRequestModel.isManualAddress = false
                                NewCreateAccountRequestModel.emailSecurityCode=""
                                NewCreateAccountRequestModel.smsSecurityCode=""
                                NewCreateAccountRequestModel.password=""
                            }
                            NewCreateAccountRequestModel.plateNumber =
                                binding.editNumberPlate.getText().toString().trim().replace(" ", "")
                                    .replace("-", "")
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_businessVehicleDetailFragment,
                                bundle
                            )
                        }
                    }

                }
            }

            is Resource.DataError -> {
                Log.e("TAG", "apiResponseDVRM1: errorCode 11-> " + resource.errorModel?.errorCode)
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    var isVehicleExist = false
                    val numberPlate =
                        binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                            .replace("-", "")
                    for (obj in vehicleList) {
                        if (obj.plateNumber.equals(numberPlate, true)) {
                            isVehicleExist = true
                        }
                    }
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.PLATE_NUMBER,
                        binding.editNumberPlate.editText.text.toString()
                    )

                    if (navData == null) {
                        navData =
                            CrossingDetailsModelsResponse(plateNo = binding.editNumberPlate.editText.text.toString())
                    }
                    bundle.putParcelable(
                        Constants.NAV_DATA_KEY,
                        navData as CrossingDetailsModelsResponse
                    )
                    if (isVehicleExist) {
                        accountData.isVehicleAlreadyAddedLocal = true
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                        bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                            bundle
                        )
                    } else {
                        NewCreateAccountRequestModel.plateNumberIsNotInDVLA = true
                        bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        arguments?.getInt(Constants.VEHICLE_INDEX)
                            ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                        if(!binding.editNumberPlate.getText().toString().trim().replace(" ", "")
                                .replace("-", "").equals(NewCreateAccountRequestModel.plateNumber)){
                            NewCreateAccountRequestModel.referenceId= ""
                            NewCreateAccountRequestModel.emailAddress= ""
                            NewCreateAccountRequestModel.mobileNumber= ""
                            NewCreateAccountRequestModel.countryCode= ""
                            NewCreateAccountRequestModel.telephoneNumber= ""
                            NewCreateAccountRequestModel.telephone_countryCode=""
                            NewCreateAccountRequestModel.communicationTextMessage=false
                            NewCreateAccountRequestModel.termsCondition=false
                            NewCreateAccountRequestModel.twoStepVerification=false
                            NewCreateAccountRequestModel.personalAccount=false
                            NewCreateAccountRequestModel.firstName=""
                            NewCreateAccountRequestModel.lastName=""
                            NewCreateAccountRequestModel.companyName=""
                            NewCreateAccountRequestModel.addressline1=""
                            NewCreateAccountRequestModel.addressline2=""
                            NewCreateAccountRequestModel.townCity=""
                            NewCreateAccountRequestModel.state=""
                            NewCreateAccountRequestModel.country=""
                            NewCreateAccountRequestModel.zipCode=""
                            NewCreateAccountRequestModel.selectedAddressId=-1
                            NewCreateAccountRequestModel.prePay=false
                            NewCreateAccountRequestModel.plateCountry=""
                            NewCreateAccountRequestModel.plateNumber=""
                            NewCreateAccountRequestModel.plateNumberIsNotInDVLA=false
                            NewCreateAccountRequestModel.vehicleList = mutableListOf<NewVehicleInfoDetails>()
                            NewCreateAccountRequestModel.addedVehicleList = ArrayList<VehicleResponse?>()
                            NewCreateAccountRequestModel.addedVehicleList2 = ArrayList<VehicleResponse?>()
                            NewCreateAccountRequestModel.isRucEligible=false
                            NewCreateAccountRequestModel.isExempted=false
                            NewCreateAccountRequestModel.isVehicleAlreadyAdded=false
                            NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal=false
                            NewCreateAccountRequestModel.isMaxVehicleAdded=false
                            NewCreateAccountRequestModel.isManualAddress = false
                            NewCreateAccountRequestModel.emailSecurityCode=""
                            NewCreateAccountRequestModel.smsSecurityCode=""
                            NewCreateAccountRequestModel.password=""
                        }
                        NewCreateAccountRequestModel.plateNumber =
                            binding.editNumberPlate.getText().toString().trim().replace(" ", "")
                                .replace("-", "")
                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_addNewVehicleDetailsFragment,
                            bundle
                        )
                    }
                    if (resource.errorModel?.errorCode != 5415) {
                        ErrorUtil.showError(binding.root, resource.errorMsg)
                    }
                }
            }

            else -> {}
        }
    }

    private fun apiResponsePlateInfo(resource: Resource<GetPlateInfoResponseModel?>?) {
        val bundle = Bundle()
        bundle.putString(Constants.PLATE_NUMBER, binding.editNumberPlate.editText.text.toString())
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        val accountData = NewCreateAccountRequestModel
        val vehicleList = accountData.vehicleList
        when (resource) {
            is Resource.Success -> {
                resource.data?.let { it1 ->
                    val vehicleItem = it1[0]
                    NewCreateAccountRequestModel.plateNumberIsNotInDVLA = false
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    data?.apply {
                        isExempted = vehicleItem.isExempted
                        isRUCEligible = vehicleItem.isRUCEligible
                        plateCountry = vehicleItem.plateCountry
                        vehicleColor = vehicleItem.vehicleColor
                        vehicleClass = vehicleItem.vehicleClass
                        vehicleMake = vehicleItem.vehicleMake
                        vehicleModel = vehicleItem.vehicleModel
                        plateNo = binding.editNumberPlate.editText.text.toString()
                    }
//                        var crossingDetailsModelsResponse=CrossingDetailsModelsResponse().apply {
//                            referenceNumber = data?.referenceNumber!!
//                            plateNumber = data?.plateNumber
//                            accountActStatus= resource.data?.get(0)?.accountActStatus!!
//                            accountBalance= resource.data?.get(0)?.accountBalance!!
//                            accountNo= resource.data?.get(0)?.accountNo!!
//                            accountTypeCd=resource.data?.get(0)?.accountStatusCd!!
//                            expirationDate=resource.data?.get(0)?.expirationDate!!
//                            plateCountry=resource.data?.get(0)?.plateCountry
//                            plateNo=resource.data?.get(0)?.plateNo!!
//                            unusedTrip=resource.data?.get(0)?.unusedTrip!!
//                            vehicleClass=resource.data?.get(0)?.vehicleClass
//                        }
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    arguments?.getInt(Constants.VEHICLE_INDEX)
                        ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                    findNavController().navigate(
                        R.id.action_findVehicleFragment_to_businessVehicleDetailFragment,
                        bundle
                    )
                }

            }

            is Resource.DataError -> {
                Log.e("TAG", "apiResponseDVRM1: errorCode 22-> " + resource.errorCode)
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    var isVehicleExist = false
                    val numberPlate =
                        binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                            .replace("-", "")
                    for (obj in vehicleList) {
                        if (obj.plateNumber.equals(numberPlate, true)) {
                            isVehicleExist = true
                        }
                    }
                    bundle.putParcelable(
                        Constants.NAV_DATA_KEY,
                        data?.apply {
                            plateNo =
                                binding.editNumberPlate.editText.text.toString().trim()
                                    .replace(" ", "")
                                    .replace("-", "")
                        }
                    )
                    if (isVehicleExist) {
                        accountData.isVehicleAlreadyAddedLocal = true
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                        bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                            bundle
                        )
                    } else {
                        NewCreateAccountRequestModel.plateNumberIsNotInDVLA = true
                        bundle.putString(
                            Constants.OLD_PLATE_NUMBER,
                            binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                                .replace("-", "")
                        )
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        arguments?.getInt(Constants.VEHICLE_INDEX)
                            ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_addNewVehicleDetailsFragment,
                            bundle
                        )
                    }
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {}
        }
    }

    private fun apiResponseDVRM(resource: Resource<List<NewVehicleInfoDetails?>?>) {
        Log.d("response how many time", "two times")
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        val accountData = NewCreateAccountRequestModel
        val vehicleList = accountData.vehicleList
        if (isClicked) {
            when (resource) {
                is Resource.Success -> {

                    resource.data?.let { apiData ->
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putString(
                            Constants.PLATE_NUMBER,
                            binding.editNumberPlate.editText.text.toString()
                        )

                        Log.d("responseData", Gson().toJson(apiData))

                        if (vehicleList.contains(apiData[0]) && isCrossingCall.not()) {
                            Log.e("TAG", "apiResponseDVRM: 11 ")
                            accountData.isVehicleAlreadyAddedLocal = true
                            val bundleData = Bundle()
                            bundleData.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            apiData[0].let {
                                bundleData.putString(
                                    Constants.PLATE_NUMBER,
                                    it?.plateNumber
                                )
                            }
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundleData
                            )
                            return
                        }

                        if (apiData[0]?.isExempted?.equals("Y", true) == true) {
                            Log.e("TAG", "apiResponseDVRM: 22 ")
                            NewCreateAccountRequestModel.isExempted = true
                            bundle.putParcelable(Constants.VEHICLE_DETAIL, apiData[0])
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                            return
                        }

                        if (apiData[0]?.isRUCEligible?.equals("Y", true) == true) {
                            Log.e("TAG", "apiResponseDVRM: 33 ")
                            if (apiData.isNotEmpty()) {
                                bundle.putParcelable(
                                    Constants.VEHICLE_DETAIL,
                                    apiData[0]
                                )
                            }
                            bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                            arguments?.getInt(Constants.VEHICLE_INDEX)
                                ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                            if (navData == null) {
                                navData =
                                    CrossingDetailsModelsResponse(plateNo = binding.editNumberPlate.editText.text.toString())
                            }
                            bundle.putParcelable(
                                Constants.NAV_DATA_KEY,
                                navData as CrossingDetailsModelsResponse
                            )
                            findNavController().navigate(
                                R.id.action_findYourVehicleFragment_to_businessVehicleDetailFragment,
                                bundle
                            )
                        } else if (apiData[0]?.isRUCEligible?.equals("N", true) == true) {
                            Log.e("TAG", "apiResponseDVRM: 44 ")
                            NewCreateAccountRequestModel.isRucEligible = true
                            if (apiData.isNotEmpty()) {
                                bundle.putParcelable(
                                    Constants.VEHICLE_DETAIL,
                                    apiData[0]
                                )
                            }
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                            return
                        }


                    }


                }

                is Resource.DataError -> {
                    Log.e("TAG", "apiResponseDVRM1: errorCode 33-> " + resource.errorCode)
                    if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                        displaySessionExpireDialog()
                    } else {
                        var isVehicleExist = false
                        val numberPlate =
                            binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                                .replace("-", "")
                        for (obj in vehicleList) {
                            if (obj.plateNumber.equals(numberPlate, true)) {
                                isVehicleExist = true
                            }
                        }
                        val bundle = Bundle()
                        if (navData == null) {
                            navData =
                                CrossingDetailsModelsResponse(plateNo = binding.editNumberPlate.editText.text.toString())
                        }
                        bundle.putParcelable(
                            Constants.NAV_DATA_KEY,
                            navData as CrossingDetailsModelsResponse
                        )
                        if (isVehicleExist) {
                            accountData.isVehicleAlreadyAddedLocal = true
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                        } else {
                            NewCreateAccountRequestModel.plateNumberIsNotInDVLA = true
                            bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            arguments?.getInt(Constants.VEHICLE_INDEX)
                                ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_addNewVehicleDetailsFragment,
                                bundle
                            )
                        }
                    }
                }

                else -> {
                }
            }
        }

        isClicked = false


    }

    private fun checkForDuplicateVehicle(plateNumber: String) {

        val vehicleValidReqModel = ValidVehicleCheckRequest(
            plateNumber.uppercase(), "UK", "STANDARD",
            "2022", "model", "make", "colour", "2", "HE"
        )
        viewModel.validVehicleCheck(vehicleValidReqModel, Constants.AGENCY_ID.toInt())

    }

    private fun apiResponseValidVehicle(resource: Resource<String?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {

                viewModel.getNewVehicleData(
                    binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                        .replace("-", "").uppercase(),
                    Constants.AGENCY_ID.toInt()
                )


            }

            is Resource.DataError -> {
                Log.e("TAG", "apiResponseDVRM1: errorCode 44-> " + resource.errorCode)
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    val numberPlate =
                        binding.editNumberPlate.editText.text.toString().trim().replace(" ", "")
                            .replace("-", "")
                    NewCreateAccountRequestModel.plateNumber = numberPlate
                    NewCreateAccountRequestModel.isVehicleAlreadyAdded = true
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                    findNavController().navigate(
                        R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                        bundle
                    )
                }
            }

            else -> {
            }
        }
    }

}