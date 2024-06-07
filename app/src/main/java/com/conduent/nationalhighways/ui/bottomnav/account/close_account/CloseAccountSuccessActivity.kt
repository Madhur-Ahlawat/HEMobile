package com.conduent.nationalhighways.ui.bottomnav.account.close_account

import android.text.Html
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityCloseAccountSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CloseAccountSuccessActivity : BaseActivity<ActivityCloseAccountSuccessBinding>(),
    LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    var email = ""
    var accountSubType: String = ""
    lateinit var binding: ActivityCloseAccountSuccessBinding

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        binding = ActivityCloseAccountSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        sessionManager.clearAll()
        binding.toolbar.backButton.gone()
        binding.toolbar.titleTxt.text = resources.getString(R.string.str_close_account)
        if (intent.hasExtra(Constants.EMAIL)) {
            email = intent.getStringExtra(Constants.EMAIL).toString()
        }
        if (intent.hasExtra(Constants.ACCOUNT_SUBTYPE)) {
            accountSubType = intent.getStringExtra(Constants.ACCOUNT_SUBTYPE).toString()
        }

        if (accountSubType.equals(Constants.PAYG) || accountSubType.equals(Constants.EXEMPT_PARTNER)) {
            binding.titleNext.gone()
            binding.whatHappensNext.gone()
        } else {
            binding.titleNext.visible()
            binding.whatHappensNext.visible()
        }

        binding.gotoStartMenuBt.setOnClickListener {
            startNormalActivityWithFinish(LandingActivity::class.java)
        }
        binding.message2.text = Html.fromHtml(
            resources.getString(
                R.string.we_will_send_an_email_to_when_the_account_has_been_closed,
                email
            ), Html.FROM_HTML_MODE_LEGACY
        )
        clearSession()
    }

    private fun clearSession() {
        sessionManager.clearAll()
    }

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
//        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}