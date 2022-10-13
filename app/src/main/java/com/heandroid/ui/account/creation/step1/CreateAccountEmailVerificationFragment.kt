package com.heandroid.ui.account.creation.step1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.EmailValidationModel
import com.heandroid.data.model.account.UserNameCheckReq
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.databinding.FragmentCreateAccountEmailVerificationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.landing.LandingActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.loader.OnRetryClickListener
import com.heandroid.utils.common.*
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountEmailVerificationFragment :
    BaseFragment<FragmentCreateAccountEmailVerificationBinding>(), View.OnClickListener,
    OnRetryClickListener {

    private var loader: LoaderDialog? = null
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var requestModel: CreateAccountRequestModel? = null
    private var isEditEmail: Int? = null
    private var count = 1
    private var isCodeCheckApi = true
    private var isLiveDateClicked = false
    private var isNavigate = false


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountEmailVerificationBinding.inflate(inflater, container, false)

    override fun init() {

        binding.etEmail.setText(
            requireActivity().intent?.getStringExtra(Constants.EMAIL) ?: "",
            TextView.BufferType.EDITABLE
        )

        requestModel = CreateAccountRequestModel(
            referenceId = 0,
            securityCd = 0,
            accountType = "",
            tcAccepted = "Y",
            firstName = "",
            lastName = "",
            address1 = "",
            city = "",
            stateType = "",
            countryType = "UK",
            zipCode1 = "",
            emailAddress = "",
            cellPhone = "",
            cellPhoneCountryCode = "+44",
            eveningPhone = "",
            eveningPhoneCountryCode = "+44",
            smsOption = "Y",
            password = "",
            digitPin = "",
            correspDeliveryMode = "EMAIL",
            correspDeliveryFrequency = "MONTHLY",
            companyName = "",
            fein = "",
            nonRevenueOption = "Y",
            ftvehicleList = null,
            creditCardType = "",
            creditCardNumber = "",
            maskedNumber = "",
            creditCExpMonth = "",
            creditCExpYear = "",
            securityCode = "",
            cardFirstName = "",
            cardMiddleName = "",
            cardLastName = "",
            billingAddressLine1 = "",
            billingAddressLine2 = "",
            cardCity = "",
            cardStateType = "",
            cardZipCode = "",
            thresholdAmount = null,
            replenishmentAmount = null,
            transactionAmount = null,
            planType = null,
            enable = false,
            vehicleNo = "",
            mNoOfVehicles = "",
            mNoOfCrossings = "",
            plateCountryType = ""
        )
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL) == true) {
            isEditEmail = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL)
        }

        if (arguments?.containsKey(CREATE_ACCOUNT_DATA) == true) {
            requestModel = arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        }

        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 6)
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
        observe(createAccountViewModel.userNameAvailabilityCheck, ::checkUserNameAvailable)
    }

    private fun checkUserNameAvailable(resource: Resource<Boolean?>?) {
        isCodeCheckApi = true
        if (isLiveDateClicked) {
            when (resource) {
                is Resource.Success -> {
                    if (resource.data == true) {
                        count = 1
                        val request =
                            EmailVerificationRequest(
                                Constants.EMAIL_SELECTION_TYPE,
                                binding.model?.email ?: ""
                            )
                        isNavigate = true
                        createAccountViewModel.emailVerificationApi(request)
                    } else {
                        if (loader?.isVisible == true) {
                            loader?.dismiss()
                        }
                        showError(binding.root, getString(R.string.str_username_exits_message))
                    }

                }
                is Resource.DataError -> {
                    if (loader?.isVisible == true) {
                        loader?.dismiss()
                    }
                    if (resource.errorMsg.contains("Connect your VPN", true)) {
                        if (count > Constants.RETRY_COUNT) {
                            requireActivity().startActivity(
                                Intent(context, LandingActivity::class.java)
                                    .putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        ErrorUtil.showRetry(this)
                    } else {
                        showError(binding.root, resource.errorMsg)
                    }
                }
                else -> {

                }
            }
            isLiveDateClicked = false
        }

    }


    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        isCodeCheckApi = false
        if (isNavigate) {
            when (resource) {
                is Resource.Success -> {
                    count = 1
                    if (resource.data?.statusCode?.equals("0") == true) {
                        requestModel?.emailAddress = binding.etEmail.text.toString().trim()
                        requestModel?.referenceId = resource.data.referenceId?.toLongOrNull()
                        val bundle = Bundle().apply {
                            putParcelable(CREATE_ACCOUNT_DATA, requestModel)
                            isEditEmail?.let {
                                putInt(
                                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL,
                                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL_KEY
                                )
                            }
                        }
                        findNavController().navigate(
                            R.id.action_emailVerification_to_confirmEmailFragment,
                            bundle
                        )
                    } else {
                        showError(binding.root, resource.data?.message)
                    }
                }
                is Resource.DataError -> {
                    if (resource.errorMsg.contains("Connect your VPN", true)) {
                        if (count > Constants.RETRY_COUNT) {
                            requireActivity().startActivity(
                                Intent(context, LandingActivity::class.java)
                                    .putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        ErrorUtil.showRetry(this)
                    } else {
                        showError(binding.root, resource.errorMsg)
                    }
                }
                else -> {

                }
            }
            isNavigate = false
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_action -> {
                hideKeyboard()
                isLiveDateClicked = true
                sendEmailVerificationRequest()
            }
        }
    }

    private fun sendEmailVerificationRequest() {
        loader?.show(requireActivity().supportFragmentManager, "")
        createAccountViewModel.userNameAvailabilityCheck(
            UserNameCheckReq(
                binding.model?.email ?: ""
            )
        )
    }

    private fun isEnable() {
        if (Utils.isEmailValid(binding.etEmail.text.toString())) binding.model =
            EmailValidationModel(enable = true, email = binding.etEmail.text.toString(), "")
        else binding.model =
            EmailValidationModel(enable = false, email = binding.etEmail.text.toString(), "")
    }

    override fun onRetryClick() {
        if (isCodeCheckApi) {
            count++
            sendEmailVerificationRequest()

        } else {
            count++
            loader?.show(requireActivity().supportFragmentManager, "")

            val request =
                EmailVerificationRequest(Constants.EMAIL_SELECTION_TYPE, binding.model?.email ?: "")

            createAccountViewModel.emailVerificationApi(request)

        }

    }
}