package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Build
import android.os.Bundle
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
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentResendCodeBinding
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResendCodeFragment : BaseFragment<FragmentResendCodeBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null
    private var data: RequestOTPModel? = null
    private var isViewCreated: Boolean = false
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var response: SecurityCodeResponseModel? = null
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private lateinit var editRequest: String

    private var phoneCountryCode: String = ""

    @Inject
    lateinit var sessionManager: SessionManager
    private var isItMobileNumber: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentResendCodeBinding = FragmentResendCodeBinding.inflate(inflater, container, false)


    override fun initCtrl() {

        if(arguments?.containsKey(Constants.PHONE_COUNTRY_CODE) == true){
            phoneCountryCode = arguments?.getString(Constants.PHONE_COUNTRY_CODE, "").toString()
        }
        if (arguments?.containsKey(Constants.IS_MOBILE_NUMBER) == true) {
            isItMobileNumber = arguments?.getBoolean(Constants.IS_MOBILE_NUMBER) ?: false
        }

        if (arguments != null) {
            data = arguments?.getParcelable("data")
        }
        editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION)
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }

        if (arguments?.getParcelable<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION) != null) {
            replenishmentInformation =
                arguments?.getParcelable<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION)
        }
    }

    override fun init() {

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }



        if (navFlowCall == Constants.ACCOUNT_CREATION_EMAIL_FLOW || navFlowCall == Constants.ACCOUNT_CREATION_MOBILE_FLOW) {
            if (data?.optionType == Constants.EMAIL) {
                binding.subTitle.text = getString(
                    R.string.resend_code_email,
                    Utils.hiddenEmailText(data?.optionValue.toString())
                )

            } else {
                binding.subTitle.text = getString(
                    R.string.resend_code_text,
                    Utils.maskPhoneNumber(data?.optionValue.toString())
                )

            }

        } else {
            if (data?.optionType == Constants.EMAIL) {
                binding.subTitle.text = getString(
                    R.string.resend_code_email,
                    Utils.hiddenEmailText(data?.optionValue.toString())
                )

            } else {
                binding.subTitle.text = getString(
                    R.string.resend_code_text,
                    Utils.maskPhoneNumber(data?.optionValue.toString())
                )

            }

        }
        binding.apply {
            btnVerify.setOnClickListener(this@ResendCodeFragment)
        }
    }

    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.otp, ::handleOTPResponse)
            observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)

        }



        isViewCreated = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify -> {

                when (navFlowCall) {
                    Constants.FORGOT_PASSWORD_FLOW -> {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                        viewModel.requestOTP(data)
                    }

                    Constants.EDIT_SUMMARY -> {
                        hitApi()
                    }

                    Constants.ACCOUNT_CREATION_EMAIL_FLOW -> {
                        hitApi()
                    }

                    Constants.ACCOUNT_CREATION_MOBILE_FLOW -> {
                        hitApi()
                    }

                    Constants.TWOFA -> {
                        viewModel.twoFARequestOTP(data)
                    }

                    else -> {
                        hitApi()

                        /* val bundle = Bundle()
                         bundle.putParcelable("data", data)
                         bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)

                         findNavController().navigate(
                             R.id.action_resenedCodeFragment_to_otpFragment,
                             bundle
                         )*/
                    }
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
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                bundle.putString(Constants.Edit_REQUEST_KEY, editRequest)

                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                bundle.putParcelable(Constants.REPLENISHMENTINFORMATION, replenishmentInformation)
                bundle.putString(Constants.PHONE_COUNTRY_CODE, phoneCountryCode)
                bundle.putBoolean(Constants.IS_MOBILE_NUMBER,isItMobileNumber)

                findNavController().navigate(
                    R.id.action_resenedCodeFragment_to_otpFragment,
                    bundle
                )


            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(Constants.INVALID_TOKEN))|| status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }

            else -> {
            }
        }

    }


    private fun hitApi() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = EmailVerificationRequest(
            data?.optionType,
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
                bundle.putParcelable("data", RequestOTPModel(data?.optionType, data?.optionValue))

                bundle.putParcelable(
                    "response",
                    SecurityCodeResponseModel(
                        resource.data?.emailStatusCode,
                        0L,
                        resource.data?.referenceId,
                        true
                    )
                )

                if (navFlowCall == Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE) {
                    val data = navData as ProfileDetailModel?
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)

                }else if(navFlowCall==Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED)
                 {
                    val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        arguments?.getParcelable(
                            Constants.NAV_DATA_KEY,
                            CommunicationPrefsRequestModel::class.java
                        )
                    } else {
                        arguments?.getParcelable(Constants.NAV_DATA_KEY)
                    }

                     bundle.putParcelable(Constants.NAV_DATA_KEY, data)

                 } else if (navFlowCall == Constants.PROFILE_MANAGEMENT_2FA_CHANGE) {
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

                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.Edit_REQUEST_KEY, editRequest)
                bundle.putString(Constants.PHONE_COUNTRY_CODE, phoneCountryCode)
                bundle.putBoolean(Constants.IS_MOBILE_NUMBER,isItMobileNumber)

                findNavController().navigate(
                    R.id.action_resenedCodeFragment_to_otpFragment,
                    bundle
                )
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
    }


}