package com.conduent.nationalhighways.ui.bottomnav.account.close_account

import android.os.Bundle
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCloseAccountSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
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
    private var binding: ActivityCloseAccountSuccessBinding? = null

    @Inject
    lateinit var sessionManager: SessionManager
    override fun observeViewModel() {
        TODO("Not yet implemented")
    }

    override fun initViewBinding() {
        binding = ActivityCloseAccountSuccessBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setView()
    }

    private fun setView() {
        if (HomeActivityMain.accountDetailsData?.accountInformation?.accSubType.equals(Constants.PAYG)) {
            binding?.titleNext?.visible()
            binding?.whatHappensNext?.visible()
        } else {
            binding?.titleNext?.gone()
            binding?.whatHappensNext?.gone()
        }
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