package com.conduent.nationalhighways.ui.auth.forgot.password

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.AccountResponse
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
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentForgotOtpchangesBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNT_CREATION_MOBILE_FLOW
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_MOBILE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.FORGOT_PASSWORD_FLOW
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.TWOFA
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.google.gson.Gson
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
    private lateinit var editRequest: String

    @Inject
    lateinit var sessionManager: SessionManager
    private var isViewCreated: Boolean = false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private val viewModelProfile: ProfileViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var phoneCountryCode: String = ""
    private var isItMobileNumber: Boolean = false
    private var phoneCell: String = ""
    private var phoneCellCountryCode: String = ""
    private var phoneDay: String = ""
    private var phoneDayCountryCode: String = ""
    private val dashboardViewmodel: DashboardViewModel by activityViewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotOtpchangesBinding =
        FragmentForgotOtpchangesBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

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
        editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()
        phoneCountryCode = arguments?.getString(Constants.PHONE_COUNTRY_CODE, "").toString()


        if (arguments?.containsKey(Constants.IS_MOBILE_NUMBER) == true) {
            isItMobileNumber = arguments?.getBoolean(Constants.IS_MOBILE_NUMBER) ?: false
        }
        if (arguments?.containsKey(Constants.NAV_FLOW_KEY) == true) {
            navFlowCall = arguments?.getString(Constants.NAV_FLOW_KEY)?: ""
        }
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
            if (isItMobileNumber) {
                NewCreateAccountRequestModel.sms_referenceId = response?.referenceId
            } else {
                NewCreateAccountRequestModel.referenceId = response?.referenceId
            }
        }

        Log.d("referenceId",Gson().toJson(NewCreateAccountRequestModel.sms_referenceId+"  "+NewCreateAccountRequestModel.referenceId))


        setInputParamsData()
        binding.apply {
            btnVerify.setOnClickListener(this@OTPForgotPassword)
            btnResend.setOnClickListener(this@OTPForgotPassword)
            edtOtp.editText.addTextChangedListener {
                verifyCodeErrorMessage()
            }
        }
    }

    private fun setInputParamsData() {
        val profile_navData = navData as ProfileDetailModel?

        phoneCell = profile_navData?.personalInformation?.phoneCell ?: ""
        phoneCellCountryCode = profile_navData?.personalInformation?.phoneCellCountryCode ?: ""
        phoneDay = profile_navData?.personalInformation?.phoneDay ?: ""
        phoneDayCountryCode = profile_navData?.personalInformation?.phoneDayCountryCode ?: ""

        if (!isItMobileNumber) {
            phoneDay = data?.optionValue.toString()
            phoneDayCountryCode =
                phoneCountryCode
        } else {
            phoneCell = data?.optionValue.toString()
            phoneCellCountryCode =
                phoneCountryCode
        }

    }

    private fun verifyCodeErrorMessage() {
        btnEnabled = if (binding.edtOtp.getText().toString().trim().length < 6) {
            binding.edtOtp.setErrorText(getString(R.string.str_security_code_must_be_6_characters))
            false
        } else {
            binding.edtOtp.removeError()
            true
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
            observe(viewModelProfile.updateProfileApiVal, ::handleUpdateProfileDetail)

            observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetails)
            observe(dashboardViewModel.crossingHistoryVal, ::crossingHistoryResponse)


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
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.PROFILE_MANAGEMENT_EMAIL_CHANGE)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
                if(navFlowCall==PROFILE_MANAGEMENT){
                    findNavController().navigate(
                        R.id.action_otpForgotFragment_to_resetForgotPassword,
                        bundle
                    )
                }

            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    showError(binding.root, resource.errorMsg)
                }
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
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                        findNavController().navigate(
                            R.id.action_otpForgotFragment_to_resetForgotPassword,
                            bundle
                        )
                    } else {
                        showError(binding.root, resource.errorMsg)
                    }
                }
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    showError(binding.root, resource.errorMsg)
                }
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

                when (navFlowCall) {


                    FORGOT_PASSWORD_FLOW -> {
                        if (!timeFinish) {
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
                            showError(
                                binding.root,
                                getString(R.string.str_security_code_expired_message)
                            )
                        }
                    }

                    TWOFA -> {
                        hitTWOFAVerifyAPI()
                    }

                    else -> {
                        //otpSuccessRedirection()
                        confirmEmailCode()
                    }

                }
            }

            R.id.btn_Resend -> {
                val bundle = Bundle()

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

                    ACCOUNT_CREATION_MOBILE_FLOW -> {
                        AdobeAnalytics.setActionTrack(
                            "resend",
                            "login:forgot password:choose options:otp",
                            "forgot password",
                            "english",
                            "login", "Create Account",
                            sessionManager.getLoggedInUser()
                        )
                    }

                    FORGOT_PASSWORD_FLOW -> {
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

                    Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                        val data = navData as ProfileDetailModel?
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
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
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)

                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)

                    }

                    PROFILE_MANAGEMENT_2FA_CHANGE -> {
                        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arguments?.getParcelable(
                                Constants.NAV_DATA_KEY,
                                ProfileDetailModel::class.java
                            )
                        } else {
                            arguments?.getParcelable(Constants.NAV_DATA_KEY)
                        }

                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    }
                }
                bundle.putString(Constants.PHONE_COUNTRY_CODE, this.phoneCountryCode)
                bundle.putParcelable("data", data)

                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                bundle.putString(Constants.Edit_REQUEST_KEY, editRequest)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                bundle.putParcelable(Constants.REPLENISHMENTINFORMATION, replenishmentInformation)
                bundle.putBoolean(Constants.IS_MOBILE_NUMBER, isItMobileNumber)

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
            response?.referenceId ?: "",


            )
        viewModel.twoFAVerifyRequestCode(request)
    }

    private fun confirmEmailCode() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        if (phoneCountryCode.isNotEmpty()&&phoneCountryCode!=null){
            val request = ConfirmEmailRequest(
                response?.referenceId ?: "",
                phoneCountryCode+data?.optionValue,
                binding.edtOtp.getText().toString().trim()
            )
            createAccountViewModel.confirmEmailApi(request)

        }else{
            val request = ConfirmEmailRequest(
                response?.referenceId ?: "",
                data?.optionValue,
                binding.edtOtp.getText().toString().trim()
            )
            createAccountViewModel.confirmEmailApi(request)

        }

    }

    private fun loadUI() {
        when (data?.optionType) {

            Constants.SMS -> {

                binding.topTitle.text = getString(R.string.str_check_sms)

                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_sms)

                when (navFlowCall) {
                    ACCOUNT_CREATION_MOBILE_FLOW -> {
                        binding.messageReceivedTxt.text =
                            getString(R.string.wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            ) + "."

                    }

                    Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                        binding.messageReceivedTxt.text =
                            getString(R.string.wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            ) + "."

                    }

                    else -> {
                        binding.messageReceivedTxt.text =
                            getString(R.string.wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            ) + "."

                    }
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
                        getString(R.string.wehavesentanemail) + " " + Utils.maskEmail(data?.optionValue.toString())

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

                if (navFlowCall == TWOFA) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                    dashboardViewModel.getAccountDetailsData()


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
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(Constants.INVALID_TOKEN))|| status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
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
                            binding.edtOtp.setErrorText(getString(R.string.security_code_must_contain_correct_numbers))
                        }

                        2050 -> {
                            binding.edtOtp.setErrorText(getString(R.string.str_security_code_expired_message))
                        }

                        5260 -> {
                            binding.edtOtp.setErrorText(getString(R.string.str_for_your_security_we_have_locked))
                        }

                        else -> {
                            binding.edtOtp.setErrorText(status.errorMsg)
                        }
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
                    if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(Constants.INVALID_TOKEN))|| status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                        displaySessionExpireDialog(status.errorModel)
                    } else {
                        showError(binding.root, status.errorMsg)
                    }
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
                otpSuccessRedirection()
            }

            is Resource.DataError -> {
                when (resource.errorModel?.status) {

                    500 -> {
                        binding.edtOtp.setErrorText(getString(R.string.security_code_must_contain_correct_numbers))
                    }

                    900 -> {
                        binding.edtOtp.setErrorText(getString(R.string.str_security_code_expired_message))
                    }

                    Constants.TOKEN_FAIL -> {
                        displaySessionExpireDialog(resource.errorModel)
                    }
                    Constants.INTERNAL_SERVER_ERROR -> {
                        displaySessionExpireDialog(resource.errorModel)
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

    private fun otpSuccessRedirection() {
        val bundle = Bundle()
        when (navFlowCall) {
            ACCOUNT_CREATION_MOBILE_FLOW -> {
                NewCreateAccountRequestModel.smsSecurityCode =
                    binding.edtOtp.getText().toString().trim()
                if (editRequest.equals(EDIT_SUMMARY, true)) {
                    when (navFlowFrom) {
                        Constants.OPTSMS -> {
                            findNavController().navigate(
                                R.id.action_optSms_forgotOtpFragment_to_createAccountSummaryFragment,
                                bundle
                            )
                        }

                        Constants.TwoStepVerification -> {

                            findNavController().navigate(
                                R.id.action_twoStep_forgotOtpFragment_to_createAccountSummaryFragment,
                                bundle
                            )
                        }

                        else -> {
                            findNavController().navigate(
                                R.id.action_forgotOtpFragment_to_createAccountSummaryFragment
                            )

                        }
                    }
                }else if(editRequest.equals(EDIT_MOBILE,true)){
                    findNavController().navigate(
                        R.id.action_forgotOtpFragment_to_createAccountSummaryFragment
                    )
                } else if (editRequest.equals(EDIT_ACCOUNT_TYPE, true)) {
                    findNavController().navigate(
                        R.id.action_AccountChangeType_forgotPassword_to_vehicleListFragment,
                        bundle
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_otpForgotFragment_to_createVehicleFragment
                    )
                }
            }

            Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                loader?.show(
                    requireActivity().supportFragmentManager,
                    Constants.LOADER_DIALOG
                )
                dashboardViewmodel.personalInformationData.value?.let { updateSmsOption(it) }

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
                    updateStandardUserProfile(data?.personalInformation, data?.accountInformation)
                } else {
                    updateBusinessUserProfile(data?.personalInformation, data?.accountInformation)
                }
            }

            else -> {
                response?.code = binding.edtOtp.getText().toString()
                bundle.putParcelable("data", response)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                NewCreateAccountRequestModel.emailSecurityCode =
                    binding.edtOtp.editText.text.toString()

                when (navFlowCall) {
                    EDIT_SUMMARY -> {
                        when (navFlowFrom) {
                            Constants.OPTSMS -> {
                                findNavController().navigate(
                                    R.id.action_optSms_forgotOtpFragment_to_createAccountSummaryFragment,
                                    bundle
                                )
                            }

                            Constants.TwoStepVerification -> {
                                findNavController().navigate(
                                    R.id.action_twoStep_forgotOtpFragment_to_createAccountSummaryFragment,
                                    bundle
                                )
                            }
                            else -> {
                                findNavController().navigate(
                                    R.id.action_email_forgotOtpFragment_to_createAccountSummaryFragment,
                                    bundle
                                )
                            }
                        }
                    }

                    EDIT_ACCOUNT_TYPE -> {
                        findNavController().navigate(
                            R.id.action_forgotOtpFragment_to_createPasswordFragment,
                            bundle
                        )
                    }
                    PROFILE_MANAGEMENT -> {
                        updateProfileEmail(HomeActivityMain.accountDetailsData!!.personalInformation, HomeActivityMain.accountDetailsData?.accountInformation)
                    }

                    else -> {
                        findNavController().navigate(
                            R.id.action_forgotOtpFragment_to_createPasswordFragment,
                            bundle
                        )
                    }

                }
            }
        }


    }

    private fun updateBusinessUserProfile(
        dataModel: com.conduent.nationalhighways.data.model.profile.PersonalInformation?,
        accountInformation: com.conduent.nationalhighways.data.model.profile.AccountInformation?
    ) {
        dataModel?.run {
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
                phoneCell = phoneCell,
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = "",
                fein = accountInformation?.fein,
                businessName = customerName,
                phoneCellCountryCode = phoneCellCountryCode,
                phoneDayCountryCode = phoneDayCountryCode,
                mfaEnabled = accountInformation?.mfaEnabled

            )

            viewModelProfile.updateUserDetails(request)
        }


    }

    private fun updateStandardUserProfile(
        dataModel: com.conduent.nationalhighways.data.model.profile.PersonalInformation?,
        accountInformation: com.conduent.nationalhighways.data.model.profile.AccountInformation?
    ) {

        dataModel?.run {
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
                phoneCell = phoneCell,
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = "",
                phoneCellCountryCode = phoneCellCountryCode,
                phoneDayCountryCode = phoneDayCountryCode,
                mfaEnabled = accountInformation?.mfaEnabled
            )

            viewModelProfile.updateUserDetails(request)
        }

    }

    private fun updateProfileEmail(
        dataModel: com.conduent.nationalhighways.data.model.account.PersonalInformation?,
        accountInformation: com.conduent.nationalhighways.data.model.account.AccountInformation?
    ) {

        dataModel?.run {
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
                phoneCell = phoneDay,
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = HomeActivityMain.accountDetailsData?.accountInformation?.smsOption,
                phoneEvening = "",
                phoneCellCountryCode = phoneDayCountryCode,
                phoneDayCountryCode = phoneDayCountryCode,
                mfaEnabled = accountInformation?.mfaEnabled,
                securityCode = binding.edtOtp.getText().toString().trim(), referenceId = arguments?.getString(Constants.REFERENCE_ID),
                correspDeliveryMode = accountInformation?.stmtDelivaryMethod, businessName = accountInformation?.businessName
            )

            viewModelProfile.updateUserDetails(request)
        }

    }

    private fun updateSmsOption(personalInformationModel: PersonalInformation) {
//        loader?.show(
//            requireActivity().supportFragmentManager,
//            Constants.LOADER_DIALOG
//        )

        personalInformationModel.run {
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
                phoneCell = data?.optionValue,
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = "",
                phoneCellCountryCode = phoneCountryCode,
                phoneDayCountryCode = phoneDayCountryCode
            )

            viewModelProfile.updateUserDetails(request)
        }
    }


    private fun handleAccountDetails(status: Resource<AccountResponse?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (status) {
            is Resource.Success -> {
                personalInformation = status.data?.personalInformation
                accountInformation = status.data?.accountInformation
                replenishmentInformation = status.data?.replenishmentInformation


                if (status.data?.accountInformation?.status.equals(Constants.SUSPENDED, true)) {
                    val intent = Intent(requireActivity(), AuthActivity::class.java)
                    intent.putExtra(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
                    intent.putExtra(Constants.NAV_FLOW_FROM, navFlowFrom)
                    intent.putExtra(Constants.CROSSINGCOUNT, "")
                    intent.putExtra(Constants.PERSONALDATA, personalInformation)
                    intent.putExtra(
                        Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
                    )
                    startActivity(intent)


                } else {
                    crossingHistoryApi()
                }


            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(Constants.INVALID_TOKEN))|| status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    AdobeAnalytics.setLoginActionTrackError(
                        "login",
                        "login",
                        "login",
                        "english",
                        "login",
                        "",
                        "true",
                        "manual",
                        sessionManager.getLoggedInUser()
                    )
                }
            }

            else -> {

            }
        }

    }

    private fun crossingHistoryApi() {
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 0,
            transactionType = Constants.TOLL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "",
            endDate = DateUtils.currentDate() ?: ""
        )
        dashboardViewModel.crossingHistoryApiCall(request)
    }

    private fun crossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.transactionList != null) {
                        navigateWithCrossing(it.transactionList.count ?: 0)

                    } else {
                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                            putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                            putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        }

                    }

                }
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                        putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                        putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                    }
                }
            }

            else -> {
            }
        }
    }

    private fun navigateWithCrossing(count: Int) {


        if (count > 0) {


            val intent = Intent(requireActivity(), AuthActivity::class.java)
            intent.putExtra(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
            intent.putExtra(Constants.CROSSINGCOUNT, count.toString())
            intent.putExtra(Constants.PERSONALDATA, personalInformation)
            intent.putExtra(Constants.NAV_FLOW_FROM, navFlowFrom)


            intent.putExtra(
                Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
            )
            startActivity(intent)

        } else {
            requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            }
        }


    }


}