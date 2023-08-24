package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import com.conduent.nationalhighways.utils.extn.invisible
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

    private var makeInputCheck: Boolean = false
    private var modelInputCheck: Boolean = false
    private var colourInputCheck: Boolean = false
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

    override fun init() {
        typeOfVehicle.clear()
        typeOfVehicle.add("Motorcycle, moped or quad bike")
        typeOfVehicle.add("Car, van or minibus < 8 seats")
        typeOfVehicle.add("Bus, coach or other goods vehicle with 2 axles")
        typeOfVehicle.add("Vehicle with more than 2 axles")
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)

        binding.apply {
            typeVehicle.dataSet.addAll(typeOfVehicle)
            modelInputLayout.editText.filters = arrayOf<InputFilter>(LengthFilter(50))
            makeInputLayout.editText.filters = arrayOf<InputFilter>(LengthFilter(50))
            colorInputLayout.editText.filters = arrayOf<InputFilter>(LengthFilter(50))
        }
        oldPlateNumber = arguments?.getString(Constants.OLD_PLATE_NUMBER, "").toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(arguments?.getParcelable(Constants.NAV_DATA_KEY,CrossingDetailsModelsResponse::class.java)!=null){
                navData = arguments?.getParcelable(

                    Constants.NAV_DATA_KEY,CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if(arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY)!=null){
                navData = arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                )
            }
        }
        accountData = NewCreateAccountRequestModel
        vehicleList = accountData?.vehicleList
        if (oldPlateNumber.isNotEmpty()) {
            val index = arguments?.getInt(Constants.VEHICLE_INDEX)
            val isDblaAvailable = arguments?.getBoolean(Constants.IS_DBLA_AVAILABLE, true)
            if (isDblaAvailable != null) {
                if (isDblaAvailable.not()) {
                    nonUKVehicleModel = index?.let { vehicleList?.get(it) }
                    updateView(nonUKVehicleModel)
                }
            }
        }
        setPreSelectedVehicleType()
        binding.typeVehicle.dropDownItemSelectListener = this
        binding.model = false
        try {
            mVehicleDetails = arguments?.getParcelable(Constants.NAV_DATA_KEY) as? VehicleResponse?
            navData = CrossingDetailsModelsResponse()
        } catch (e: Exception) {
            navData = arguments?.getParcelable(
                Constants.NAV_DATA_KEY,
                CrossingDetailsModelsResponse::class.java
            )
        } finally {

        }

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            radioButtonChecked = R.id.radioButtonYes == checkedId || R.id.radioButtonNo == checkedId

            (navData as CrossingDetailsModelsResponse).veicleUKnonUK=radioButtonChecked
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
            binding.vehicleRegisteredLayout.visibility = View.VISIBLE
        } else {
            binding.vehicleRegisteredLayout.visibility = View.GONE
            updateView(nonUKVehicleModel)



            if (NewCreateAccountRequestModel.plateCountry == Constants.COUNTRY_TYPE_UK) {
                binding.typeVehicle.visibility = View.GONE
                binding.typeOfVehicleInputLayout.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
            } else {
                binding.typeVehicle.visibility = View.VISIBLE
                binding.typeOfVehicleInputLayout.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE


                binding.typeOfVehicleInputLayout.setText(nonUKVehicleModel?.plateNumber.toString())


            }

        }

        binding.nextBtn.setOnClickListener(this)

        when (navFlowCall) {

            Constants.PAY_FOR_CROSSINGS -> {
                typeOfVehicle.clear()
                typeOfVehicle.add("Car, van or minibus < 8 seats")
                typeOfVehicle.add("Bus, coach or other goods vehicle with 2 axles")
                typeOfVehicle.add("Vehicle with more than 2 axles")
                binding.apply {
                    typeVehicle.dataSet.clear()
                    typeVehicle.dataSet.addAll(typeOfVehicle)
                }
                if (NewCreateAccountRequestModel.isExempted) {
                    binding.makeInputLayout.invisible()
                    binding.modelInputLayout.invisible()
                    binding.colorInputLayout.invisible()

                    binding.vehicleRegisteredLayout.visibility = View.GONE

                    radioButtonChecked = true
                    makeInputCheck = true
                    modelInputCheck = true
                    colourInputCheck = true

                    checkValidation()
                }
            }
        }

    }

    private fun setPreSelectedVehicleType() {
        if (typeOfVehicle.size > 0 && navData != null && navData is CrossingDetailsModelsResponse) {
            binding.typeVehicle.setSelectedValue((navData as CrossingDetailsModelsResponse).vehicleType!!)
            typeOfVehicleChecked=true
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
                        val mUnSettledTrips = it.unSettledTrips?.toInt()
                        if (mUnSettledTrips != null) {
                            val bundle = Bundle()
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            bundle.putParcelable(
                                Constants.NAV_DATA_KEY,
                                (resource.data as CrossingDetailsModelsResponse).apply {
                                    vehicleColor =
                                        (navData as CrossingDetailsModelsResponse).vehicleColor
                                    unSettledTrips=mUnSettledTrips
                                    vehicleType=(navData as CrossingDetailsModelsResponse).vehicleType
                                })
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
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }

    }

    private fun updateView(nonUKVehicleModel: NewVehicleInfoDetails?) {
        binding.vehiclePlateNumber.text = nonUKVehicleModel?.plateNumber
        binding.makeInputLayout.setText(nonUKVehicleModel?.vehicleMake.toString())
        binding.modelInputLayout.setText(nonUKVehicleModel?.vehicleModel.toString())
        binding.colorInputLayout.setText(nonUKVehicleModel?.vehicleColor.toString())
        binding.typeVehicle.setSelectedValue(Utils.getVehicleType(nonUKVehicleModel?.vehicleClass.toString()))
        if (nonUKVehicleModel?.vehicleClass.equals("D", true)) {
            typeOfVehicle.clear()
            typeOfVehicle.add("Bus, coach or other goods vehicle with 2 axles")
            typeOfVehicle.add("Vehicle with more than 2 axles")
            binding.apply {
                typeVehicle.dataSet.clear()
                typeVehicle.dataSet.addAll(typeOfVehicle)
            }
        }
        if (nonUKVehicleModel?.isUK == true) {
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
                it?.apply {
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
                    (navData as CrossingDetailsModelsResponse).vehicleMake =
                        binding.makeInputLayout.editText.getText().toString().trim()
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
                    (navData as CrossingDetailsModelsResponse).vehicleModel =
                        binding.modelInputLayout.editText.getText().toString().trim()
                    true
                }
            } else {
                binding.modelInputLayout.setErrorText(getString(R.string.enter_your_vehicle_model))
                false
            }

            checkButton()
        }
        binding.colorInputLayout.editText.onTextChanged {

            colourInputCheck = if (it != null && it.trim().length > 0) {
                if (hasDigits(it) || hasSpecialCharacters(it.trim(), Utils.splCharVehicleColor)) {
                    binding.colorInputLayout.setErrorText(getString(R.string.str_colour_error_message))
                    false
                } else {
                    validateAllFields()
                    binding.colorInputLayout.removeError()
                    (navData as CrossingDetailsModelsResponse).vehicleColor =
                        binding.colorInputLayout.editText.getText().toString().trim()
                    true
                }
            } else {
                binding.colorInputLayout.setErrorText(getString(R.string.enter_your_vihicle_color))
                false
            }

            checkButton()
        }


    }

    private fun validateAllFields() {
        radioButtonChecked = (binding.radioButtonYes.isChecked || binding.radioButtonNo.isChecked)
        checkBoxChecked = binding.checkBoxTerms.isChecked
        modelInputCheck = if (binding.modelInputLayout.editText.getText()!!.isNotEmpty()) {
            if (binding.modelInputLayout.editText.getText()!!.toString().trim().length > 50) {
                false
            } else if (hasSpecialCharacters(
                    binding.modelInputLayout.editText.getText()!!.toString().trim()
                        .replace(" ", ""), Utils.splCharVehicleModel
                )
            ) {
                false
            } else {
                binding.modelInputLayout.removeError()
                (navData as CrossingDetailsModelsResponse).vehicleModel =
                    binding.modelInputLayout.editText.getText().toString().trim()
                true
            }
        } else {
            false
        }


        makeInputCheck = if (binding.makeInputLayout.editText.getText().toString().isNotEmpty()) {
            if (binding.makeInputLayout.editText.getText().toString().trim().length > 50) {
                false
            } else if (hasDigits(
                    binding.makeInputLayout.editText.getText().toString()
                ) || hasSpecialCharacters(
                    binding.makeInputLayout.editText.getText().toString(),
                    Utils.splCharVehicleMake
                )
            ) {
                false
            } else {
                binding.makeInputLayout.removeError()
                (navData as CrossingDetailsModelsResponse).vehicleMake =
                    binding.makeInputLayout.editText.getText().toString().trim()
                true
            }
        } else {
            false
        }

        colourInputCheck = if (binding.colorInputLayout.editText.getText()
                .toString() != null && binding.colorInputLayout.editText.getText().toString()
                .trim().length > 0
        ) {
            if (hasDigits(
                    binding.colorInputLayout.editText.getText().toString()
                ) || hasSpecialCharacters(
                    binding.colorInputLayout.editText.getText().toString().trim(),
                    Utils.splCharVehicleColor
                )
            ) {
                false
            } else {
                binding.colorInputLayout.removeError()
                (navData as CrossingDetailsModelsResponse).vehicleColor =
                    binding.colorInputLayout.editText.getText().toString().trim()
                true
            }
        } else {
            false
        }
        checkButton()
    }

    private fun checkButton() {
        if (NewCreateAccountRequestModel.plateNumberIsNotInDVLA && NewCreateAccountRequestModel.plateNumber.isNotEmpty()) {
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
                    && colourInputCheck && binding.typeOfVehicleInputLayout.getText().toString()
                        .isNotEmpty() && binding.typeVehicle.getSelectedDescription().toString()
                        .isNotEmpty() && checkBoxChecked
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
        binding.nextBtn.isEnabled = true
    }

    private fun setBtnDisabled() {
        binding.nextBtn.isEnabled = false
    }

    override fun onClick(v: View?) {
        val editCall = navFlowCall.equals(Constants.EDIT_SUMMARY, true)

        when (v?.id) {
            R.id.editVehicle -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.PLATE_NUMBER, binding.vehiclePlateNumber.text.toString())
                findNavController().navigate(
                    R.id.action_addVehicleDetailsFragment_to_CreateAccountFindVehicleFragment,
                    bundle
                )
            }

            R.id.next_btn -> {
                (navData as CrossingDetailsModelsResponse).vehicleMake =
                    binding.makeInputLayout.editText.getText().toString().trim()
                (navData as CrossingDetailsModelsResponse).vehicleModel =
                    binding.modelInputLayout.editText.getText().toString().trim()
                (navData as CrossingDetailsModelsResponse).vehicleColor =
                    binding.colorInputLayout.editText.getText().toString().trim()

                if (oldPlateNumber.isNotEmpty()) {
                    val index = arguments?.getInt(Constants.VEHICLE_INDEX)
                    if (vehicleList!!.size > 0 && index != null) {
                        vehicleList?.removeAt(index)
                    }
                }

                nonUKVehicleModel?.let {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    if (vehicleList?.contains(nonUKVehicleModel) == true) {
                        accountData?.isVehicleAlreadyAddedLocal = true

                        nonUKVehicleModel.let {
                            bundle.putString(
                                Constants.PLATE_NUMBER,
                                it?.plateNumber
                            )
                        }
                        findNavController().navigate(
                            R.id.action_addVehicleDetailFragment_to_max_vehicleFragment,
                            bundle
                        )
                    } else {
                        it.isUK = binding.radioButtonYes.isChecked
                        it.vehicleMake =
                            binding.makeInputLayout.getText().toString()
                        it.vehicleModel =
                            binding.modelInputLayout.getText().toString()
                        it.vehicleColor =
                            binding.colorInputLayout.getText().toString()
                        it.vehicleClass =
                            Utils.getManuallyAddedVehicleClass(vehicleClassSelected)
                        checkRUC(it)

                    }
                    return
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
                            Utils.getManuallyAddedVehicleClass(vehicleClassSelected)
                        newVehicleInfoDetails.plateNumber =
                            binding.vehiclePlateNumber.text.toString()
                        newVehicleInfoDetails.isDblaAvailable = false
                        newVehicleInfoDetails.isUK = binding.radioButtonYes.isChecked
                        checkRUC(newVehicleInfoDetails)


                    }


                }


            }

        }
    }

    private fun checkRUC(newVehicleInfoDetails: NewVehicleInfoDetails) {
        val bundle = Bundle()
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
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    val model = CrossingDetailsModelsRequest(
                        newVehicleInfoDetails.plateNumber,
                        newVehicleInfoDetails.vehicleClass,
                        "UK",
                        newVehicleInfoDetails.vehicleMake,
                        newVehicleInfoDetails.vehicleModel
                    )

                    viewModel.getCrossingDetails(model)
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
        (navData as CrossingDetailsModelsResponse).vehicleType = selectedItem
        validateAllFields()
        checkButton()
    }
}