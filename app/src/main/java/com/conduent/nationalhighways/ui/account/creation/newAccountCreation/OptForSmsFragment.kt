package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentOptForSmsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.makeLinks


class OptForSmsFragment : BaseFragment<FragmentOptForSmsBinding>(), View.OnClickListener {
    private var oldCommunicationTextMessage = false
    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOptForSmsBinding = FragmentOptForSmsBinding.inflate(inflater, container, false)

    override fun init() {

        binding.switchCommunication.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.checkBoxTerms.visibility = View.VISIBLE
                binding.btnNext.disable()
                binding.checkBoxTerms.isChecked = false
                NewCreateAccountRequestModel.communicationTextMessage=true
            }else{
                NewCreateAccountRequestModel.communicationTextMessage=false
                binding.checkBoxTerms.visibility = View.GONE
                binding.btnNext.enable()
            }

        }

        binding.pushCommunication.setOnCheckedChangeListener { _, isChecked ->

        }


        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.btnNext.enable()
                NewCreateAccountRequestModel.termsCondition=true

            }else{
                NewCreateAccountRequestModel.termsCondition=false

                binding.btnNext.disable()
            }
        }
        binding.btnNext.setOnClickListener(this)


        binding.checkBoxTerms.makeLinks(Pair("terms and conditions", View.OnClickListener {
            var url:String=""
            url = if (NewCreateAccountRequestModel.prePay){
                "https://pay-dartford-crossing-charge.service.gov.uk/dart-charge-terms-conditions"

            }else{
                "https://pay-dartford-crossing-charge.service.gov.uk/payg-terms-condtions"

            }
            val bundle=Bundle()
            bundle.putString(Constants.TERMSCONDITIONURL,url)
            findNavController().navigate(R.id.action_optForSmsFragment_to_termsConditionFragment,bundle)

        }))
        when(navFlowCall){

            EDIT_ACCOUNT_TYPE,EDIT_SUMMARY -> {oldCommunicationTextMessage = NewCreateAccountRequestModel.communicationTextMessage
                binding.switchCommunication.isChecked = oldCommunicationTextMessage
            }
            Constants.PROFILE_MANAGEMENT -> {
                val data = navData as ProfileDetailModel?
                val communicationPreferences = data?.accountInformation?.communicationPreferences
                if(communicationPreferences.isNullOrEmpty()){
                    binding.switchCommunication.isChecked =
                        communicationPreferences.equals("Y",true)
                }
                binding.pushCommunication.isChecked = data?.personalInformation?.pushNotifications == true

            }

        }
    }

    override fun initCtrl() {


    }

    override fun observer() {
        observe(viewModel.updateProfileApiVal, ::handleUpdateProfileDetail)
    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                val data = navData as ProfileDetailModel?
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON,false)
                findNavController().navigate(R.id.action_optForSmsFragment_to_resetForgotPassword,bundle)
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext ->{

                when(navFlowCall){

                    EDIT_SUMMARY -> {if(NewCreateAccountRequestModel.mobileNumber?.isNotEmpty() == true){
                        findNavController().popBackStack()
                    }else{
                        findNavController().navigate(R.id.action_optForSmsFragment_to_mobileVerificationFragment,bundle())
                    }}
                    Constants.PROFILE_MANAGEMENT -> {
                        val data = navData as ProfileDetailModel?
                        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                        val communication = if (binding.switchCommunication.isChecked) "Y" else "N"
                        val push = binding.pushCommunication.isChecked
                            if (data?.accountInformation?.accountType.equals(Constants.PERSONAL_ACCOUNT,true)) {
                                updateStandardUserProfile(data,communication,push)
                            }else{
                                updateBusinessUserProfile(data,communication,push)
                            }
                    }
                    EDIT_ACCOUNT_TYPE -> {findNavController().navigate(
                        R.id.action_optForSmsFragment_to_twoStepVerificationFragment,bundle())}
                    else -> {findNavController().navigate(
                        R.id.action_optForSmsFragment_to_twoStepVerificationFragment,bundle())}

                }
            }
        }
    }

    private fun bundle() : Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
        return bundle
    }

    private fun updateBusinessUserProfile(
        data: ProfileDetailModel?,
        communication: String,
        push: Boolean
    ) {
        data?.run {
            val request = UpdateProfileRequest(
                firstName = personalInformation?.firstName,
                lastName = personalInformation?.lastName,
                addressLine1 = personalInformation?.addressLine1,
                addressLine2 = personalInformation?.addressLine2,
                city = personalInformation?.city,
                state = personalInformation?.state,
                zipCode = personalInformation?.zipcode,
                zipCodePlus = personalInformation?.zipCodePlus,
                country = personalInformation?.country,
                emailAddress = personalInformation?.emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = personalInformation?.pemailUniqueCode,
                phoneCell = personalInformation?.phoneNumber ?: "",
                phoneDay = personalInformation?.phoneDay,
                phoneFax = "",
                smsOption = communication,
                phoneEvening = "",
                fein = accountInformation?.fein,
                businessName = personalInformation?.customerName
            )

            if(data.personalInformation?.phoneNumber.isNullOrEmpty()){
                verifyMobileNumber(request)
            }else {
                viewModel.updateUserDetails(request)
            }
        }


    }

    private fun updateStandardUserProfile(
        data: ProfileDetailModel?,
        communication: String,
        push: Boolean
    ) {

        data?.personalInformation?.run {
            val request = UpdateProfileRequest(
                firstName = firstName,
                lastName = lastName,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                city = city,
                state = state,
                zipCode = zipcode,
                zipCodePlus = zipCodePlus,
                country = country,
                emailAddress = emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = pemailUniqueCode,
                phoneCell = phoneNumber ?: "",
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = communication,
                phoneEvening = ""
            )

            if(data.personalInformation?.phoneNumber.isNullOrEmpty()){
                verifyMobileNumber(request)
            }else {
                viewModel.updateUserDetails(request)
            }
        }

    }

    private fun verifyMobileNumber(request: UpdateProfileRequest) {
        val bundle = Bundle()
        val data = navData as ProfileDetailModel?
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
        bundle.putParcelable(Constants.DATA, request)
        findNavController().navigate(R.id.action_optForSmsFragment_to_mobileVerificationFragment,bundle)
    }

}