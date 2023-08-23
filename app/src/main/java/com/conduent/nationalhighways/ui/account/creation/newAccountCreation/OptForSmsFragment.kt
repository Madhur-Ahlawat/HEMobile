package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModelList
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsResp
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentOptForSmsBinding
import com.conduent.nationalhighways.ui.account.communication.CommunicationPrefsViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.makeLinks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptForSmsFragment : BaseFragment<FragmentOptForSmsBinding>(), View.OnClickListener {
    private var oldCommunicationTextMessage = false
    private var loader: LoaderDialog? = null
    //    private val viewModel: ProfileViewModel by viewModels()
    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()
    private var mAccountResp: AccountResponse? = null
    private val mCommunicationsList = ArrayList<CommunicationPrefsModel>()
    private var smsFlag = "N"
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOptForSmsBinding = FragmentOptForSmsBinding.inflate(inflater, container, false)

    override fun init() {
        when (navFlowCall){
            EDIT_SUMMARY ->{
                binding.switchCommunication.isChecked=NewCreateAccountRequestModel.communicationTextMessage
                binding.checkBoxTerms.visibility = View.GONE
                binding.btnNext.enable()

            }
            else ->{
                NewCreateAccountRequestModel.communicationTextMessage=false
            }
        }

        binding.switchCommunication.setOnClickListener {
            if(binding.switchCommunication.isChecked){
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

/*
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
*/

//        binding.pushCommunication.setOnCheckedChangeListener { _, isChecked ->
//
//        }


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
            var url =""
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
            Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                val title: TextView? = requireActivity().findViewById(R.id.title_txt)
                title?.text = getString(R.string.communication_preferences)
                val data = navData as ProfileDetailModel?
//                binding.pushCommunication.isChecked = data?.personalInformation?.pushNotifications == true
                loader = LoaderDialog()
                loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                communicationPrefsViewModel.getAccountSettingsPrefs()
            }

        }
    }

    override fun initCtrl() {


    }

    override fun observer() {
//        observe(viewModel.updateProfileApiVal, ::handleUpdateProfileDetail)
        observe(communicationPrefsViewModel.getAccountSettingsPrefs, ::getCommunicationSettingsPref)
        observe(communicationPrefsViewModel.updateCommunicationPrefs, ::updateCommunicationSettingsPrefs)
    }

    private fun updateCommunicationSettingsPrefs(resource: Resource<CommunicationPrefsResp?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.let { res ->
                    if (res.data?.statusCode == "0") {
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED)
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON,false)
                        findNavController().navigate(R.id.action_optForSmsFragment_to_resetForgotPassword,bundle)
                    }else{
                        ErrorUtil.showError(binding.root, resource.errorMsg)
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

    private fun getCommunicationSettingsPref(resource: Resource<AccountResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.let { res ->
                    res.data?.let {
                        mAccountResp = it
                    }

                    res.data?.accountInformation?.communicationPreferences?.forEach {
                        if (it != null) {
                            mCommunicationsList.add(it)
                        }
                        if (it?.category.equals(Constants.CATEGORY_RECEIPTS, true)) {

                            if (it?.smsFlag.equals("Y", true)) {
                                binding.switchCommunication.isChecked  = true
                                smsFlag = "Y"
                            }
                            binding.checkBoxTerms.visibility = View.GONE
                            binding.btnNext.enable()
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

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                val data = navData as ProfileDetailModel?
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED)
                bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
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
                    Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        val communication = if (binding.switchCommunication.isChecked) "Y" else "N"
//                        val push = binding.pushCommunication.isChecked
                        if(smsFlag.equals(communication,true)){
                            findNavController().popBackStack()
                        }else{
                            val mList = ArrayList<CommunicationPrefsRequestModelList?>()
                            if (mCommunicationsList.size > 0 ) {
                                mCommunicationsList.forEach {
                                    val mListModel = CommunicationPrefsRequestModelList(
                                        it.id,
                                        it.category,
                                        it.oneMandatory,
                                        it.defEmail,
                                        it.emailFlag,
                                        it.mailFlag,
                                        it.defSms,
                                        communication,
                                        it.defVoice,
                                        it.voiceFlag,
                                        it.pushNotFlag
                                    )
                                    mList.add(mListModel)

                                }
                                val model = CommunicationPrefsRequestModel(mList)
                                if(mAccountResp?.personalInformation?.phoneCell.isNullOrEmpty()){
                                    verifyMobileNumber(model)
                                }else {
                                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                                    communicationPrefsViewModel.updateCommunicationPrefs(model)
                                }

                            }
                        }
                        /*if (data?.accountInformation?.accountType.equals(Constants.PERSONAL_ACCOUNT,true)) {
                            updateStandardUserProfile(data,communication,push)
                        }else{
                            updateBusinessUserProfile(data,communication,push)
                        }*/
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

            /*if(data.personalInformation?.phoneNumber.isNullOrEmpty()){
                verifyMobileNumber(request)
            }else {
                viewModel.updateUserDetails(request)
            }*/
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

            /* if(data.personalInformation?.phoneNumber.isNullOrEmpty()){
                 verifyMobileNumber(request)
             }else {
                 viewModel.updateUserDetails(request)
             }*/
        }

    }

    private fun verifyMobileNumber(model: CommunicationPrefsRequestModel) {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED)
        bundle.putParcelable(Constants.NAV_DATA_KEY, model)
        findNavController().navigate(R.id.action_optForSmsFragment_to_mobileVerificationFragment,bundle)
    }

}