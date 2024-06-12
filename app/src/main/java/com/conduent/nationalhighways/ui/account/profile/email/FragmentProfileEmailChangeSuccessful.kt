package com.conduent.nationalhighways.ui.account.profile.email

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentProfileEmailChangeSuccessBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FragmentProfileEmailChangeSuccessful :
    BaseFragment<FragmentProfileEmailChangeSuccessBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileEmailChangeSuccessBinding.inflate(inflater, container, false)

    override fun init() {
        var email = HomeActivityMain.accountDetailsData?.personalInformation?.emailAddress
        try {
            binding.message.text =
                "We’ve sent a confirmation email to\n${email}".toLowerCase()
        } catch (e: Exception) {
            binding.message.text =
                "We’ve sent a confirmation email to\n${email}"
        }
        sessionManager.clearAll()
    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener {
            Intent(requireActivity(), LandingActivity::class.java).apply {
                putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
            requireActivity().finish()
        }
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                sessionManager.clearAll()
                requireActivity().finish()
                requireActivity().startNormalActivity(AuthActivity::class.java)
            }
        }
    }
}