package com.conduent.nationalhighways.ui.bottomnav.account.close_account

import android.os.Bundle
import android.text.Html
import android.util.Log
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCloseAccountSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CloseAccountSuccessActivity : BaseActivity<ActivityCloseAccountSuccessBinding>(),LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager
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
        binding.toolbar.backButton.gone()
        binding.toolbar.titleTxt.text = resources.getString(R.string.str_close_account)
        if (intent.hasExtra(Constants.EMAIL)) {
            email = intent.getStringExtra(Constants.EMAIL).toString()
        }
        if (intent.hasExtra(Constants.ACCOUNT_SUBTYPE)) {
            accountSubType = intent.getStringExtra(Constants.ACCOUNT_SUBTYPE).toString()
        }

        if (accountSubType.equals(Constants.PAYG)) {
            binding.titleNext.gone()
            binding.whatHappensNext.gone()
        } else {
            binding.titleNext.visible()
            binding.whatHappensNext.visible()
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
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}