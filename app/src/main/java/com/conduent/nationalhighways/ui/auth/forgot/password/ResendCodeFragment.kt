package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.ReplenishmentInformation
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.databinding.FragmentForgotOtpchangesBinding
import com.conduent.nationalhighways.databinding.FragmentResendCodeBinding
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResendCodeFragment : BaseFragment<FragmentResendCodeBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null
    private var data: RequestOTPModel? = null
    private var isViewCreated:Boolean=false
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var response: SecurityCodeResponseModel? = null
    private lateinit var  navFlow:String
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation?=null
    private var replenishmentInformation: ReplenishmentInformation?=null





    @Inject
    lateinit var sessionManager: SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentResendCodeBinding = FragmentResendCodeBinding.inflate(inflater, container, false)



    override fun initCtrl() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()

        if (arguments!=null){
            data = arguments?.getParcelable("data")
        }

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION)!=null){
            accountInformation=arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION)
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }

        if (arguments?.getParcelable<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION)!=null){
            replenishmentInformation=arguments?.getParcelable<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION)
        }
    }
    override fun init() {

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }



        if (navFlow==Constants.ACCOUNT_CREATION_EMAIL_FLOW||navFlow==Constants.ACCOUNT_CREATION_MOBILE_FLOW){
            if (data?.optionType==Constants.EMAIL){
                binding.subTitle.text=getString(R.string.resend_code,Utils.maskEmail(data?.optionValue.toString()))

            }else{
                binding.subTitle.text=getString(R.string.resend_code_expire,Utils.maskPhoneNumber(data?.optionValue.toString()))

            }

        }else{
            if (data?.optionType==Constants.EMAIL){
                binding.subTitle.text=getString(R.string.resend_code,data?.optionValue)

            }else{
                binding.subTitle.text=getString(R.string.resend_code_text,data?.optionValue)

            }

        }
        binding.apply {
            btnVerify.setOnClickListener(this@ResendCodeFragment)
        }

    }

    override fun observer() {
        if (!isViewCreated){
            observe(viewModel.otp, ::handleOTPResponse)
            observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)

        }



        isViewCreated=true
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_verify->{
                if (navFlow==Constants.FORGOT_PASSWORD_FLOW){
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    viewModel.requestOTP(data)

                }else if (navFlow==Constants.ACCOUNT_CREATION_EMAIL_FLOW){
                    hitApi()
                }else if (navFlow==Constants.ACCOUNT_CREATION_MOBILE_FLOW){
                    val bundle = Bundle()
                    bundle.putParcelable("data", data)
                    bundle.putString(Constants.NAV_FLOW_KEY,navFlow)

                    findNavController().navigate(
                        R.id.action_resenedCodeFragment_to_otpFragment,
                        bundle
                    )
                }else if (navFlow==Constants.TWOFA){
                    viewModel.twoFARequestOTP(data)

                }


            }
        }
    }
    private fun handleOTPResponse(status: Resource<SecurityCodeResponseModel?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
            when (status) {
                is Resource.Success -> {
                    response = status.data
                    val bundle = Bundle()
                    bundle.putParcelable("data", data)
                    response = status.data
                    bundle.putParcelable("response", response)
                    bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                    bundle.putParcelable(Constants.PERSONALDATA,personalInformation)
                    bundle.putParcelable(Constants.ACCOUNTINFORMATION,accountInformation)
                    bundle.putParcelable(Constants.REPLENISHMENTINFORMATION,replenishmentInformation)

                    findNavController().navigate(
                        R.id.action_resenedCodeFragment_to_otpFragment,
                        bundle
                    )


                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
                else -> {
                }
            }

    }


    private fun hitApi(){
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = EmailVerificationRequest(
            Constants.EMAIL,
            data?.optionValue
        )
        createAccountViewModel.emailVerificationApi(request)



    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {

                val bundle = Bundle()
                bundle.putParcelable("data", RequestOTPModel(Constants.EMAIL,data?.optionValue))

                bundle.putParcelable("response", SecurityCodeResponseModel(resource.data?.emailStatusCode,0L,resource.data?.referenceId,true))


                bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                findNavController().navigate(
                    R.id.action_resenedCodeFragment_to_otpFragment,
                    bundle
                )
            }
            is Resource.DataError -> {

                ErrorUtil.showError(binding.root, resource.errorMsg)

            }
            else -> {
            }
        }
    }



}