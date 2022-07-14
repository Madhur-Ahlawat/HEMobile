package com.heandroid.ui.bottomnav.dashboard.topup

import com.heandroid.R
import com.heandroid.databinding.ActivityManualTopUpBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ManualTopUpActivity : BaseActivity<Any>(), LogoutListener {

    private lateinit var binding : ActivityManualTopUpBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding=ActivityManualTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar("Manual top-up")

    }

    override fun onStart() {
        super.onStart()
        loadSession()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession(){
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }

    override fun observeViewModel() {

    }

}