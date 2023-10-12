package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentNewAddVehicleDetailsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.MakeOneOfPaymentViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasDigits
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment<FragmentNewAddVehicleDetailsBinding>(),
    View.OnClickListener, DropDownItemSelectListener {

    private var mScreeType = 0
    private var mVehicleDetails: VehicleResponse? = null
    private var typeOfVehicle: MutableList<String> = ArrayList()
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null
    private var radioButtonChecked: Boolean = false
    private var typeOfVehicleChecked: Boolean = false
    private var checkBoxChecked: Boolean = false
    private var vehicleClassSelected = ""
    private var oldPlateNumber = ""
    private var vehicleList: MutableList<NewVehicleInfoDetails>? = null
    private var accountData: NewCreateAccountRequestModel? = null
    private var data: CrossingDetailsModelsResponse? = null
    private var makeInputCheck: Boolean = false
    private var modelInputCheck: Boolean = false
    private var colourInputCheck: Boolean = false
    private var vehcileRegRequired: Boolean = true
    private val viewModel: MakeOneOfPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager
    private var isViewCreated = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.getCrossingDetails, ::getUnSettledCrossings)
        }
        isViewCreated = true
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun init() {

        typeOfVehicle.clear()
        typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_A))
        typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_B))
        typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_C))
        typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_D))
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)

        binding.apply {
            typeVehicle.dataSet.addAll(typeOfVehicle)
            modelInputLayout.editText.filters = arrayOf<InputFilter>(LengthFilter(50))
            makeInputLayout.editText.filters = arrayOf<InputFilter>(LengthFilter(50))
            colorInputLayout.editText.filters = arrayOf<InputFilter>(LengthFilter(50))
        }
        oldPlateNumber = arguments?.getString(Constants.OLD_PLATE_NUMBER, "").toString()

        navData?.let {
            try {
                data = it as CrossingDetailsModelsResponse
            } catch (e: Exception) {
                mVehicleDetails = it as VehicleResponse
            }
        }
        if (data == null) {
            data = CrossingDetailsModelsResponse()
        }
        Log.e("TAG", "setBtnActivated: plateCountry--?  " + data?.plateCountry)
        binding.vehicleRegTv.editText.filters = arrayOf(LengthFilter(10))
        binding.vehicleRegTv.editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS

        binding.vehicleRegTv.setText(data?.plateNo.toString())

        accountData = NewCreateAccountRequestModel
        vehicleList = accountData?.vehicleList
        if (oldPlateNumber.isNotEmpty()) {
            val index = arguments?.getInt(Constants.VEHICLE_INDEX)
            val isDblaAvailable = arguments?.getBoolean(Constants.IS_DBLA_AVAILABLE, true)
            if (isDblaAvailable != null) {
                if (isDblaAvailable.not()) {
                    nonUKVehicleModel = index?.let { vehicleList?.get(it) }
                    updateView(
                        nonUKVehicleModel?.plateNumber ?: "",
                        nonUKVehicleModel?.vehicleMake ?: "",
                        nonUKVehicleModel?.vehicleColor ?: "",
                        nonUKVehicleModel?.vehicleClass ?: "",
                        nonUKVehicleModel?.vehicleModel ?: "",
                        nonUKVehicleModel?.isUK ?: false
                    )
                }
            }
        }
        setPreSelectedVehicleType()
        binding.typeVehicle.dropDownItemSelectListener = this
        binding.model = false
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            radioButtonChecked = R.id.radioButtonYes == checkedId || R.id.radioButtonNo == checkedId

            data?.veicleUKnonUK = radioButtonChecked

            checkButton()
        }

        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            checkBoxChecked = isChecked
            validateAllFields()
            checkButton()
        }



        AdobeAnalytics.setScreenTrack(
            "one of  payment:vehicle details manual entry",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of  payment:vehicle details manual entry",
            sessionManager.getLoggedInUser()
        )

        binding.editVehicle.setOnClickListener(this)


        if (NewCreateAccountRequestModel.plateNumberIsNotInDVLA && NewCreateAccountRequestModel.plateNumber.isNotEmpty()) {
            binding.vehiclePlateNumber.text = NewCreateAccountRequestModel.plateNumber
            binding.vehicleRegisteredLayout.visible()
        } else {
            binding.vehicleRegisteredLayout.gone()
            radioButtonChecked = true

            when (navFlowCall) {
                Constants.TRANSFER_CROSSINGS -> {
                    binding.vehiclePlateNumber.text = data?.plateNo.toString()
                }

                Constants.PAY_FOR_CROSSINGS -> {
                    updateView(
                        data?.plateNo.toString(), data?.vehicleMake ?: "",
                        data?.vehicleColor ?: "", data?.vehicleClass ?: "",
                        data?.vehicleModel ?: "", data?.veicleUKnonUK ?: false
                    )
                    binding.vehiclePlateNumber.text = data?.plateNo.toString()

                }

                else -> {
                    updateView(
                        nonUKVehicleModel?.plateNumber ?: "",
                        nonUKVehicleModel?.vehicleMake ?: "",
                        nonUKVehicleModel?.vehicleColor ?: "",
                        nonUKVehicleModel?.vehicleClass ?: "",
                        nonUKVehicleModel?.vehicleModel ?: "",
                        nonUKVehicleModel?.isUK ?: false
                    )
                }
            }



            if (NewCreateAccountRequestModel.plateCountry == Constants.COUNTRY_TYPE_UK) {
                if (data?.vehicleClass == "D") {
                    binding.typeVehicle.visible()
                } else {
                    binding.typeVehicle.gone()
                }
                binding.vehicleRegTv.gone()
                binding.cardLayout.visible()
            } else {
                binding.typeVehicle.visible()
                binding.vehicleRegTv.visible()
                binding.cardLayout.gone()
            }
            if (NewCreateAccountRequestModel.isExempted) {
                binding.typeVehicle.visible()
            }

        }

        binding.nextBtn.setOnClickListener(this)

        when (navFlowCall) {

            Constants.PAY_FOR_CROSSINGS -> {
                NewCreateAccountRequestModel.vehicleList.clear()
                binding.apply {
                    typeVehicle.dataSet.clear()
                    typeVehicle.dataSet.addAll(typeOfVehicle)
                }
                if (NewCreateAccountRequestModel.isExempted) {
                    binding.makeInputLayout.gone()
                    binding.modelInputLayout.gone()
                    binding.colorInputLayout.gone()
                    binding.vehicleRegTv.gone()

                    binding.vehicleRegisteredLayout.gone()

                    radioButtonChecked = true
                    makeInputCheck = true
                    modelInputCheck = true
                    colourInputCheck = true
                    radioButtonChecked = true
                    checkValidation()
                }
            }

            Constants.TRANSFER_CROSSINGS -> {
                NewCreateAccountRequestModel.vehicleList.clear()
//                typeOfVehicle.clear()
//                typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_B))
//                typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_C))
//                typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_D))
                binding.apply {
                    typeVehicle.dataSet.clear()
                    typeVehicle.dataSet.addAll(typeOfVehicle)
                    vehiclePlateNumber.text = data?.plateNo.toString()
                }
            }
        }

        if (navFlowCall == Constants.EDIT_SUMMARY) {
            binding.checkBoxTerms.isChecked = true
        }
    }

    private fun setPreSelectedVehicleType() {
        if (typeOfVehicle.size > 0 && data != null && data?.plateNo?.isNotEmpty() == true) {
            binding.typeVehicle.setSelectedValue(data?.vehicleType!!)
            typeOfVehicleChecked = true
        }
    }

    private fun getUnSettledCrossings(resource: Resource<CrossingDetailsModelsResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    it.let {
                        val mUnSettledTrips = it.unSettledTrips
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putParcelable(
                            Constants.NAV_DATA_KEY,
                            resource.data.apply {
                                plateNo = binding.vehiclePlateNumber.text.toString().trim()
                                vehicleColor =
                                    data?.vehicleColor
                                vehicleMake =
                                    data?.vehicleMake
                                vehicleModel =
                                    data?.vehicleModel
                                unSettledTrips = mUnSettledTrips
                                additionalCrossingCount = it.additionalCrossingCount
                                vehicleType =
                                    data?.vehicleType
                            })

                        if (navFlowCall == Constants.PAY_FOR_CROSSINGS) {
                            if (it.customerClass != it.dvlaclass) {
                                findNavController().navigate(
                                    R.id.action_addVehicleDetailsFragment_to_vehicleDoesNotMatchCurrentVehicleFragment,
                                    bundle
                                )
                                return
                            }
                        }

                        if (mUnSettledTrips > 0) {

                            findNavController().navigate(
                                R.id.action_addVehicleDetailFragment_to_pay_for_crossingFragment,
                                bundle
                            )

                        } else {
                            findNavController().navigate(
                                R.id.action_addVehicleDetailFragment_to_additional_crossingFragment,
                                bundle
                            )
                        }

                    }
                }
            }

            is Resource.DataError -> {
                if (resource.errorCode != 5415) {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }

    }

    private fun updateView(
        plateNumber: String = "",
        vehicleMake: String = "",
        vehicleColor: String = "",
        vehicleClass: String = "",
        vehicleModel: String = "",
        isUK: Boolean = false
    ) {
        binding.vehiclePlateNumber.text = plateNumber
        binding.makeInputLayout.setText(vehicleMake)
        binding.colorInputLayout.setText(vehicleColor)
        binding.typeVehicle.setSelectedValue(
            Utils.getVehicleType(
                requireActivity(),
                vehicleClass
            )
        )
        binding.modelInputLayout.setText(vehicleModel)

        if (vehicleClass.equals("D", true) && NewCreateAccountRequestModel.plateCountry.equals(
                Constants.COUNTRY_TYPE_UK
            )
        ) {
            typeOfVehicle.clear()
            typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_C))
            typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_D))
            binding.apply {
                typeVehicle.dataSet.clear()
                typeVehicle.dataSet.addAll(typeOfVehicle)
            }
        }
        if (NewCreateAccountRequestModel.isExempted) {
            typeOfVehicle.clear()
            typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_B))
            typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_C))
            typeOfVehicle.add(requireActivity().resources.getString(R.string.vehicle_type_D))
            binding.apply {
                typeVehicle.dataSet.clear()
                typeVehicle.dataSet.addAll(typeOfVehicle)
            }
        }
        if (isUK) {
            binding.radioButtonYes.isChecked = true
        } else {
            binding.radioButtonNo.isChecked = true
        }
        typeOfVehicleChecked = true
        radioButtonChecked = true

        makeInputCheck = true
        modelInputCheck = true
        colourInputCheck = true
        checkValidation()
    }

    override fun initCtrl() {
        validateAllFields()
        navData?.let {
            if (it is CrossingDetailsModelsResponse) {
                it.apply {
                    binding.makeInputLayout.editText.setText(vehicleMake)
                    binding.modelInputLayout.editText.setText(vehicleModel)
                    binding.colorInputLayout.editText.setText(vehicleColor)
                }
            }

        }
        binding.makeInputLayout.editText.onTextChanged {
            makeInputCheck = if (it.isNotEmpty()) {
                if (it.trim().length > 50) {
                    binding.makeInputLayout.setErrorText(getString(R.string.vehicle_make_must_be_less_than_fifty))
                    false
                } else if (hasDigits(it) || hasSpecialCharacters(it, Utils.splCharVehicleMake)) {
                    binding.makeInputLayout.setErrorText(getString(R.string.str_make_error_message))
                    false
                } else {
                    validateAllFields()
                    binding.makeInputLayout.removeError()
                    data?.vehicleMake =
                        binding.makeInputLayout.editText.text.toString().trim()
                    true
                }
            } else {
                binding.makeInputLayout.setErrorText(getString(R.string.enter_the_make_of_your_vehicle))
                false
            }
            checkButton()
        }
        binding.modelInputLayout.editText.onTextChanged {
            modelInputCheck = if (it.isNotEmpty()) {
                if (it.trim().length > 50) {
                    binding.modelInputLayout.setErrorText(getString(R.string.vehicle_make_must_be_less_than_fifty))
                    false
                } else if (hasSpecialCharacters(
                        it.trim().replace(" ", ""),
                        Utils.splCharVehicleModel
                    )
                ) {
                    binding.modelInputLayout.setErrorText(getString(R.string.str_model_error_message))
                    false
                } else {
                    validateAllFields()
                    binding.modelInputLayout.removeError()
                    data?.vehicleModel =
                        binding.modelInputLayout.editText.text.toString().trim()
                    true
                }
            } else {
                binding.modelInputLayout.setErrorText(getString(R.string.enter_your_vehicle_model))
                false
            }

            checkButton()
        }
        binding.colorInputLayout.editText.onTextChanged {

            colourInputCheck = if (it.trim().isNotEmpty()) {
                if (hasDigits(it) || hasSpecialCharacters(it.trim(), Utils.splCharVehicleColor)) {
                    binding.colorInputLayout.setErrorText(getString(R.string.str_colour_error_message))
                    false
                } else {
                    validateAllFields()
                    binding.colorInputLayout.removeError()
                    data?.vehicleColor =
                        binding.colorInputLayout.editText.text.toString().trim()
                    true
                }
            } else {
                binding.colorInputLayout.setErrorText(getString(R.string.enter_your_vihicle_color))
                false
            }

            checkButton()
        }

        binding.vehicleRegTv.editText.addTextChangedListener { GenericWatcher() }

    }

    private fun GenericWatcher() {
        val entered_numberplate =
            binding.vehicleRegTv.editText.text.toString().trim().replace("-", "")
        if (entered_numberplate.isEmpty()) {
            vehcileRegRequired = false
            binding.vehicleRegTv.removeError()
        } else {
            if (entered_numberplate.last().toString() == "." || entered_numberplate.first()
                    .toString() == "."
            ) {
                binding.vehicleRegTv.setErrorText(requireActivity().resources.getString(R.string.str_vehicle_registration))
                vehcileRegRequired = false
            } else if (hasSpecialCharacters(
                    entered_numberplate.replace(" ", ""),
                    Utils.splCharsVehicleRegistration
                )
            ) {
                binding.vehicleRegTv.setErrorText(requireActivity().resources.getString(R.string.str_vehicle_registration))
                vehcileRegRequired = false
            } else if (entered_numberplate.trim().length > 10) {
                binding.vehicleRegTv.setErrorText(requireActivity().resources.getString(R.string.vehicle_registration_number_plate_error))
                vehcileRegRequired = false
            } else {
                binding.vehicleRegTv.removeError()
                vehcileRegRequired = true
            }
        }
        checkButton()
    }


    private fun validateAllFields() {
        radioButtonChecked = (binding.radioButtonYes.isChecked || binding.radioButtonNo.isChecked)
        checkBoxChecked = binding.checkBoxTerms.isChecked
        modelInputCheck = if (binding.modelInputLayout.editText.text!!.isNotEmpty()) {
            if (binding.modelInputLayout.editText.text!!.toString().trim().length > 50) {
                false
            } else if (hasSpecialCharacters(
                    binding.modelInputLayout.editText.text!!.toString().trim()
                        .replace(" ", ""), Utils.splCharVehicleModel
                )
            ) {
                false
            } else {
                binding.modelInputLayout.removeError()
                data?.vehicleModel =
                    binding.modelInputLayout.editText.text.toString().trim()
                true
            }
        } else {
            false
        }


        makeInputCheck = if (binding.makeInputLayout.editText.text.toString().isNotEmpty()) {
            if (binding.makeInputLayout.editText.text.toString().trim().length > 50) {
                false
            } else if (hasDigits(
                    binding.makeInputLayout.editText.text.toString()
                ) || hasSpecialCharacters(
                    binding.makeInputLayout.editText.text.toString(),
                    Utils.splCharVehicleMake
                )
            ) {
                false
            } else {
                binding.makeInputLayout.removeError()
                data?.vehicleMake =
                    binding.makeInputLayout.editText.text.toString().trim()
                true
            }
        } else {
            false
        }

        colourInputCheck = if (binding.colorInputLayout.editText.text.toString()
                .trim().isNotEmpty()
        ) {
            if (hasDigits(
                    binding.colorInputLayout.editText.text.toString()
                ) || hasSpecialCharacters(
                    binding.colorInputLayout.editText.text.toString().trim(),
                    Utils.splCharVehicleColor
                )
            ) {
                false
            } else {
                binding.colorInputLayout.removeError()
                data?.vehicleColor =
                    binding.colorInputLayout.editText.text.toString().trim()
                true
            }
        } else {
            false
        }
        checkButton()
    }

    private fun checkButton() {
        Log.e("TAG", "checkButton:vehicleClassSelected "+vehicleClassSelected )
        if (NewCreateAccountRequestModel.isExempted) {
            binding.typeVehicle.setSelectedValue(vehicleClassSelected)
            if (typeOfVehicleChecked && binding.checkBoxTerms.isChecked
            ) {
                setBtnActivated()
            } else {
                setBtnDisabled()
            }
        } else if (NewCreateAccountRequestModel.plateNumberIsNotInDVLA && NewCreateAccountRequestModel.plateNumber.isNotEmpty()) {
            checkValidation()
        } else {
            if (NewCreateAccountRequestModel.plateCountry == Constants.COUNTRY_TYPE_UK) {
                if (makeInputCheck
                    && modelInputCheck
                    && colourInputCheck && checkBoxChecked
                ) {
                    setBtnActivated()
                } else {
                    setBtnDisabled()
                }
            } else {
                if (makeInputCheck
                    && modelInputCheck
                    && colourInputCheck && binding.vehiclePlateNumber.text.toString()
                        .isNotEmpty() && binding.typeVehicle.getSelectedDescription().toString()
                        .isNotEmpty() && checkBoxChecked && vehcileRegRequired
                ) {
                    setBtnActivated()
                } else {
                    setBtnDisabled()
                }
            }

        }


    }

    private fun checkValidation() {
        if (makeInputCheck
            && modelInputCheck
            && colourInputCheck && radioButtonChecked && typeOfVehicleChecked && checkBoxChecked
        ) {
            setBtnActivated()
        } else {
            setBtnDisabled()
        }
    }

    private fun setBtnActivated() {
        if (binding.radioButtonYes.isChecked) {
            data?.plateCountry = "UK"
        } else {
            data?.plateCountry = "NON-UK"
        }
        Log.e("TAG", "setBtnActivated: plateCountry " + data?.plateCountry)
        binding.nextBtn.isEnabled = true
    }

    private fun setBtnDisabled() {
        binding.nextBtn.isEnabled = false
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.editVehicle -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(
                    Constants.PLATE_NUMBER,
                    binding.vehiclePlateNumber.text.toString()
                )
                findNavController().navigate(
                    R.id.action_addVehicleDetailsFragment_to_CreateAccountFindVehicleFragment,
                    bundle
                )
            }

            R.id.next_btn -> {
                data?.vehicleMake =
                    binding.makeInputLayout.editText.text.toString().trim()
                data?.vehicleModel =
                    binding.modelInputLayout.editText.text.toString().trim()
                data?.vehicleColor =
                    binding.colorInputLayout.editText.text.toString().trim()
                data?.plateNo = binding.vehiclePlateNumber.text.toString().trim()

                if (oldPlateNumber.isNotEmpty()) {
                    val index = arguments?.getInt(Constants.VEHICLE_INDEX)
                    if (vehicleList!!.size > 0 && index != null) {
                        vehicleList?.removeAt(index)
                    }
                }
                if (navFlowCall == Constants.PAY_FOR_CROSSINGS) {
                    val dataModel = NewVehicleInfoDetails()
                    dataModel.isUK = binding.radioButtonYes.isChecked
                    dataModel.plateNumber = binding.vehiclePlateNumber.text.toString()
                    dataModel.vehicleMake =
                        binding.makeInputLayout.getText().toString()
                    dataModel.vehicleModel =
                        binding.modelInputLayout.getText().toString()
                    dataModel.vehicleColor =
                        binding.colorInputLayout.getText().toString()
                    if (vehicleClassSelected.isNotEmpty()) {
                        dataModel.vehicleClass =
                            Utils.getManuallyAddedVehicleClass(
                                requireActivity(),
                                vehicleClassSelected
                            )
                    } else {
                        if (data?.vehicleClass?.length == 1) {
                            dataModel.vehicleClass =
                                data?.vehicleClass
                        } else {
                            dataModel.vehicleClass =
                                Utils.getManuallyAddedVehicleClass(
                                    requireActivity(),
                                    data?.vehicleClass ?: ""
                                )

                        }
                    }

                    checkRUC(dataModel)
                    return
                } else {
                    nonUKVehicleModel?.let {
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        if (vehicleList?.contains(nonUKVehicleModel) == true) {
                            accountData?.isVehicleAlreadyAddedLocal = true

                            nonUKVehicleModel.let { it1 ->
                                bundle.putString(
                                    Constants.PLATE_NUMBER,
                                    it1?.plateNumber
                                )
                            }
                            findNavController().navigate(
                                R.id.action_addVehicleDetailFragment_to_max_vehicleFragment,
                                bundle
                            )
                        } else {
                            val dataModel = NewVehicleInfoDetails()
                            dataModel.isUK = binding.radioButtonYes.isChecked
                            dataModel.plateNumber = binding.vehiclePlateNumber.text.toString()
                            dataModel.vehicleMake =
                                binding.makeInputLayout.getText().toString()
                            dataModel.vehicleModel =
                                binding.modelInputLayout.getText().toString()
                            dataModel.vehicleColor =
                                binding.colorInputLayout.getText().toString()
                            if (vehicleClassSelected.isNotEmpty()) {
                                dataModel.vehicleClass =
                                    Utils.getManuallyAddedVehicleClass(
                                        requireActivity(),
                                        vehicleClassSelected
                                    )
                            } else {
                                dataModel.vehicleClass =
                                    it.vehicleClass?.let { it1 ->
                                        Utils.getVehicleType(
                                            requireActivity(),
                                            it1
                                        )
                                    }
                            }

                            checkRUC(dataModel)

                        }
                        return
                    }

                }
                if (NewCreateAccountRequestModel.plateNumberIsNotInDVLA && NewCreateAccountRequestModel.plateNumber.isNotEmpty()) {
                    val newVehicleInfoDetails = NewVehicleInfoDetails()
                    if ((vehicleList?.size ?: 0) > 0) {
                        for (i in 0 until (vehicleList?.size ?: 0)) {
                            val numberPlate = binding.vehiclePlateNumber.text.toString()
                            if (vehicleList?.get(i)?.plateNumber == numberPlate) {
                                accountData?.isVehicleAlreadyAddedLocal = true
                                val bundle = Bundle()
                                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                                bundle.putString(Constants.PLATE_NUMBER, numberPlate)
                                bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                                findNavController().navigate(
                                    R.id.action_addVehicleDetailFragment_to_max_vehicleFragment,
                                    bundle
                                )
                                return
                            }
                        }

                        newVehicleInfoDetails.vehicleMake =
                            binding.makeInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleModel =
                            binding.modelInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleColor =
                            binding.colorInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleClass = Utils.getManuallyAddedVehicleClass(
                            requireActivity(),
                            binding.typeVehicle.getSelectedDescription().toString()
                        )
                        newVehicleInfoDetails.plateNumber =
                            binding.vehiclePlateNumber.text.toString()
                        newVehicleInfoDetails.isDblaAvailable = false
                        newVehicleInfoDetails.isUK = binding.radioButtonYes.isChecked
                        checkRUC(newVehicleInfoDetails)


                    } else {
                        newVehicleInfoDetails.vehicleMake =
                            binding.makeInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleModel =
                            binding.modelInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleColor =
                            binding.colorInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleClass =
                            Utils.getManuallyAddedVehicleClass(
                                requireActivity(),
                                vehicleClassSelected
                            )
                        newVehicleInfoDetails.plateNumber =
                            binding.vehiclePlateNumber.text.toString()
                        newVehicleInfoDetails.isDblaAvailable = false
                        newVehicleInfoDetails.isUK = binding.radioButtonYes.isChecked
                        checkRUC(newVehicleInfoDetails)


                    }


                } else if (navFlowCall == Constants.TRANSFER_CROSSINGS) {
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    arguments?.getInt(Constants.VEHICLE_INDEX)
                        ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                    if (data?.vehicleType?.lowercase().equals("a")) {
                        findNavController().navigate(
                            R.id.action_addNewVehicleDetailsFragment_to_vehicleIsExemptFromDartChargesFragment,
                            bundle
                        )

                    } else {
                        findNavController().navigate(
                            R.id.action_addVehicleDetailsFragment_to_yourVehicleFragment,
                            bundle
                        )
                    }

                }


            }

        }
    }

    private fun checkRUC(newVehicleInfoDetails: NewVehicleInfoDetails) {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        if (newVehicleInfoDetails.vehicleClass.equals("A", true)) {
            NewCreateAccountRequestModel.isRucEligible = true

            bundle.putParcelable(
                Constants.VEHICLE_DETAIL,
                newVehicleInfoDetails
            )
            findNavController().navigate(
                R.id.action_addVehicleDetailFragment_to_max_vehicleFragment,
                bundle
            )

        } else {
            vehicleList?.add(newVehicleInfoDetails)
            when (navFlowCall) {

                Constants.PAY_FOR_CROSSINGS -> {
                    Log.e("TAG", "checkRUC: plateCountry --> " + data?.plateCountry)
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    val model = CrossingDetailsModelsRequest(
                        newVehicleInfoDetails.plateNumber,
                        newVehicleInfoDetails.vehicleClass,
                        data?.plateCountry,
                        newVehicleInfoDetails.vehicleMake,
                        newVehicleInfoDetails.vehicleModel
                    )

                    viewModel.getCrossingDetails(model)
                }

                Constants.TRANSFER_CROSSINGS -> {
                    data?.apply {
                        plateNo = newVehicleInfoDetails.plateNumber.toString()
                        vehicleClass = newVehicleInfoDetails.vehicleClass
                        plateCountry = "UK"
                        vehicleMake = newVehicleInfoDetails.vehicleMake
                        vehicleModel = newVehicleInfoDetails.vehicleModel
                        vehicleColor = newVehicleInfoDetails.vehicleColor
                    }
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putParcelable(
                        Constants.NAV_DATA_KEY,
                        navData as CrossingDetailsModelsResponse
                    )
                    findNavController().navigate(
                        R.id.action_addVehicleDetailsFragment_to_ConfirmNewVehicleDetailsCheckPaidCrossingsFragment,
                        bundle
                    )
                }

                else -> {
                    val editCall = navFlowCall.equals(Constants.EDIT_SUMMARY, true)
                    if (editCall) {
                        findNavController().navigate(
                            R.id.action_addVehicleDetailsFragment_to_CreateAccountSummaryFragment,
                            bundle
                        )
                    } else {
                        findNavController().navigate(
                            R.id.action_addVehicleDetailsFragment_to_vehicleListFragment,
                            bundle
                        )
                    }
                }
            }
        }
    }


    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        typeOfVehicleChecked = true
        vehicleClassSelected = selectedItem
        if (data != null) {
            data?.vehicleType = selectedItem
        }
        validateAllFields()
        checkButton()

    }
}