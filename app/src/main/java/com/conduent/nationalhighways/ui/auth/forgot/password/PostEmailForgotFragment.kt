package com.conduent.nationalhighways.ui.auth.forgot.password

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentForgotPasswordPostalEmailBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PostEmailForgotFragment : BaseFragment<FragmentForgotPasswordPostalEmailBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotPasswordPostalEmailBinding =
        FragmentForgotPasswordPostalEmailBinding.inflate(inflater, container, false)

    @Inject
    lateinit var sessionManager: SessionManager
    override fun init() {

        AdobeAnalytics.setScreenTrack(
            "login:forgot password:choose options:post email",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password:choose options:post email",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}