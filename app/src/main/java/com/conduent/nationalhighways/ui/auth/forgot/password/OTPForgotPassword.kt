package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.ReplenishmentInformation
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpResp
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsResp
import com.conduent.nationalhighways.data.model.createaccount.ConfirmEmailRequest
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentForgotOtpchangesBinding
import com.conduent.nationalhighways.ui.account.communication.CommunicationPrefsViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNT_CREATION_MOBILE_FLOW
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.FORGOT_PASSWORD_FLOW
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.TWOFA
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OTPForgotPassword : BaseFragment<FragmentForgotOtpchangesBinding>(), View.OnClickListener {


    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var data: RequestOTPModel? = null
    private var response: SecurityCodeResponseModel? = null
    private var loader: LoaderDialog? = null
    private var timer: CountDownTimer? = null
    private var timeFinish: Boolean = false
    private var isCalled = true
    private var btnEnabled: Boolean = false
    lateinit var editRequest: String
    @Inject
    lateinit var sessionManager: SessionManager
    private var isViewCreated: Boolean = false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotOtpchangesBinding =
        FragmentForgotOtpchangesBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY,"").toString()

        binding.isEnable = false
        loadUI()


        /*AdobeAnalytics.setScreenTrack(
            "login:forgot password:choose options:otp",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password:choose options:otp",
            sessionManager.getLoggedInUser()
        )*/


    }

    override fun initCtrl() {

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION)
        }

        if (arguments?.getParcelable<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION) != null) {
            replenishmentInformation =
                arguments?.getParcelable<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION)
        }

        if (arguments != null) {
            data = arguments?.getParcelable("data")
            response = arguments?.getParcelable("response")
        }


        binding.apply {
            btnVerify.setOnClickListener(this@OTPForgotPassword)
            btnResend.setOnClickListener(this@OTPForgotPassword)
            edtOtp.editText.addTextChangedListener {

                verifyCodeErrorMessage()


            }
        }
    }

    private fun verifyCodeErrorMessage() {
        if (binding.edtOtp.getText().toString().trim().length < 6) {
            binding.edtOtp.setErrorText(getString(R.string.str_security_code_must_be_6_characters))
            btnEnabled = false
        } else {
            binding.edtOtp.removeError()
            btnEnabled = true
        }



        checkButtonEnable()
    }

    private fun checkButtonEnable() {
        binding.btnVerify.isEnabled = btnEnabled

    }

    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.otp, ::handleOTPResponse)
            observe(viewModel.verifyRequestCode, ::verifyRequestOtp)
            observe(createAccountViewModel.confirmEmailApiVal, ::handleConfirmEmailResponse)
            observe(communicationPrefsViewModel.updateCommunicationPrefs, ::updateCommunicationSettingsPrefs)
            observe(viewModelProfile.updateProfileApiVal, ::handleUpdateProfileDetail)
        }

        isViewCreated = true

    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                val data = navData as ProfileDetailModel?
                val bundle = Bundle()

                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON,false)
                findNavController().navigate(R.id.action_otpForgotFragment_to_resetForgotPassword,bundle)
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
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
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON,false)
                        findNavController().navigate(R.id.action_otpForgotFragment_to_resetForgotPassword,bundle)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }

                when(navFlowCall){


                    FORGOT_PASSWORD_FLOW -> {if (!timeFinish) {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )

                        val mVerifyRequestOtpReq =
                            VerifyRequestOtpReq(
                                binding.edtOtp.getText().toString(),
                                response?.referenceId
                            )
                        viewModel.verifyRequestCode(mVerifyRequestOtpReq)

                    } else {
                        showError(binding.root, getString(R.string.error_otp_time_expire))
                    }}
                    TWOFA -> {hitTWOFAVerifyAPI()}
                    else -> {  confirmEmailCode()}

                }
            }

            R.id.btn_Resend -> {
                when (navFlowCall) {
                    Constants.ACCOUNT_CREATION_EMAIL_FLOW -> {
                        AdobeAnalytics.setActionTrack(
                            "resend",
                            "login:forgot password:choose options:otp",
                            "forgot password",
                            "english",
                            "login", "Create Account",
                            sessionManager.getLoggedInUser()
                        )
                    }

                    Constants.ACCOUNT_CREATION_MOBILE_FLOW -> {
                        AdobeAnalytics.setActionTrack(
                            "resend",
                            "login:forgot password:choose options:otp",
                            "forgot password",
                            "english",
                            "login", "Create Account",
                            sessionManager.getLoggedInUser()
                        )
                    }

                    Constants.FORGOT_PASSWORD_FLOW -> {
                        AdobeAnalytics.setActionTrack(
                            "resend",
                            "login:forgot password:choose options:otp",
                            "forgot password",
                            "english",
                            "login",
                            (requireActivity() as AuthActivity).previousScreen,
                            sessionManager.getLoggedInUser()
                        )
                    }
                }

                val bundle = Bundle()
                bundle.putParcelable("data", data)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                bundle.putParcelable(Constants.REPLENISHMENTINFORMATION, replenishmentInformation)

                findNavController().navigate(R.id.action_otpFragment_to_resenedCodeFragment, bundle)

                /* isCalled = true
                 loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                 viewModel.requestOTP(data)*/
            }
        }
    }

    private fun hitTWOFAVerifyAPI() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = VerifyRequestOtpReq(
            binding.edtOtp.getText().toString().trim(),
            response?.referenceId?.toString() ?: "",


            )
        viewModel.twoFAVerifyRequestCode(request)
    }

    private fun confirmEmailCode() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = ConfirmEmailRequest(
            response?.referenceId ?: "",
            data?.optionValue,
            binding.edtOtp.getText().toString().trim()
        )
        createAccountViewModel.confirmEmailApi(request)
    }

    private fun loadUI() {
        when (data?.optionType) {

            Constants.SMS -> {

                binding.topTitle.text = getString(R.string.str_check_sms)

                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_sms)

                if (navFlowCall == Constants.ACCOUNT_CREATION_MOBILE_FLOW) {
                    binding.messageReceivedTxt.text =
                        getString(R.string.wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                            data?.optionValue.toString()
                        ) + "."

                } else if (navFlowCall == Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE) {
                    binding.messageReceivedTxt.text =
                        getString(R.string.wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                            data?.optionValue.toString()
                        ) + "."

                }else {
                    binding.messageReceivedTxt.text =
                        getString(R.string.wehavesentatextmessageto) + " " + data!!.optionValue + "."

                }
            }

            Constants.EMAIL -> {
                binding.topTitle.text = getString(R.string.str_check_your_mail)
                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_txt)

                if (navFlowCall == Constants.ACCOUNT_CREATION_EMAIL_FLOW) {
                    binding.messageReceivedTxt.text =
                        getString(R.string.wehavesentanemail) + " " + Utils.maskEmail(data?.optionValue.toString())

                } else {
                    binding.messageReceivedTxt.text =
                        getString(R.string.wehavesentanemail) + " " + data!!.optionValue

                }
            }
        }
    }


    private fun verifyRequestOtp(status: Resource<VerifyRequestOtpResp?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                val bundle = Bundle()

                if (navFlowCall ==TWOFA) {
                    if (accountInformation?.status.equals(Constants.SUSPENDED)) {
                        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)

                        bundle.putString(
                            Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
                        )
                    } else {
                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)

                    }


                } else {
                    response?.code = binding.edtOtp.getText().toString()
                    bundle.putParcelable("data", response)
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)

                    Logg.logging("NewPassword", "response $response")
                    AdobeAnalytics.setActionTrack(
                        "verify",
                        "login:forgot password:choose options:otp",
                        "forgot password",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen,
                        sessionManager.getLoggedInUser()
                    )


                    findNavController().navigate(
                        R.id.action_otpFragment_to_createPasswordFragment,
                        bundle
                    )
                }



                AdobeAnalytics.setActionTrack1(
                    "verify",
                    "login:forgot password:choose options:otp:new password set",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen, "success",
                    sessionManager.getLoggedInUser()
                )

            }

            is Resource.DataError -> {
                if(status.errorModel?.errorCode?.toString().equals("2051")){
                    binding.edtOtp.setErrorText(getString(R.string.security_code_must_contain_correct_numbers))
                }
                else{
                    binding.edtOtp.setErrorText(status.errorMsg)

                }
                Logg.logging("NewPassword", "status.errorMsg ${status.errorMsg}")

                AdobeAnalytics.setActionTrack1(
                    "verify",
                    "login:forgot password:choose options:otp:new password set",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    status.errorMsg,
                    sessionManager.getLoggedInUser()
                )

                when (status.errorModel?.errorCode) {
                    2051 -> {
                        binding.edtOtp.setErrorText(getString(R.string.str_security_code_not_correct))

                    }
                }
            }

            else -> {
            }
        }
    }

    private fun handleOTPResponse(status: Resource<SecurityCodeResponseModel?>?) {
        Logg.logging("NewPassword", "response handleOTPResponse called  $response")

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isCalled) {
            when (status) {
                is Resource.Success -> {
                    response = status.data
                    Logg.logging("NewPassword", "response api call  $response")

                    timer = object : CountDownTimer(response?.otpExpiryInSeconds ?: 0L, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            timeFinish = true
                        }

                        override fun onFinish() {
                            timeFinish = true
                        }
                    }

                }

                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)
                }

                else -> {
                }
            }
            isCalled = false
        }
    }

    private fun handleConfirmEmailResponse(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }


        when (resource) {
            is Resource.Success -> {

                val bundle = Bundle()

                when(navFlowCall) {
                    ACCOUNT_CREATION_MOBILE_FLOW -> {
                        NewCreateAccountRequestModel.smsSecurityCode =
                            binding.edtOtp.getText().toString().trim()
                        if (editRequest.equals(EDIT_SUMMARY, true)) {
                            findNavController().navigate(
                                R.id.action_forgotOtpFragment_to_createAccountSummaryFragment
                            )
                        } else {
                            findNavController().navigate(
                                R.id.action_otpForgotFragment_to_createVehicleFragment
                            )
                        }
                    }

                    Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arguments?.getParcelable(
                                Constants.NAV_DATA_KEY,
                                CommunicationPrefsRequestModel::class.java
                            )
                        } else {
                            arguments?.getParcelable(Constants.NAV_DATA_KEY)
                        }
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                        if (data != null) {
                            communicationPrefsViewModel.updateCommunicationPrefs(data)
                        }
                    }

                    PROFILE_MANAGEMENT_2FA_CHANGE, Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                        val data = navData as ProfileDetailModel?
                        if (data?.accountInformation?.accountType.equals(
                                Constants.PERSONAL_ACCOUNT,
                                true
                            )
                        ) {
                            updateStandardUserProfile(data)
                        } else {
                            updateBusinessUserProfile(data)
                        }
                    }

                    else -> {
                        response?.code = binding.edtOtp.getText().toString()
                        bundle.putParcelable("data", response)
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        NewCreateAccountRequestModel.emailSecurityCode=binding.edtOtp.editText.text.toString()

                        when(navFlowCall){

                            EDIT_SUMMARY -> {findNavController().navigate(
                                R.id.action_forgotOtpFragment_to_createAccountSummaryFragment,
                                bundle
                            )}
                            EDIT_ACCOUNT_TYPE -> {findNavController().navigate(
                                R.id.action_forgotOtpFragment_to_createPasswordFragment,
                                bundle
                            )}
                            else -> {
                                findNavController().navigate(
                                    R.id.action_forgotOtpFragment_to_createPasswordFragment,
                                    bundle
                                )}

                        }
                    }
                }



            }

            is Resource.DataError -> {

                when (resource.errorModel?.errorCode) {
                    1 -> {
                        binding.edtOtp.setErrorText(getString(R.string.str_security_code_not_correct))

                    }

                    2 -> {
                        binding.edtOtp.setErrorText(getString(R.string.str_security_code_expired_message))

                    }

                    else -> {
                        binding.edtOtp.setErrorText(resource.errorModel?.message.toString())
                    }
                }
            }

            else -> {
            }
        }

    }

    private fun updateBusinessUserProfile(
        dataModel: ProfileDetailModel?
    ) {
        dataModel?.run {
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
                phoneCell = data?.optionValue.toString(),
                phoneDay = personalInformation?.phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = "",
                fein = accountInformation?.fein,
                businessName = personalInformation?.customerName
            )

            viewModelProfile.updateUserDetails(request)
        }


    }

    private fun updateStandardUserProfile(
        dataModel: ProfileDetailModel?
    ) {

        dataModel?.personalInformation?.run {
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
                phoneCell = data?.optionValue.toString(),
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = ""
            )

            viewModelProfile.updateUserDetails(request)
        }

    }


}