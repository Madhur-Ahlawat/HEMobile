package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.databinding.FragmentForgotChooseOptionBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.SessionManager
import javax.inject.Inject

class ChooseOptionForgotFragment : BaseFragment<FragmentForgotChooseOptionBinding>(),
    RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private var model: RequestOTPModel? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotChooseOptionBinding =
        FragmentForgotChooseOptionBinding.inflate(inflater, container, false)

    override fun init() {
        model = RequestOTPModel(optionType = "", optionValue = "")
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
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.continueBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {

            R.id.email_radio_btn -> {
                model?.optionType = Constants.EMAIL
                model?.optionValue = binding.model?.email
            }
            R.id.text_message_radio_btn -> {
                model?.optionType = Constants.SMS
                model?.optionValue = binding.model?.email
            }
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
                    val bundle = Bundle()
                    bundle.putParcelable("data", model)

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
                } else {
                    showError(binding.root, getString(R.string.please_select_one_mode_for_password))
                }
            }
        }
    }
}