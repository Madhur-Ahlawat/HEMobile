package com.conduent.nationalhighways.ui.bottomnav.dashboard.topup

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityManualTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManualTopUpActivity : BaseActivity<Any>(), LogoutListener {

    private lateinit var binding : ActivityManualTopUpBinding

    @Inject
    lateinit var api: ApiService
    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding=ActivityManualTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.toolBarLyt.tvHeader.text = getString(R.string.manual_top_up)
        binding.toolBarLyt.btnBack.setOnClickListener { onBackPressed() }
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
        LogoutUtil.stopLogoutTimer()
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager,api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

    override fun observeViewModel() {

    }

}