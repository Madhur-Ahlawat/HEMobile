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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.GetPlateInfoResponseModel
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentCreateAccountFindVehicleBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.splCharsVehicleRegistration
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.setAccessibilityDelegateForDigits
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(),
    View.OnClickListener {
    private var data: CrossingDetailsModelsResponse? = null
    private var isViewCreated = false
    private var plateNumber = ""
    private var oldPlateNumber = ""
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var time = (1 * 1000).toLong()
    private var isPayForCrossingFlow = false
    private var isClicked: Boolean = false
    private var editVehicle: Boolean = false
    private var totalVehicleCount: Int = -1

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.containsKey(Constants.EDIT_VEHICLE) == true) {
            editVehicle = arguments?.getBoolean(Constants.EDIT_VEHICLE) ?: false
        }
        if (arguments?.containsKey(Constants.COUNT) == true) {
            totalVehicleCount = arguments?.getInt(Constants.COUNT) ?: 0
        }
        isPayForCrossingFlow = navFlowCall.equals(Constants.PAY_FOR_CROSSINGS, true)
        if (NewCreateAccountRequestModel.oneOffVehiclePlateNumber.isNotEmpty()) {
            binding.editNumberPlate.editText.setText(NewCreateAccountRequestModel.oneOffVehiclePlateNumber)
            binding.findVehicle.enable()
        }

        if (arguments?.containsKey(Constants.PLATE_NUMBER) == true) {
            arguments?.getString(Constants.PLATE_NUMBER, "").toString()
                .let { plateNumber = it.replace("null", "") }
            arguments?.getString(Constants.PLATE_NUMBER, "").toString()
                .let { oldPlateNumber = it.replace("null", "") }
            if (plateNumber.isNotEmpty() && oldPlateNumber.isEmpty()) {
                oldPlateNumber = plateNumber
            }
        }
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        if (data == null) {
            data = CrossingDetailsModelsResponse()
        }

        binding.editNumberPlate.editText.filters = arrayOf(InputFilter.LengthFilter(10))
        binding.editNumberPlate.editText.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        if (plateNumber.isNotEmpty()) {
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
        binding.cancelBt.gone()

        when (navFlowCall) {

            Constants.PAY_FOR_CROSSINGS -> {
                NewCreateAccountRequestModel.vehicleList.clear()
                binding.titleText1.visible()
                binding.youcanTv.visible()
                binding.titleText2.visible()
                binding.titleText3.visible()
                binding.editNumberPlate.setText(NewCreateAccountRequestModel.plateNumber)
            }

            Constants.TRANSFER_CROSSINGS -> {
                NewCreateAccountRequestModel.vehicleList.clear()
                binding.enterDetailsTxt.text =
                    getString(R.string.what_is_the_vehicle_registration_number_plate_of_the_vehicle_you_would_like_to_transfer_any_remaining_crossings_to)
                binding.cancelBt.visible()
            }
        }

        binding.editNumberPlate.editText.setAccessibilityDelegateForDigits()


    }

    override fun initCtrl() {
        binding.editNumberPlate.editText.addTextChangedListener(GenericTextWatcher())
        binding.findVehicle.setOnClickListener(this)
        binding.cancelBt.setOnClickListener(this)
    }

    inner class GenericTextWatcher : TextWatcher {
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
        val enteredNumberplate =
            binding.editNumberPlate.editText.text.toString().trim().replace("-", "")
        if (enteredNumberplate.isEmpty()) {
            binding.findVehicle.isEnabled = false
            binding.editNumberPlate.removeError()
        } else {
            if (enteredNumberplate.trim().last().toString() == "." || enteredNumberplate.first()
                    .toString() == "."
            ) {
                binding.editNumberPlate.setErrorText(resources.getString(R.string.str_vehicle_registration))
                binding.findVehicle.isEnabled = false
            } else if (Utils.hasSpecialCharacters(
                    enteredNumberplate.replace(" ", ""),
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
            observe(viewModel.findVehiclePlateLiveData, ::apiResponsePlateInfo)

            observe(viewModel.findNewVehicleLiveData, ::apiResponseDVRM)
            observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
            observe(viewModel.heartBeatLiveData, ::heartBeatApiResponse)

        }
        isViewCreated = true
    }

    private fun heartBeatApiResponse(resource: Resource<EmptyApiResponse?>?) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancel_bt -> {
                findNavController().popBackStack()
            }

            R.id.findVehicle -> {
                emailHeartBeatApi()
                smsHeartBeatApi()
                NewCreateAccountRequestModel.oneOffVehiclePlateNumber = ""
                isClicked = true
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                bundle.putString(
                    Constants.PLATE_NUMBER,
                    binding.editNumberPlate.editText.text.toString()
                )
                if (plateNumber.isNotEmpty() && plateNumber == binding.editNumberPlate.editText.text
                        .toString().trim() && isPayForCrossingFlow.not()
                ) {
                    if (edit_summary) {
                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_accountSummaryFragment,
                            bundle
                        )
                    } else {
                        if (editVehicle || navFlowCall == Constants.TRANSFER_CROSSINGS) {
                            val numberPlate =
                                binding.editNumberPlate.editText.text.toString().trim()
                                    .replace(" ", "")
                                    .replace("-", "")
                            checkVehicle(numberPlate)
                        } else {
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_vehicleListFragment,
                                bundle
                            )
                        }
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
                    if (obj?.plateInfo?.number?.replace(" ", "")?.lowercase()?.trim()
                            .equals(numberPlate.replace(" ", "").lowercase().trim(), true)
                    ) {
                        isVehicleExist = true
                    }
                }

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
                    var vehicleAddedCount = NewCreateAccountRequestModel.addedVehicleList.size
                    if (totalVehicleCount != -1) {
                        vehicleAddedCount = totalVehicleCount
                    }
                    val vehicleList = NewCreateAccountRequestModel.vehicleList
                    val size = vehicleAddedCount + vehicleList.size
                    if (navFlowCall == Constants.VEHICLE_MANAGEMENT) {
                        val accountType =
                            HomeActivityMain.accountDetailsData?.accountInformation?.accountType
                        val accSubType =
                            HomeActivityMain.accountDetailsData?.accountInformation?.accSubType
                        if (accSubType == Constants.EXEMPT_PARTNER) {
                            if ((size >= BuildConfig.EXEMPT.toInt()) || vehicleList.size >= 10) {
                                NewCreateAccountRequestModel.isMaxVehicleAdded = true
                                findNavController().navigate(
                                    R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                    bundle
                                )
                            } else {
                                checkVehicle(numberPlate)
                            }

                        } else {
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
        if (isPayForCrossingFlow) {
            if (edit_summary) {
                if (oldPlateNumber.uppercase() == binding.editNumberPlate.editText.text.toString()
                        .trim().replace(" ", "")
                        .replace("-", "").uppercase()
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

            if (navFlowCall.equals(Constants.TRANSFER_CROSSINGS, true)) {
                if (oldPlateNumber == binding.editNumberPlate.editText.text.toString().trim()) {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putParcelable(
                        Constants.NAV_DATA_KEY,
                        data
                    )

                    findNavController().navigate(
                        R.id.action_findVehicleFragment_to_confirmNewVehicleDetailsCheckPaidCrossingsFragment,
                        bundle
                    )
                } else {
                    loader?.show(
                        requireActivity().supportFragmentManager,
                        Constants.LOADER_DIALOG
                    )
                    viewModel.getVehiclePlateData(
                        numberPlate.uppercase(),
                        Constants.AGENCY_ID.toInt()
                    )

                }

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

    private fun apiResponseDVRM1(resource: Resource<List<NewVehicleInfoDetails>?>?) {
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

                        if (it[0].vehicleClass.equals("E", true)) {
                            NewCreateAccountRequestModel.isExempted = true
                            bundle.putParcelable(Constants.VEHICLE_DETAIL, it[0])
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )

                        } else if (it[0].vehicleClass.equals("A", true)) {
                            NewCreateAccountRequestModel.isRucEligible = true
                            if (it.isNotEmpty()) {
                                bundle.putParcelable(
                                    Constants.VEHICLE_DETAIL,
                                    it[0]
                                )
                            }
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )

                        } else {
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            data?.apply {
                                isExempted = it[0].isExempted
                                isRUCEligible = it[0].isRUCEligible
                                plateCountry = it[0].plateCountry
                                vehicleColor = it[0].vehicleColor
                                vehicleClass = it[0].vehicleClass
                                vehicleMake = it[0].vehicleMake
                                vehicleModel = it[0].vehicleModel
                                vehicleType = Utils.getVehicleType(
                                    requireActivity(),
                                    it[0].vehicleClass ?: ""
                                )
                                plateNo = binding.editNumberPlate.editText.text.toString()
                            }
                            bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                            arguments?.getInt(Constants.VEHICLE_INDEX)
                                ?.let { it1 -> bundle.putInt(Constants.VEHICLE_INDEX, it1) }
                            if (binding.editNumberPlate.getText().toString().trim()
                                    .replace(" ", "")
                                    .replace("-", "") != NewCreateAccountRequestModel.plateNumber
                            ) {
                                NewCreateAccountRequestModel.referenceId = ""
                                NewCreateAccountRequestModel.emailAddress = ""
                                NewCreateAccountRequestModel.mobileNumber = ""
                                NewCreateAccountRequestModel.countryCode = ""
                                NewCreateAccountRequestModel.telephoneNumber = ""
                                NewCreateAccountRequestModel.telephone_countryCode = ""
                                NewCreateAccountRequestModel.communicationTextMessage = false
                                NewCreateAccountRequestModel.termsCondition = false
                                NewCreateAccountRequestModel.twoStepVerification = false
                                NewCreateAccountRequestModel.personalAccount = false
                                NewCreateAccountRequestModel.firstName = ""
                                NewCreateAccountRequestModel.lastName = ""
                                NewCreateAccountRequestModel.companyName = ""
                                NewCreateAccountRequestModel.addressLine1 = ""
                                NewCreateAccountRequestModel.addressLine2 = ""
                                NewCreateAccountRequestModel.townCity = ""
                                NewCreateAccountRequestModel.state = ""
                                NewCreateAccountRequestModel.country = ""
                                NewCreateAccountRequestModel.zipCode = ""
                                NewCreateAccountRequestModel.selectedAddressId = -1
                                // NewCreateAccountRequestModel.prePay = false
                                NewCreateAccountRequestModel.plateCountry = ""
                                NewCreateAccountRequestModel.plateNumber = ""
                                NewCreateAccountRequestModel.plateNumberIsNotInDVLA = false
                                NewCreateAccountRequestModel.vehicleList =
                                    mutableListOf()
                                NewCreateAccountRequestModel.addedVehicleList =
                                    ArrayList()
                                NewCreateAccountRequestModel.addedVehicleList2 =
                                    ArrayList()
                                NewCreateAccountRequestModel.isRucEligible = false
                                NewCreateAccountRequestModel.isExempted = false
                                NewCreateAccountRequestModel.isVehicleAlreadyAdded = false
                                NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal = false
                                NewCreateAccountRequestModel.isMaxVehicleAdded = false
                                NewCreateAccountRequestModel.isManualAddress = false
                                NewCreateAccountRequestModel.emailSecurityCode = ""
                                NewCreateAccountRequestModel.smsSecurityCode = ""
                                NewCreateAccountRequestModel.password = ""
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
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
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
                    bundle.putBoolean(Constants.EDIT_SUMMARY, edit_summary)

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
                        bundle.putBoolean(Constants.EDIT_SUMMARY, edit_summary)
                        arguments?.getInt(Constants.VEHICLE_INDEX)
                            ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                        if (binding.editNumberPlate.getText().toString().trim().replace(" ", "")
                                .replace("-", "") != NewCreateAccountRequestModel.plateNumber
                        ) {
                            NewCreateAccountRequestModel.referenceId = ""
                            NewCreateAccountRequestModel.emailAddress = ""
                            NewCreateAccountRequestModel.mobileNumber = ""
                            NewCreateAccountRequestModel.countryCode = ""
                            NewCreateAccountRequestModel.telephoneNumber = ""
                            NewCreateAccountRequestModel.telephone_countryCode = ""
                            NewCreateAccountRequestModel.communicationTextMessage = false
                            NewCreateAccountRequestModel.termsCondition = false
                            NewCreateAccountRequestModel.twoStepVerification = false
                            NewCreateAccountRequestModel.personalAccount = false
                            NewCreateAccountRequestModel.firstName = ""
                            NewCreateAccountRequestModel.lastName = ""
                            NewCreateAccountRequestModel.companyName = ""
                            NewCreateAccountRequestModel.addressLine1 = ""
                            NewCreateAccountRequestModel.addressLine2 = ""
                            NewCreateAccountRequestModel.townCity = ""
                            NewCreateAccountRequestModel.state = ""
                            NewCreateAccountRequestModel.country = ""
                            NewCreateAccountRequestModel.zipCode = ""
                            NewCreateAccountRequestModel.selectedAddressId = -1
                            //NewCreateAccountRequestModel.prePay = false
                            NewCreateAccountRequestModel.plateCountry = ""
                            NewCreateAccountRequestModel.plateNumber = ""
                            NewCreateAccountRequestModel.plateNumberIsNotInDVLA = false
                            NewCreateAccountRequestModel.vehicleList =
                                mutableListOf()
                            NewCreateAccountRequestModel.addedVehicleList =
                                ArrayList()
                            NewCreateAccountRequestModel.addedVehicleList2 =
                                ArrayList()
                            NewCreateAccountRequestModel.isRucEligible = false
                            NewCreateAccountRequestModel.isExempted = false
                            NewCreateAccountRequestModel.isVehicleAlreadyAdded = false
                            NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal = false
                            NewCreateAccountRequestModel.isMaxVehicleAdded = false
                            NewCreateAccountRequestModel.isManualAddress = false
                            NewCreateAccountRequestModel.emailSecurityCode = ""
                            NewCreateAccountRequestModel.smsSecurityCode = ""
                            NewCreateAccountRequestModel.password = ""
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
        bundle.putBoolean(Constants.EDIT_SUMMARY, edit_summary)

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
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    arguments?.getInt(Constants.VEHICLE_INDEX)
                        ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }

                    if (vehicleItem.isExempted.lowercase() == "y") {
                        NewCreateAccountRequestModel.isExempted = true
                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                            bundle
                        )

                    } else {
                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_businessVehicleDetailFragment,
                            bundle
                        )
                    }
                }

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
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
                    if (isVehicleExist) {
                        bundle.putParcelable(
                            Constants.NAV_DATA_KEY,
                            data?.apply {
                                plateNo =
                                    binding.editNumberPlate.editText.text.toString().trim()
                                        .replace(" ", "")
                                        .replace("-", "")
                            }
                        )

                        accountData.isVehicleAlreadyAddedLocal = true
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                        bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                        bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)

                        findNavController().navigate(
                            R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                            bundle
                        )
                    } else {
                        bundle.putParcelable(
                            Constants.NAV_DATA_KEY,
                            data?.apply {
                                plateNo =
                                    binding.editNumberPlate.editText.text.toString().trim()
                                        .replace(" ", "")
                                        .replace("-", "")

                                vehicleColor = ""
                                vehicleMake = ""
                                vehicleClass = ""
                                vehicleModel = ""
                                vehicleType = ""
                            }
                        )

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

                }
            }

            else -> {}
        }
    }

    private fun apiResponseDVRM(resource: Resource<List<NewVehicleInfoDetails?>?>) {
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

                        if (vehicleList.contains(apiData[0]) && isPayForCrossingFlow.not()) {
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
                            NewCreateAccountRequestModel.isExempted = true
                            bundle.putParcelable(Constants.VEHICLE_DETAIL, apiData[0])
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
                            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                            return
                        }

                        if (apiData[0]?.isRUCEligible?.equals("Y", true) == true) {
                            NewCreateAccountRequestModel.isRucEligible = false
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
                                    CrossingDetailsModelsResponse(
                                        plateNo = binding.editNumberPlate.editText.text.toString(),
                                        vehicleClass = apiData[0]?.vehicleClass
                                    )
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
                            NewCreateAccountRequestModel.isRucEligible = true
                            if (apiData.isNotEmpty()) {
                                bundle.putParcelable(
                                    Constants.VEHICLE_DETAIL,
                                    apiData[0]
                                )
                            }
                            bundle.putString(Constants.NAV_FLOW_FROM, Constants.FIND_VEHICLE)
//                            findNavController().navigate(
//                                R.id.action_findYourVehicleFragment_to_businessVehicleDetailFragment,
//                                bundle
//                            )
                            findNavController().navigate(
                                R.id.action_findVehicleFragment_to_maximumVehicleFragment,
                                bundle
                            )
                            return
                        }


                    }


                }

                is Resource.DataError -> {
                    if (checkSessionExpiredOrServerError(resource.errorModel)
                    ) {
                        displaySessionExpireDialog(resource.errorModel)
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
                        bundle.putBoolean(Constants.EDIT_SUMMARY, edit_summary)

                        if (navData == null) {
                            navData =
                                CrossingDetailsModelsResponse(
                                    plateNo = binding.editNumberPlate.editText.text.toString(),
                                    vehicleMake = "",
                                    vehicleClass = "",
                                    vehicleColor = "",
                                    vehicleModel = "",
                                    vehicleType = ""
                                )
                        } else {
                            if (navData is CrossingDetailsModelsResponse) {
                                (navData as CrossingDetailsModelsResponse).vehicleMake = ""
                                (navData as CrossingDetailsModelsResponse).vehicleClass = ""
                                (navData as CrossingDetailsModelsResponse).vehicleColor = ""
                                (navData as CrossingDetailsModelsResponse).vehicleModel = ""
                                (navData as CrossingDetailsModelsResponse).vehicleType = ""
                            }
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
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
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