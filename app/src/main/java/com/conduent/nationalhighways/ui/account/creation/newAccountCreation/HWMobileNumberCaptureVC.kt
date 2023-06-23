package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.databinding.FragmentMobileNumberCaptureVcBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HWMobileNumberCaptureVC : BaseFragment<FragmentMobileNumberCaptureVcBinding>(),
    View.OnClickListener, OnRetryClickListener {

    private var requiredCountryCode = false
    private var requiredMobileNumber = false
    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesCodeList: MutableList<String> = ArrayList()
    private var isViewCreated: Boolean = false
    private lateinit var navFlow: String
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMobileNumberCaptureVcBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()
        binding.inputCountry.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.inputMobileNumber.editText.inputType = InputType.TYPE_CLASS_NUMBER

        if (!NewCreateAccountRequestModel.communicationTextMessage && !NewCreateAccountRequestModel.twoStepVerification) {
            binding.txtTitleTop.text = getString(R.string.str_what_is_your_number)
            binding.inputMobileNumber.setLabel(getString(R.string.str_phone_number))
            binding.txtBottom.visibility = View.GONE
            requiredMobileNumber=true
        } else {
            binding.inputMobileNumber.setLabel(getString(R.string.str_mobile_number))
            binding.txtTitleTop.text = getString(R.string.str_what_mobile_number)
            binding.txtBottom.visibility = View.VISIBLE


        }

        binding.btnNext.setOnClickListener(this)
        if(NewCreateAccountRequestModel.isEditCall) {
            navFlow = Constants.ACCOUNT_CREATION_MOBILE_FLOW
            NewCreateAccountRequestModel.mobileNumber?.let { binding.inputMobileNumber.setText(it) }
            NewCreateAccountRequestModel.countryCode?.let { binding.inputCountry.setSelectedValue(it) }
        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
        if (!isViewCreated) {

            viewModel.getCountryCodesList()

            observe(viewModel.countriesCodeList, ::getCountryCodesList)
            observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
        }
        isViewCreated = true

    }

    private fun getCountryCodesList(response: Resource<List<CountryCodes?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesCodeList.clear()
                response.data?.forEach {
                    it?.value?.let { it1 -> countriesCodeList.add(it1) }
                }
                countriesCodeList.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })


                if (countriesCodeList.contains(Constants.UK_CODE)) {
                    countriesCodeList.remove(Constants.UK_CODE)
                    countriesCodeList.add(0, Constants.UK_CODE)
                }

                binding.apply {
                    inputCountry.dataSet.addAll(countriesCodeList)
                    inputCountry.setSelectedValue(Constants.UK_CODE)
                }

            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {
            }

        }
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {
                val mobileNumber = binding.inputMobileNumber.getText().toString().trim()
                val countryCode = binding.inputCountry.selectedItemDescription.toString()

                if(NewCreateAccountRequestModel.isEditCall && countryCode == NewCreateAccountRequestModel.countryCode  && mobileNumber == NewCreateAccountRequestModel.mobileNumber) {
                    if(NewCreateAccountRequestModel.isAccountTypeEditCall){
                        findNavController().navigate(R.id.action_HWMobileNumberCaptureVC_to_vehicleListFragment)
                    }else {
                        findNavController().navigate(R.id.action_HWMobileNumberCaptureVC_to_accountSummaryFragment)
                    }
                }else {
                    NewCreateAccountRequestModel.mobileNumber = mobileNumber
                    NewCreateAccountRequestModel.countryCode = countryCode
                    if (!NewCreateAccountRequestModel.communicationTextMessage && !NewCreateAccountRequestModel.twoStepVerification) {
                        if(NewCreateAccountRequestModel.isEditCall){
                            findNavController().navigate(R.id.action_HWMobileNumberCaptureVC_to_accountSummaryFragment)
                        }else{
                            findNavController().navigate(R.id.action_HWMobileNumberCaptureVC_to_createVehicleFragment)
                        }

                    } else {
                        hitApi()

                    }

                }


            }
        }
    }

    override fun onRetryClick() {

    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?, start: Int, count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?, start: Int, before: Int, count: Int
        ) {

            requiredCountryCode = binding.inputCountry.getText()?.isNotEmpty() == true


            if (index==1){
                requiredMobileNumber = if (binding.inputMobileNumber.getText().toString().isNotEmpty()){
                    if (Utils.phoneNumber.matches(
                            binding.inputMobileNumber.getText().toString().trim()
                        )
                    ) {
                        binding.inputMobileNumber.removeError()
                        true
                    } else {
                        binding.inputMobileNumber.setErrorText(getString(R.string.str_uk_phoneNumber_error_message))
                        false
                    }
                }else{
                    false
                }
            }

            checkButton()

        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {
        if (requiredCountryCode && requiredMobileNumber) {
            binding.btnNext.enable()
        } else {
            binding.btnNext.disable()
        }
    }

    private fun hitApi() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = EmailVerificationRequest(
            Constants.SMS, binding.inputMobileNumber.getText().toString().trim()
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
                bundle.putParcelable(
                    "data", RequestOTPModel(
                        Constants.SMS, binding.inputMobileNumber.getText().toString().trim()
                    )
                )

                bundle.putParcelable(
                    "response", SecurityCodeResponseModel(
                        resource.data?.emailStatusCode, 0L, resource.data?.referenceId, true
                    )
                )

                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                findNavController().navigate(
                    R.id.action_HWMobileNumberCaptureVC_to_forgotOtpFragment, bundle
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