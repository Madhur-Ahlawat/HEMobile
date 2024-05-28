package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentForgotChooseOptionchangesBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChooseOptionForgotFragment : BaseFragment<FragmentForgotChooseOptionchangesBinding>(),
    RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private var model: RequestOTPModel? = null
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var responseModel: ConfirmOptionResponseModel? = null
    private var loader: LoaderDialog? = null
    private var response: SecurityCodeResponseModel? = null
    private var isViewCreated: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private lateinit var navFlow: String// create account , forgot password
    private var lrdsAccount: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotChooseOptionchangesBinding =
        FragmentForgotChooseOptionchangesBinding.inflate(inflater, container, false)


    override fun initCtrl() {

        if (arguments?.containsKey(Constants.LRDS_ACCOUNT) == true) {
            lrdsAccount = arguments?.getBoolean(Constants.LRDS_ACCOUNT, false) ?: false
        }

        model = RequestOTPModel(optionType = "", optionValue = "")
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }


        responseModel = arguments?.getParcelable(Constants.OPTIONS)

        if (navFlow == Constants.TWOFA) {

            viewModel.twoFAConfirmOption()
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

        } else {
            if (!responseModel?.phone.isNullOrEmpty() && !responseModel?.phone.equals(
                    "null",
                    true
                )
            ) {
                val htmlText =
                    Html.fromHtml(
                        getString(R.string.str_radio_sms, responseModel?.phone)
                    )
                binding.radioSms.text = htmlText
                binding.radioSms.visibility = View.VISIBLE
                binding.enterDetailsTxt.text = getString(R.string.str_choose_method_to_get_ur_link)

            } else {
                binding.radioSms.visibility = View.GONE
                binding.enterDetailsTxt.text =
                    getString(R.string.str_choose_method_to_get_email_code)
            }

            val htmlText =
                Html.fromHtml(getString(R.string.str_radio_email, responseModel?.email))
            binding.radioEmail.text = htmlText
        }


    }

    override fun init() {
        if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
        Utils.setupAccessibilityDelegatesForRadioButtons(binding.radioGroup)
        binding.radioGroup.setOnCheckedChangeListener(this)

        binding.btn.setOnClickListener(this)



        AdobeAnalytics.setScreenTrack(
            "login:forgot password:choose options",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password:choose options",
            sessionManager.getLoggedInUser()
        )

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.confirmOption, ::handleConfirmOptionResponse)
        }
        if (!isViewCreated) {
            observe(viewModel.otp, ::handleOTPResponse)


        }

        isViewCreated = true
    }


    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

        when (group?.checkedRadioButtonId) {


            R.id.radio_sms -> {
                model?.optionType = Constants.SMS
                model?.optionValue = responseModel?.phone
                binding.btn.isEnabled = true

            }

            R.id.radio_email -> {

                model?.optionType = Constants.EMAIL
                model?.optionValue = responseModel?.email


                binding.btn.isEnabled = true
            }


        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn -> {

                if (!TextUtils.isEmpty(model?.optionType ?: "")) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    if (navFlow == Constants.TWOFA) {
                        viewModel.twoFARequestOTP(model)

                    } else {
                        viewModel.requestOTP(model)

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
                val bundle = Bundle()
                bundle.putParcelable("data", model)

                response = status.data
                bundle.putParcelable("response", response)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                bundle.putBoolean(Constants.LRDS_ACCOUNT, lrdsAccount)

                AdobeAnalytics.setActionTrack2(
                    "continue",
                    "login:forgot password:choose options",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    model?.optionType?:"",
                    sessionManager.getLoggedInUser()
                )


                findNavController().navigate(
                    R.id.action_chooseOptionFragment_to_otpFragment,
                    bundle
                )


            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    showError(binding.root, status.errorMsg)
                }
            }

            else -> {
            }

        }
    }

    private fun handleConfirmOptionResponse(status: Resource<ConfirmOptionResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("1054") == true) {
                } else {
                    responseModel = status.data
                    binding.root.post {
                        if (!status.data?.phone.isNullOrEmpty() && !status.data?.phone.equals(
                                "null",
                                true
                            )
                        ) {
                            val htmlText =
                                Html.fromHtml(
                                    getString(R.string.str_radio_sms, status.data?.phone)
                                )
                            binding.radioSms.text = htmlText
                            binding.radioSms.visibility = View.VISIBLE
                        } else {
                            binding.radioSms.visibility = View.GONE
                            binding.enterDetailsTxt.text =
                                getString(R.string.str_choose_method_to_get_email_code)

                        }

                        val htmlText =
                            Html.fromHtml(getString(R.string.str_radio_email, status.data?.email))
                        binding.radioEmail.text = htmlText

                    }
                }
                AdobeAnalytics.setActionTrackError(
                    "next",
                    "login:forgot password",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen, "success",
                    sessionManager.getLoggedInUser()
                )

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    AdobeAnalytics.setActionTrackError(
                        "next",
                        "login:forgot password",
                        "forgot password",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen, status.errorMsg,
                        sessionManager.getLoggedInUser()
                    )
                }
            }

            else -> {
            }
        }


    }

}