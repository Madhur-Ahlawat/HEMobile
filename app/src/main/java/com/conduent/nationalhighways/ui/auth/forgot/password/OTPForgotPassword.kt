package com.conduent.nationalhighways.ui.auth.forgot.password

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.LRDSResponse
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpResp
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModelList
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsResp
import com.conduent.nationalhighways.data.model.createaccount.ConfirmEmailRequest
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.profile.ReplenishmentInformation
import com.conduent.nationalhighways.databinding.FragmentForgotOtpchangesBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.receiver.SmsBroadcastReceiver
import com.conduent.nationalhighways.ui.account.biometric.BiometricActivity
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CommunicationPrefsViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.OTPReceiver
import com.conduent.nationalhighways.utils.Utility
import com.conduent.nationalhighways.utils.Utility.REQ_USER_CONSENT
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNT_CREATION_MOBILE_FLOW
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_MOBILE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.FORGOT_PASSWORD_FLOW
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.TWOFA
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OTPForgotPassword : BaseFragment<FragmentForgotOtpchangesBinding>(), View.OnClickListener {

    private var myOTPReceiver: OTPReceiver? = null
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
    private var accountStatus: String = ""
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    var hasFaceBiometric = false
    var hasTouchBiometric = false

    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotOtpchangesBinding =
        FragmentForgotOtpchangesBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        } else if (requireActivity() is CreateAccountActivity) {
            (requireActivity() as CreateAccountActivity).focusToolBarCreateAccount()
        }
        hasFaceBiometric = Utils.hasFaceId(requireContext())
        hasTouchBiometric = Utils.hasTouchId(requireContext())
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
//        binding.edtOtp.editText.setText("101010")
//        binding.btnVerify.isEnabled = true
//        binding.btnVerify.isClickable = true
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_USER_CONSENT -> {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    //That gives all message to us. We need to get the code from inside with regex
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val code = message?.let { Utility.fetchVerificationCode(it) }
                    binding.edtOtp.editText.setText(code.toString())
                }
            }
        }
    }

    private fun startSMSRetrieverClient(context: Context) {
        val client: SmsRetrieverClient = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener { _ ->
            Log.e("Atiar OTP Receiver", "startSMSRetrieverClient addOnSuccessListener")
        }
        task.addOnFailureListener { e ->
            Log.e(
                "Atiar OTP Receiver",
                "startSMSRetrieverClient addOnFailureListener" + e.stackTrace
            )
        }
    }

    override fun onStart() {
        super.onStart()
        registerOTPReceiver()
    }

    override fun onStop() {
        super.onStop()
//        requireActivity().unregisterReceiver(smsBroadcastReceiver)
        requireActivity().unregisterReceiver(myOTPReceiver)
    }

    override fun initCtrl() {
//        startSmsUserConsent(requireActivity())
        startSMSRetrieverClient(requireActivity())
        editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()
        phoneCountryCode = arguments?.getString(Constants.PHONE_COUNTRY_CODE, "").toString()


        if (arguments?.containsKey(Constants.IS_MOBILE_NUMBER) == true) {
            isItMobileNumber = arguments?.getBoolean(Constants.IS_MOBILE_NUMBER) ?: false
        }
        if (arguments?.containsKey(Constants.NAV_FLOW_KEY) == true) {
            navFlowCall = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }

        if (arguments?.getParcelable<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION) != null) {
            replenishmentInformation =
                arguments?.getParcelable(Constants.REPLENISHMENTINFORMATION)
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

        Log.d(
            "referenceId",
            Gson().toJson(NewCreateAccountRequestModel.sms_referenceId + "  " + NewCreateAccountRequestModel.referenceId)
        )


        setInputParamsData()
        binding.apply {
            btnVerify.setOnClickListener(this@OTPForgotPassword)
            btnResend.setOnClickListener(this@OTPForgotPassword)
            edtOtp.editText.addTextChangedListener {
                verifyCodeErrorMessage()
            }
        }
    }

    private fun registerOTPReceiver() {
        myOTPReceiver = OTPReceiver()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        ContextCompat.registerReceiver(
            requireActivity(),
            myOTPReceiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
//        smsBroadcastReceiver = SmsBroadcastReceiver().also {
//            it.smsBroadcastReceiverListener =
//                object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
//                    override fun onSuccess(intent: Intent?) {
//                        intent?.let { intent ->
//                            startActivityForResult(
//                                intent,
//                                REQ_USER_CONSENT
//                            )
//                        }
//                    }
//
//                    override fun onFailure() {
//                    }
//                }
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().registerReceiver(
                    myOTPReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
                    Context.RECEIVER_EXPORTED
                )
            } else {
                requireActivity().registerReceiver(
                    myOTPReceiver,
                    IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), Context.RECEIVER_EXPORTED
                )
            }
        }

        //Receiving the OTP
        myOTPReceiver!!.init(object : OTPReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                Log.e("OTP ", "OTP Received  $otp")
                val code = otp?.let { Utility.fetchVerificationCode(it) }
                binding.edtOtp.setText(code.toString())                // when its true automatically run the function which
                // supposed to be run by clicking verify button
                // verifyNumberOnClick.value = true
            }

            override fun onOTPTimeOut() {
                Log.e("OTP ", "Timeout")
            }
        })

    }

    private fun setInputParamsData() {
        val profileNavdata = navData as ProfileDetailModel?

        phoneCell = profileNavdata?.personalInformation?.phoneCell ?: ""
        phoneCellCountryCode = profileNavdata?.personalInformation?.phoneCellCountryCode ?: ""
        phoneDay = profileNavdata?.personalInformation?.phoneDay ?: ""
        phoneDayCountryCode = profileNavdata?.personalInformation?.phoneDayCountryCode ?: ""

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
            observe(dashboardViewModel.lrdsVal, ::handleLrdsResposne)
            observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetails)
            observe(dashboardViewModel.crossingHistoryVal, ::crossingHistoryResponse)
            observe(
                communicationPrefsViewModel.updateCommunicationPrefs,
                ::updateCommunicationSettingsPrefs
            )

        }


        isViewCreated = true

    }

    private fun updateCommunicationSettingsPrefs(resource: Resource<CommunicationPrefsResp?>?) {

        when (resource) {
            is Resource.Success -> {
                updateSmsOption(
                    dashboardViewmodel.personalInformationData.value,
                    dashboardViewmodel.accountInformationData.value
                )
            }

            is Resource.DataError -> {
                binding.edtOtp.editText.isFocusable = true
                binding.edtOtp.editText.isFocusableInTouchMode = true
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    showError(binding.root, resource.errorMsg)
                }

            }

            else -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }
            }
        }
    }


    private fun handleLrdsResposne(resource: Resource<LRDSResponse?>?) {
        when (resource) {

            is Resource.Success -> {

                if (resource.data?.srApprovalStatus?.uppercase().equals("APPROVED")) {
                    requireActivity().startNewActivityByClearingStack(LandingActivity::class.java) {
                        putString(Constants.SHOW_SCREEN, Constants.LRDS_SCREEN)
                    }
                } else {
                    dashboardViewModel.getAccountDetailsData()
                }
            }

            is Resource.DataError -> {
                binding.edtOtp.editText.isFocusable = true
                binding.edtOtp.editText.isFocusableInTouchMode = true
                dashboardViewModel.getAccountDetailsData()
            }

            else -> {

            }
        }
    }


    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {

                if (navFlowCall == PROFILE_MANAGEMENT_COMMUNICATION_CHANGED) {
                    sessionManager.saveSmsOption("Y")
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.NAV_FLOW_KEY,
                        PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
                    )
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    findNavController().navigate(
                        R.id.action_otpForgotFragment_to_resetForgotPassword,
                        bundle
                    )
                } else {
                    val data = navData as ProfileDetailModel?
                    val bundle = Bundle()
                    if (navFlowFrom == Constants.AccountType_EMAIL) {
                        bundle.putString(
                            Constants.NAV_FLOW_FROM,
                            Constants.PROFILE_MANAGEMENT_EMAIL_CHANGE
                        )
                    }
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    if (navFlowCall == PROFILE_MANAGEMENT ||
                        navFlowCall == PROFILE_MANAGEMENT_MOBILE_CHANGE ||
                        navFlowCall == PROFILE_MANAGEMENT_2FA_CHANGE
                    ) {
                        findNavController().navigate(
                            R.id.action_otpForgotFragment_to_resetForgotPassword,
                            bundle
                        )
                    }
                }
            }

            is Resource.DataError -> {
                binding.edtOtp.editText.isFocusable = true
                binding.edtOtp.editText.isFocusableInTouchMode = true
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
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
                binding.edtOtp.editText.isFocusable = false
                binding.edtOtp.editText.isFocusableInTouchMode = false
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

                    PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                        val data = navData as ProfileDetailModel?
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    }

                    PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
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
        if (phoneCountryCode.isNotEmpty() && phoneCountryCode != null) {
            val request = ConfirmEmailRequest(
                response?.referenceId ?: "",
                phoneCountryCode + data?.optionValue,
                binding.edtOtp.getText().toString().trim()
            )
            createAccountViewModel.confirmEmailApi(request)

        } else {
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
                        binding.messageReceivedTxt.contentDescription =
                            getString(R.string.content_description_wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            )

                    }

                    PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                        binding.messageReceivedTxt.text =
                            getString(R.string.wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            ) + "."

                        binding.messageReceivedTxt.contentDescription =
                            getString(R.string.content_description_wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            )

                    }

                    else -> {
                        binding.messageReceivedTxt.text =
                            getString(R.string.wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            ) + "."

                        binding.messageReceivedTxt.contentDescription =
                            getString(R.string.content_description_wehavesentatextmessageto) + " " + Utils.maskPhoneNumber(
                                data?.optionValue.toString()
                            )

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
        binding.edtOtp.editText.requestFocus()
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
                    dashboardViewModel.getLRDSResponse()
                } else {
                    response?.code = binding.edtOtp.getText().toString()
                    bundle.putParcelable("data", response)
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)

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
                binding.edtOtp.editText.isFocusable = true
                binding.edtOtp.editText.isFocusableInTouchMode = true
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
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

                        1308 -> {
                            binding.edtOtp.setErrorText(getString(R.string.str_password_match_current_password))
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
                    binding.edtOtp.editText.isFocusable = true
                    binding.edtOtp.editText.isFocusableInTouchMode = true
                    if (checkSessionExpiredOrServerError(status.errorModel)
                    ) {
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
                binding.edtOtp.editText.isFocusable = true
                binding.edtOtp.editText.isFocusableInTouchMode = true
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    when (resource.errorModel?.errorCode) {

                        2051 -> {
                            binding.edtOtp.setErrorText(getString(R.string.security_code_must_contain_correct_numbers))
                        }

                        1 -> {
                            binding.edtOtp.setErrorText(getString(R.string.security_code_must_contain_correct_numbers))
                        }

                        2050 -> {
                            binding.edtOtp.setErrorText(getString(R.string.str_security_code_expired_message))
                        }

                        2 -> {
                            binding.edtOtp.setErrorText(getString(R.string.str_security_code_expired_message))
                        }

                        5260 -> {
                            binding.edtOtp.setErrorText(getString(R.string.str_for_your_security_we_have_locked))
                        }

                        1308 -> {
                            binding.edtOtp.setErrorText(getString(R.string.str_password_match_current_password))
                        }

                        else -> {
                            binding.edtOtp.setErrorText(resource.errorMsg)
                        }
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
                } else if (editRequest.equals(EDIT_MOBILE, true)) {
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

            PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                loader?.show(
                    requireActivity().supportFragmentManager,
                    Constants.LOADER_DIALOG
                )
                val model = CommunicationPrefsRequestModel(ArrayList())

                val communicationList =
                    ArrayList<CommunicationPrefsRequestModelList>()
                for (i in 0 until dashboardViewmodel.communicationPreferenceData.value.orEmpty().size) {
                    val communicationModel =
                        dashboardViewmodel.communicationPreferenceData.value?.get(
                            i
                        )

                    val smsFlag = if (communicationModel?.category?.lowercase()
                            .equals("standard notification")
                    ) {
                        "Y"
                    } else {
                        communicationModel?.smsFlag ?: "Y"
                    }

                    communicationList.add(
                        CommunicationPrefsRequestModelList(
                            communicationModel?.id,
                            communicationModel?.category,
                            communicationModel?.oneMandatory,
                            communicationModel?.defEmail,
                            communicationModel?.emailFlag,
                            communicationModel?.mailFlag,
                            communicationModel?.defSms,
                            smsFlag,
                            communicationModel?.defVoice,
                            communicationModel?.voiceFlag,
                            communicationModel?.pushNotFlag,
                            communicationModel?.defPushNot,
                            communicationModel?.defMail
                        )
                    )

                }
                model.categoryList = communicationList
                communicationPrefsViewModel.updateCommunicationPrefs(model)


            }

            PROFILE_MANAGEMENT_2FA_CHANGE, PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                loader?.show(
                    requireActivity().supportFragmentManager,
                    Constants.LOADER_DIALOG
                )
                val data = navData as ProfileDetailModel?

                updateProfileDetails(data?.personalInformation, data?.accountInformation)
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
                        updateProfileEmail(
                            HomeActivityMain.accountDetailsData?.personalInformation,
                            HomeActivityMain.accountDetailsData?.accountInformation
                        )
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


    private fun updateProfileDetails(
        dataModel: PersonalInformation?,
        accountInformation: AccountInformation?
    ) {
        var mfaEnabled = Utils.returnMfaStatus(accountInformation?.mfaEnabled ?: "")
        var smsOption = accountInformation?.smsOption
        if (navFlowCall == PROFILE_MANAGEMENT_2FA_CHANGE) {
            mfaEnabled = "Y"
        }
        val request = Utils.returnEditProfileModel(
            accountInformation?.businessName,
            accountInformation?.fein,
            dataModel?.firstName,
            dataModel?.lastName,
            dataModel?.addressLine1,
            dataModel?.addressLine2,
            dataModel?.city,
            dataModel?.state,
            dataModel?.zipcode,
            dataModel?.zipCodePlus,
            dataModel?.country,
            dataModel?.emailAddress,
            dataModel?.primaryEmailStatus,
            dataModel?.pemailUniqueCode,
            dataModel?.phoneCell,
            dataModel?.phoneCellCountryCode,
            dataModel?.phoneDay,
            dataModel?.phoneDayCountryCode,
            dataModel?.fax,
            smsOption,
            dataModel?.eveningPhone,
            accountInformation?.stmtDelivaryMethod,
            accountInformation?.stmtDelivaryInterval,
            mfaEnabled,
            accountType = accountInformation?.accountType,
        )
        viewModelProfile.updateUserDetails(request)


    }

    private fun updateProfileEmail(
        dataModel: PersonalInformation?,
        accountInformation: AccountInformation?
    ) {


        val request = Utils.returnEditProfileModel(
            accountInformation?.businessName,
            accountInformation?.fein,
            dataModel?.firstName,
            dataModel?.lastName,
            dataModel?.addressLine1,
            dataModel?.addressLine2,
            dataModel?.city,
            dataModel?.state,
            dataModel?.zipcode,
            dataModel?.zipCodePlus,
            dataModel?.country,
            dataModel?.emailAddress,
            Constants.PENDING_STATUS,
            dataModel?.pemailUniqueCode,
            dataModel?.phoneCell,
            dataModel?.phoneCellCountryCode,
            dataModel?.phoneDay,
            dataModel?.phoneDayCountryCode,
            dataModel?.fax,
            accountInformation?.smsOption,
            dataModel?.eveningPhone,
            accountInformation?.stmtDelivaryMethod,
            accountInformation?.stmtDelivaryInterval,
            Utils.returnMfaStatus(accountInformation?.mfaEnabled ?: ""),
            accountType = accountInformation?.accountType,
            securityCode = binding.edtOtp.getText().toString().trim(),
            referenceId = arguments?.getString(Constants.REFERENCE_ID),
        )

        viewModelProfile.updateUserDetails(request)


    }

    private fun updateSmsOption(
        personalInformationModel: PersonalInformation?,
        accountInformation: AccountInformation?
    ) {

        val request = Utils.returnEditProfileModel(
            accountInformation?.businessName,
            accountInformation?.fein,
            personalInformationModel?.firstName,
            personalInformationModel?.lastName,
            personalInformationModel?.addressLine1,
            personalInformationModel?.addressLine2,
            personalInformationModel?.city,
            personalInformationModel?.state,
            personalInformationModel?.zipcode,
            personalInformationModel?.zipCodePlus,
            personalInformationModel?.country,
            personalInformationModel?.emailAddress,
            personalInformationModel?.primaryEmailStatus,
            personalInformationModel?.pemailUniqueCode,
            data?.optionValue,
            phoneCountryCode,
            phoneDay,
            phoneDayCountryCode,
            personalInformationModel?.fax,
            "Y",
            personalInformationModel?.eveningPhone,
            accountInformation?.stmtDelivaryMethod,
            accountInformation?.stmtDelivaryInterval,
            Utils.returnMfaStatus(accountInformation?.mfaEnabled ?: ""),
            accountType = accountInformation?.accountType,
        )


        viewModelProfile.updateUserDetails(request)
    }


    private fun handleAccountDetails(status: Resource<ProfileDetailModel?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (status) {
            is Resource.Success -> {
                personalInformation = status.data?.personalInformation
                accountInformation = status.data?.accountInformation
                replenishmentInformation = status.data?.replenishmentInformation
                accountStatus = status.data?.accountInformation?.status ?: ""

                if (accountStatus.equals(Constants.SUSPENDED, true)) {
                    crossingHistoryApi()
                } else {
                    if (!(sessionManager.hasAskedForBiometric() && sessionManager.fetchTouchIdEnabled())) {
                        sessionManager.saveHasAskedForBiometric(true)
                        if (hasTouchBiometric && hasFaceBiometric) {
                            displayBiometricDialog(getString(R.string.str_enable_face_ID_fingerprint))

                        } else if (hasFaceBiometric) {
                            displayBiometricDialog(getString(R.string.str_enable_face_ID))

                        } else {
                            displayBiometricDialog(getString(R.string.str_enable_touch_ID))

                        }
                    } else {
                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                            putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                            putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        }
                    }

                }


            }

            is Resource.DataError -> {
                binding.edtOtp.editText.isFocusable = true
                binding.edtOtp.editText.isFocusableInTouchMode = true
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
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

    private fun displayBiometricDialog(title: String) {
        displayCustomMessage(title,
            getString(R.string.doyouwantenablebiometric),
            getString(R.string.enablenow_lower_case),
            getString(R.string.enablelater_lower_case),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    val intent = Intent(requireActivity(), BiometricActivity::class.java)
                    intent.putExtra(TWOFA, sessionManager.getTwoFAEnabled())
                    intent.putExtra(Constants.NAV_FLOW_FROM, navFlowCall)
                    intent.putExtra(Constants.NAV_FLOW_KEY, navFlowFrom)

                    startActivity(intent)


                    //dialog.dismiss()

                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                        putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                    }
                }
            })
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
                    if (accountStatus.equals(Constants.SUSPENDED, true)) {
                        navigateWithCrossing(it.transactionList?.count ?: 0)
                    } else {
                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                            putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                            putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        }

                    }

                }
            }

            is Resource.DataError -> {
                binding.edtOtp.editText.isFocusable = true
                binding.edtOtp.editText.isFocusableInTouchMode = true
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    if (accountStatus.equals(Constants.SUSPENDED, true)) {
                        navigateWithCrossing(0)
                    } else {
                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                            putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                            putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        }

                    }
                }
            }

            else -> {
            }
        }
    }

    private fun navigateWithCrossing(count: Int) {
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        intent.putExtra(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
        intent.putExtra(Constants.CROSSINGCOUNT, count.toString())
        intent.putExtra(Constants.PERSONALDATA, personalInformation)
        intent.putExtra(Constants.ACCOUNTINFORMATION, accountInformation)
        intent.putExtra(Constants.NAV_FLOW_FROM, navFlowFrom)
        intent.putExtra(
            Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
        )
        startActivity(intent)
    }

}