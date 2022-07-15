package com.heandroid.ui.account.communication

import com.heandroid.R
import com.heandroid.databinding.ActivityCommunicationBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.customToolbar
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
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
        customToolbar(getString(R.string.str_communication_preferences))
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