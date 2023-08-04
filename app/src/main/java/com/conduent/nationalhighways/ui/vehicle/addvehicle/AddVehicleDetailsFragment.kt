package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.R.attr.maxLength
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentNewAddVehicleDetailsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.DATA
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasDigits
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
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


    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {}

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
        binding.typeVehicle.dropDownItemSelectListener = this

        binding.model = false
        mVehicleDetails = arguments?.getParcelable(DATA) as? VehicleResponse?

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            radioButtonChecked = R.id.radioButtonYes == checkedId || R.id.radioButtonNo == checkedId

            checkButton()
        }

        binding.checkBoxTerms.setOnCheckedChangeListener { _,  isChecked ->
            checkBoxChecked = isChecked
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

    }

    private fun updateView(nonUKVehicleModel: NewVehicleInfoDetails?) {
        binding.vehiclePlateNumber.text = nonUKVehicleModel?.plateNumber
        binding.makeInputLayout.setText(nonUKVehicleModel?.vehicleMake.toString())
        binding.modelInputLayout.setText(nonUKVehicleModel?.vehicleModel.toString())
        binding.colorInputLayout.setText(nonUKVehicleModel?.vehicleColor.toString())
        binding.typeVehicle.setSelectedValue(Utils.getVehicleType(nonUKVehicleModel?.vehicleClass.toString()))
        if(nonUKVehicleModel?.vehicleClass.equals("D",true)){
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
        checkButton()
        binding.makeInputLayout.editText.onTextChanged {
            makeInputCheck = if (it.isNotEmpty()) {
                if (it.trim().length > 50) {
                    binding.makeInputLayout.setErrorText(getString(R.string.vehicle_make_must_be_less_than_fifty))
                    false
                }
                else if (hasDigits(it) || hasSpecialCharacters(it,Utils.splCharVehicleMake)) {
                    binding.makeInputLayout.setErrorText(getString(R.string.str_make_error_message))
                    false
                } else {
                    binding.makeInputLayout.removeError()
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
                }
                else if (hasSpecialCharacters(it,Utils.splCharVehicleModel)) {
                    binding.modelInputLayout.setErrorText(getString(R.string.str_model_error_message))
                    false
                } else {
                    binding.modelInputLayout.removeError()
                    true
                }
            } else {
                binding.modelInputLayout.setErrorText(getString(R.string.enter_your_vehicle_model))
                false
            }

            checkButton()
        }
        binding.colorInputLayout.editText.onTextChanged {

            colourInputCheck = if (it!=null && it.trim().length>0) {
                if (hasDigits(it) || hasSpecialCharacters(it,Utils.splCharVehicleColor)) {
                    binding.colorInputLayout.setErrorText(getString(R.string.str_colour_error_message))
                    false
                } else {
                    binding.colorInputLayout.removeError()
                    true
                }
            } else {
                binding.colorInputLayout.setErrorText(getString(R.string.enter_your_vihicle_color))
                false
            }

            checkButton()
        }


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
        val editCall = navFlowCall.equals(Constants.EDIT_SUMMARY,true)

        when (v?.id) {
            R.id.editVehicle -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                 bundle.putString(Constants.PLATE_NUMBER, binding.vehiclePlateNumber.text.toString())
                findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_CreateAccountFindVehicleFragment,bundle)
            }

            R.id.next_btn -> {

                if (oldPlateNumber.isNotEmpty()) {
                    val index = arguments?.getInt(Constants.VEHICLE_INDEX)
                    if (index != null) {
                        vehicleList?.removeAt(index)
                    }
                }

                nonUKVehicleModel?.let {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
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
                        vehicleList?.add(it)
                        if(editCall){
                            findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_CreateAccountSummaryFragment)
                        }else {
                            findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_vehicleListFragment,bundle)
                        }
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
                                bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
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
        bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
        if(newVehicleInfoDetails.vehicleClass.equals("A",true)) {
            NewCreateAccountRequestModel.isRucEligible = true

            bundle.putParcelable(
                Constants.VEHICLE_DETAIL,
                newVehicleInfoDetails
            )
            findNavController().navigate(
                R.id.action_addVehicleDetailFragment_to_max_vehicleFragment,
                bundle
            )

        }else{
            vehicleList?.add(newVehicleInfoDetails)
            val editCall = navFlowCall.equals(Constants.EDIT_SUMMARY,true)
            if(editCall){
                findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_CreateAccountSummaryFragment,bundle)
            }else {
                findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_vehicleListFragment,bundle)
            }
        }
    }



    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        typeOfVehicleChecked = true
        vehicleClassSelected = selectedItem
        checkButton()
    }

}