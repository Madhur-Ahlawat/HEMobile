package com.conduent.nationalhighways.ui.auth.forgot.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.ui.base.BaseFragment

import com.conduent.nationalhighways.databinding.FragmentForgotResetBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ResetForgotPassword : BaseFragment<FragmentForgotResetBinding>(), View.OnClickListener {
    private lateinit var  navFlow:String

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotResetBinding = FragmentForgotResetBinding.inflate(inflater, container, false)

    @Inject
    lateinit var sessionManager: SessionManager
    override fun init() {
        binding.btnSubmit.setOnClickListener(this)
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()


        AdobeAnalytics.setScreenTrack(
            "login:forgot password:choose options:otp:new password set:password reset success",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password:choose options:otp:new password set:password reset success",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {
                AdobeAnalytics.setActionTrack(
                    "submit",
                    "login:forgot password:choose options:otp:new password set:password reset success",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    sessionManager.getLoggedInUser()
                )

                requireActivity().startNormalActivity(AuthActivity::class.java)
                requireActivity().finish()
            }
        }
    }
}