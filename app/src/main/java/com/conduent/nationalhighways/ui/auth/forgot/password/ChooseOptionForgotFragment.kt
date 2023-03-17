package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.databinding.FragmentForgotChooseOptionBinding
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

        binding.model = arguments?.getParcelable(Constants.OPTIONS)

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
       // binding.continueBtn.setOnClickListener(this)
        binding.relativeLayout1.setOnClickListener(this)
        binding.relativeLayout2.setOnClickListener(this)
    }

    override fun observer() {
        if (!isViewCreated){
            observe(viewModel.otp, ::handleOTPResponse)

        }

        isViewCreated=true
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {


            R.id.post_mail_radio_btn -> {
                model?.optionType = Constants.POST_MAIL
                model?.optionValue = binding.model?.address
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.continue_btn -> {
                if (!TextUtils.isEmpty(model?.optionType ?: "")) {
                    loader = LoaderDialog()
                    loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    viewModel.requestOTP(model)


                } else {
                    showError(binding.root, getString(R.string.please_select_one_mode_for_password))
                }
            }
            R.id.relativeLayout1 -> {
                model?.optionType = Constants.EMAIL
                model?.optionValue = binding.model?.email
                hitApi()
            }
            R.id.relativeLayout2 -> {
                model?.optionType = Constants.SMS
                model?.optionValue = binding.model?.phone
                hitApi()
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