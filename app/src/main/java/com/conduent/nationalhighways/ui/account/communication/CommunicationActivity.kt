package com.conduent.nationalhighways.ui.account.communication

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCommunicationBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.customToolbar
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommunicationActivity : BaseActivity<ActivityCommunicationBinding>(), LogoutListener {

    lateinit var binding: ActivityCommunicationBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityCommunicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customToolbar(getString(R.string.str_communications))
        initCtrl()
    }

    private fun initCtrl() {
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        loadSession()
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
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }
}