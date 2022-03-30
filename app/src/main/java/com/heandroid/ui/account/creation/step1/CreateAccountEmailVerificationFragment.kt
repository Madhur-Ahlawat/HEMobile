package com.heandroid.ui.account.creation.step1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.EmailValidationModel
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.databinding.FragmentCreateAccountEmailVerificationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountEmailVerificationFragment : BaseFragment<FragmentCreateAccountEmailVerificationBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var requestModel : CreateAccountRequestModel? =null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountEmailVerificationBinding.inflate(inflater, container, false)

    override fun init() {
        VehicleHelper.list?.clear()
        requestModel = CreateAccountRequestModel(referenceId = "", securityCd = "", accountType = "", address1 = "", planType = null, billingAddressLine1 = "",
                                                 billingAddressLine2 = "", cardCity = "", cardFirstName = "", cardLastName = "",
                                                 cardMiddleName = "", cardStateType = "", cardZipCode = "",
                                                 cellPhone = "", city = "", countryType = "", creditCExpMonth = "",
                                                 creditCExpYear = "", creditCardNumber = "", creditCardType = "",
                                                 digitPin = "", emailAddress = "", eveningPhone = "", firstName = "",
                                                 ftvehicleList = null, lastName = "", maskedNumber = "",
                                                 password = "", replenishmentAmount = 0.0, securityCode = "",
                                                 smsOption = "", stateType = "", tcAccepted = "",
                                                 thresholdAmount = 0.0, transactionAmount = 0.0, zipCode1 = "",enable = false)


        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 5)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            model = EmailValidationModel(false, "", "")
            etEmail.onTextChanged { isEnable() }
            btnAction.setOnClickListener(this@CreateAccountEmailVerificationFragment)
        }
    }

    override fun observer() {
        observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        try{
       loader?.dismiss()
       when (resource) {
            is Resource.Success -> {
                if(resource.data?.statusCode?.equals("0")==true){
                    requestModel?.emailAddress = binding.etEmail.text.toString().trim()
                    val bundle = Bundle().apply {
                        putLong(Constants.REFERENCE_ID, resource.data.referenceId)
                        putParcelable(DATA,requestModel)
                    }
                    findNavController().navigate(R.id.action_emailVerification_to_confirmEmailFragment, bundle)
                }else{ showError(binding.root,resource.data?.message) }

            }
            is Resource.DataError -> { showError(binding.root, resource.errorMsg) }
      }}catch (e: Exception){}
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_action -> {
                hideKeyboard()
                sendEmailVerificationRequest()
            }
        }
    }

    private fun sendEmailVerificationRequest() {
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = EmailVerificationRequest(Constants.EMAIL_SELECTION_TYPE, binding.model?.email?:"")
        createAccountViewModel.emailVerificationApi(request)
    }

    private fun isEnable() {
        if (Utils.isEmailValid(binding.etEmail.text.toString())) binding.model = EmailValidationModel(enable = true, email = binding.etEmail.text.toString(), "")
        else binding.model = EmailValidationModel(enable = false, email = binding.etEmail.text.toString(), "")
    }
}