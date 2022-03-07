package com.heandroid.ui.account.profile

import com.heandroid.R
import com.heandroid.databinding.ActivityProfileBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>(), LogoutListener {

    private lateinit var binding: ActivityProfileBinding

    @Inject
    lateinit var sessionManager : SessionManager


    override fun initViewBinding() {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_account_management))
    }

    override fun observeViewModel() {
    }

    override fun onStart() {
        super.onStart()
        loadsession()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadsession()
    }

    private fun loadsession(){
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }


}