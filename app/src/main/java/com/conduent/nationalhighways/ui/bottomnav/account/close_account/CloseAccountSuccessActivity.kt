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
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CloseAccountSuccessActivity : BaseActivity<ActivityCloseAccountSuccessBinding>(),LogoutListener {
    lateinit var binding: ActivityCloseAccountSuccessBinding

    @Inject
    lateinit var sessionManager: SessionManager
    var email = ""
    var accountSubType: String = ""
    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        binding = ActivityCloseAccountSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        if (intent.hasExtra(Constants.EMAIL)) {
            email = intent.getStringExtra(Constants.EMAIL).toString()
        }
        if (intent.hasExtra(Constants.ACCOUNT_SUBTYPE)) {
            accountSubType = intent.getStringExtra(Constants.ACCOUNT_SUBTYPE).toString()
        }
        Log.e("TAG", "setView: email "+email )
        Log.e("TAG", "setView: accountSubType "+accountSubType )
        if (accountSubType.equals(Constants.PAYG)) {
            Log.e("TAG", "setView: 11 " )
            binding.titleNext.invisible()
            binding.whatHappensNext.invisible()
        } else {
            Log.e("TAG", "setView: 1122 " )
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_close_account_success)
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