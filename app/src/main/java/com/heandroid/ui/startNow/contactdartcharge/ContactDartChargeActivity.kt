package com.heandroid.ui.startNow.contactdartcharge

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityContactDartChargeBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDartChargeActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityContactDartChargeBinding
    var mValue = Constants.FROM_CASES_TO_CASES_VALUE
    private lateinit var navController: NavController

    override fun initViewBinding() {
        binding = ActivityContactDartChargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        navController = navHostFragment.navController

        intent?.apply {
            mValue = getIntExtra(Constants.FROM_LOGIN_TO_CASES, Constants.FROM_CASES_TO_CASES_VALUE)
        }
        var navGraph = navController.graph

        if (mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {

            navGraph.startDestination = R.id.caseHistoryDartChargeFragment

        } else {
            navGraph.startDestination = R.id.contactDartCharge

        }

        navController.graph = navGraph

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            finish()
            startNormalActivity(HomeActivityMain::class.java)
        }
    }

    override fun observeViewModel() {}

}