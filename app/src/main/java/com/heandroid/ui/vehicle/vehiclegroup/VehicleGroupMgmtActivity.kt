package com.heandroid.ui.vehicle.vehiclegroup

import com.heandroid.R
import com.heandroid.databinding.ActivityVehicleGroupMgmtBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.customToolbar
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleGroupMgmtActivity : BaseActivity<ActivityVehicleGroupMgmtBinding>(), LogoutListener {

    private lateinit var binding: ActivityVehicleGroupMgmtBinding

    override fun initViewBinding() {
        binding = ActivityVehicleGroupMgmtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCtrl()
    }

    override fun onStart() {
        super.onStart()
        loadsession()

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadsession()

    }

    private fun loadsession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }


    override fun observeViewModel() {}

    private fun initCtrl() {
        binding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
        customToolbar(getString(R.string.group_mngmt))
    }

    override fun onLogout() {
        Utils.sessionExpired(this)
    }

}