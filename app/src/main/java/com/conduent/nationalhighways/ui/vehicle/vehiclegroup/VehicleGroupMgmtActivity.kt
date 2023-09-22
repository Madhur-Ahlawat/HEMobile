package com.conduent.nationalhighways.ui.vehicle.vehiclegroup

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityVehicleGroupMgmtBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.customToolbar
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VehicleGroupMgmtActivity : BaseActivity<ActivityVehicleGroupMgmtBinding>(), LogoutListener {

    private lateinit var binding: ActivityVehicleGroupMgmtBinding

    @Inject
    lateinit var sessionManager : SessionManager

    override fun initViewBinding() {
        binding = ActivityVehicleGroupMgmtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCtrl()
    }

    override fun onStart() {
        super.onStart()
    }


    override fun observeViewModel() {}

    private fun initCtrl() {
        binding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
        customToolbar(getString(R.string.group_mngmt))
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