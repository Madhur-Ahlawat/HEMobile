package com.conduent.nationalhighways.ui.account.profile.password

import android.content.Intent
import com.conduent.nationalhighways.databinding.ActivityChangePasswordSuccessProfileBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfilePasswordChangeSuccessActivity : BaseActivity<ActivityChangePasswordSuccessProfileBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityChangePasswordSuccessProfileBinding

    override fun initViewBinding() {
        binding = ActivityChangePasswordSuccessProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCtrl()
    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    private fun initCtrl() {
        try {
            binding.message.text =
                "We’ve sent a confirmation email to\n${HomeActivityMain.accountDetailsData?.personalInformation?.emailAddress}".toLowerCase()
        } catch (e: Exception) {
            binding.message.text =
                "We’ve sent a confirmation email to\n${HomeActivityMain.accountDetailsData?.personalInformation?.emailAddress}"
        }
        binding.btnContinue.setOnClickListener {
            Intent(this@ProfilePasswordChangeSuccessActivity, LoginActivity::class.java).apply {
                putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        }
    }

    override fun observeViewModel() {}
}