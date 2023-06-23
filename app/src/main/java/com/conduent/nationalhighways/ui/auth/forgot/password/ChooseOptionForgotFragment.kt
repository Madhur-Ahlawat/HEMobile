package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.databinding.FragmentForgotChooseOptionchangesBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseOptionForgotFragment : BaseFragment<FragmentForgotChooseOptionchangesBinding>(),
    RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private var model: RequestOTPModel? = null
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var responseModel: ConfirmOptionResponseModel?=null
    private var loader: LoaderDialog? = null
    private var response: SecurityCodeResponseModel? = null
    private var isViewCreated:Boolean=false
    private lateinit var  navFlow:String// create account , forgot password



    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotChooseOptionchangesBinding =
        FragmentForgotChooseOptionchangesBinding.inflate(inflater, container, false)

    override fun init() {
        model = RequestOTPModel(optionType = "", optionValue = "")
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()


        responseModel = arguments?.getParcelable(Constants.OPTIONS)

        binding.radioSms.text=getString(R.string.str_radio_sms,responseModel?.phone)
        binding.radioEmail.text=getString(R.string.str_radio_email,responseModel?.email)

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

    override fun initCtrl() {

    }

    override fun observer() {
        if (!isViewCreated){
            observe(viewModel.otp, ::handleOTPResponse)

        }

        isViewCreated=true
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {


            R.id.radio_sms-> {
                binding.btn.isEnabled=true
                model?.optionType = Constants.SMS
                model?.optionValue = responseModel?.phone
            }
            R.id.radio_email-> {
                model?.optionType = Constants.EMAIL
                model?.optionValue = responseModel?.email

                binding.btn.isEnabled=true
            }



            }

        }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn -> {
                if (!TextUtils.isEmpty(model?.optionType ?: "")) {
                    loader = LoaderDialog()
                    loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    viewModel.requestOTP(model)


                }
            }

        }
    }

    private fun hitApi(){
        if (!TextUtils.isEmpty(model?.optionType ?: "")) {
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.requestOTP(model)


        } else {
            showError(binding.root, getString(R.string.please_select_one_mode_for_password))
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
                    bundle.putString(Constants.NAV_FLOW_KEY,navFlow)

                    AdobeAnalytics.setActionTrack2(
                        "continue",
                        "login:forgot password:choose options",
                        "forgot password",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen, model?.optionType!!,
                        sessionManager.getLoggedInUser()
                    )

                    when (model?.optionType) {
                        Constants.POST_MAIL -> {
                            findNavController().navigate(
                                R.id.action_chooseOptionFragment_to_postalEmailFragment,
                                bundle
                            )
                        }
                        else -> {
                            findNavController().navigate(
                                R.id.action_chooseOptionFragment_to_otpFragment,
                                bundle
                            )
                        }
                    }

                }
                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)
                }
                else -> {
                }

        }
    }

}