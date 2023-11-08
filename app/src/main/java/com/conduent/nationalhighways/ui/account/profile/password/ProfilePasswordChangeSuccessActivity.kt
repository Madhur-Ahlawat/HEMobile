package com.conduent.nationalhighways.ui.account.profile.password

import android.content.Intent
import android.text.Html
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityChangePasswordSuccessProfileBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfilePasswordChangeSuccessActivity : BaseActivity<ActivityChangePasswordSuccessProfileBinding>(),LogoutListener {

    @Inject
    lateinit var api: ApiService
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
            binding.message.text = Html.fromHtml(
            getString(
                R.string.you_will_receive_a_confirmation_email,
                intent.getStringExtra(Constants.EMAIL)?.toLowerCase()
            ), Html.FROM_HTML_MODE_COMPACT)
        } catch (e: Exception) {
            binding.message.text = Html.fromHtml(
                getString(
                    R.string.you_will_receive_a_confirmation_email,
                    intent.getStringExtra(Constants.EMAIL)
                ), Html.FROM_HTML_MODE_COMPACT)
        }
        binding.btnSignin.setOnClickListener {
            sessionManager.clearAll()
            Intent(this@ProfilePasswordChangeSuccessActivity, LoginActivity::class.java).apply {
                putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        }
    }

    override fun observeViewModel() {}
    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        LogoutUtil.stopLogoutTimer()
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager,api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}