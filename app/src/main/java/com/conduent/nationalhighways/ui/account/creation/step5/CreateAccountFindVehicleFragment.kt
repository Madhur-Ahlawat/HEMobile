package com.conduent.nationalhighways.ui.account.creation.step5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.databinding.FragmentCreateAccountFindVehicleBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.splCharsVehicleRegistration
import com.conduent.nationalhighways.utils.common.observe
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(),
    View.OnClickListener {

    private var isViewCreated = false
    private var plateNumber = ""
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var isObserverBack = false
    private var loader: LoaderDialog? = null
    private var time = (1 * 1000).toLong()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isObserverBack = true
    }

    override fun init() {
        arguments?.getString(Constants.PLATE_NUMBER,"").toString().let { plateNumber = it.replace("null","") }

        binding.editNumberPlate.setText(plateNumber)
        val filter = InputFilter.AllCaps()
        binding.editNumberPlate.editText.filters =  arrayOf( filter)

        if(plateNumber.isNotEmpty()){
            binding.findVehicle.isEnabled = true
        }
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.
        STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {
        binding.editNumberPlate.setMaxLength(10)
        binding.editNumberPlate.editText.addTextChangedListener { isEnable() }
        binding.findVehicle.setOnClickListener(this)
    }

    private fun isEnable() {
        if (binding.editNumberPlate.getText().toString().trim().isEmpty()) {
            binding.findVehicle.isEnabled = false
            binding.editNumberPlate.removeError()
        }
        else{
            if ( Utils.countOccurenceOfChar(binding.editNumberPlate.editText.getText().toString().trim(),'-')>1 || binding.editNumberPlate.editText.getText().toString().trim().contains(
                    Utils.TWO_OR_MORE_HYPEN
                ) || (binding.editNumberPlate.editText.getText().toString().trim().last()
                    .toString().equals(".") || binding.editNumberPlate.editText.getText()
                    .toString().first().toString().equals("."))
                || (binding.editNumberPlate.editText.getText().toString().trim().last().toString()
                    .equals("-") || binding.editNumberPlate.editText.getText().toString().first()
                    .toString().equals("-"))
            ) {
                binding.editNumberPlate.setErrorText("Vehicle Registration $plateNumber must only include letters a to z, numbers 0 to 9 and special characters such as hyphens and spaces")
                binding.findVehicle.isEnabled = false
            }
            else if (Utils.hasSpecialCharacters(binding.editNumberPlate.getText().toString().trim(),splCharsVehicleRegistration)) {
                binding.editNumberPlate.setErrorText("Vehicle Registration $plateNumber must only include letters a to z, numbers 0 to 9 and special characters such as hyphens and spaces")
                binding.findVehicle.isEnabled = false
            }
            else if(binding.editNumberPlate.getText().toString().trim().length>10){
                binding.editNumberPlate.setErrorText("Vehicle Registration $plateNumber must be 10 characters or fewer")
                binding.findVehicle.isEnabled = false
            }
            else {
                binding.editNumberPlate.removeError()
                binding.findVehicle.isEnabled = true
            }
        }

    }

    private fun removeError() {
        binding.editNumberPlate.removeError()
        binding.findVehicle.isEnabled = true
    }

    override fun observer() {
        if (!isViewCreated) {

            observe(viewModel.findNewVehicleLiveData, ::apiResponseDVRM)
            observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
        }
        isViewCreated = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.findVehicle -> {
                NewCreateAccountRequestModel.isExempted=false
                NewCreateAccountRequestModel.isRucEligible=false
                NewCreateAccountRequestModel.isVehicleAlreadyAdded=false
                NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal=false
                NewCreateAccountRequestModel.isMaxVehicleAdded=false
                NewCreateAccountRequestModel.plateNumberIsNotInDVLA = false
                val editCall = navFlowCall.equals(Constants.EDIT_SUMMARY,true)
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                if(plateNumber.isNotEmpty() && plateNumber == binding.editNumberPlate.getText().toString().trim()){
                    if(editCall){
                        findNavController().navigate(R.id.action_findVehicleFragment_to_accountSummaryFragment,bundle)
                    }else {
                        findNavController().navigate(R.id.action_findVehicleFragment_to_vehicleListFragment,bundle)
                    }
                    return
                }

                binding.findVehicle.isEnabled = false
                val numberPlate = binding.editNumberPlate.getText().toString().trim().replace(" ","").replace("-","")
                NewCreateAccountRequestModel.plateNumber = numberPlate

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.findVehicle.isEnabled = true
                }, time)


                isObserverBack = true

                val addedVehicleList = NewCreateAccountRequestModel.addedVehicleList
                var isVehicleExist = false
                for(obj in addedVehicleList){
                    if(obj?.plateInfo?.number.equals(numberPlate, true)){
                        isVehicleExist = true
                    }
                }

                if(isVehicleExist){
                    NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal = true
                    val bundleData = Bundle()
                    bundleData.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundleData.putString(Constants.PLATE_NUMBER, plateNumber)
                    findNavController().navigate(R.id.action_findVehicleFragment_to_maximumVehicleFragment,bundleData)
                }else {
                    val vehicleList = NewCreateAccountRequestModel.vehicleList
                    val size = addedVehicleList.size+vehicleList.size
                    if(size>=10){
                        NewCreateAccountRequestModel.isMaxVehicleAdded = true
                        findNavController().navigate(R.id.action_findVehicleFragment_to_maximumVehicleFragment,bundle)
                    }else {
                        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                        checkForDuplicateVehicle(numberPlate)
                    }
                }



            }
        }
    }
    private fun apiResponseDVRM(resource: Resource<List<NewVehicleInfoDetails?>?>) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        val accountData = NewCreateAccountRequestModel
        val vehicleList = accountData.vehicleList
        if (isObserverBack) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { apiData ->
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        Log.d("responseData", Gson().toJson(apiData))


                        if(vehicleList.contains(apiData[0])){
                            accountData.isVehicleAlreadyAddedLocal = true
                            val bundleData = Bundle()
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            apiData[0].let {  bundleData.putString(Constants.PLATE_NUMBER, it?.plateNumber) }
                            findNavController().navigate(R.id.action_findVehicleFragment_to_maximumVehicleFragment,bundleData)
                            return
                        }

                        if (apiData[0]?.isExempted?.equals("Y", true) == true) {
                            NewCreateAccountRequestModel.isExempted = true

                            findNavController().navigate(R.id.action_findVehicleFragment_to_maximumVehicleFragment,bundle)

                        }

                        if (apiData[0]?.isRUCEligible?.equals("y",true)==true) {
                            if (apiData.isNotEmpty()) {
                                bundle.putParcelable(
                                    Constants.VEHICLE_DETAIL,
                                    apiData[0]
                                )
                            }
                            bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                            arguments?.getInt(Constants.VEHICLE_INDEX)
                                ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
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
                            findNavController().navigate(R.id.action_findVehicleFragment_to_maximumVehicleFragment,bundle)

                        }


                    }


                }

                is Resource.DataError -> {

                    isObserverBack = false
                    var isVehicleExist = false
                    val numberPlate = binding.editNumberPlate.getText().toString().trim()
                    for(obj in vehicleList){
                        if(obj.plateNumber.equals(numberPlate, true)){
                            isVehicleExist = true
                        }
                    }

                    if(isVehicleExist){
                        accountData.isVehicleAlreadyAddedLocal = true
                        val bundleData = Bundle()
                        bundleData.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundleData.putString(Constants.PLATE_NUMBER, plateNumber)
                        findNavController().navigate(R.id.action_findVehicleFragment_to_maximumVehicleFragment,bundleData)
                    }else{
                        NewCreateAccountRequestModel.plateNumberIsNotInDVLA = true
                        val bundle = Bundle()
                        bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        arguments?.getInt(Constants.VEHICLE_INDEX)
                            ?.let { bundle.putInt(Constants.VEHICLE_INDEX, it) }
                        findNavController().navigate(R.id.action_findVehicleFragment_to_addNewVehicleDetailsFragment,bundle)
                    }

                }

                else -> {
                }
            }
        }
    }

    private fun checkForDuplicateVehicle(plateNumber: String) {

        val vehicleValidReqModel = ValidVehicleCheckRequest(
            plateNumber, "UK", "STANDARD",
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
                    binding.editNumberPlate.getText().toString().trim(),
                    Constants.AGENCY_ID.toInt()
                )


            }

            is Resource.DataError -> {
//                binding.editNumberPlate.setErrorText(resource.errorMsg)
                val numberPlate = binding.editNumberPlate.getText().toString().trim()
                NewCreateAccountRequestModel.plateNumber = numberPlate
                NewCreateAccountRequestModel.isVehicleAlreadyAdded = true

                findNavController().navigate(R.id.action_findVehicleFragment_to_maximumVehicleFragment)
            }

            else -> {
            }
        }
    }

}