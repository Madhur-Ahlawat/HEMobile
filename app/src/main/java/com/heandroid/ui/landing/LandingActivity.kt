package com.heandroid.ui.landing

import android.content.Intent
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.heandroid.R
import com.heandroid.databinding.ActivityLandingBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : BaseActivity<Any?>() {

    private lateinit var navController: NavController
    private var screenType: String = ""
    private lateinit var binding: ActivityLandingBinding


    override fun initViewBinding() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(this, R.id.nav_host_fragment_container)
        binding.idToolBarLyt.btnLogin.setOnClickListener {
            startLoginActivity()
        }

    }


    fun openLandingFragment() {
        navController.navigate(R.id.action_landing_screen)
    }

    override fun observeViewModel() {


    }

    private fun startLoginActivity() {
        Intent(this, AuthActivity::class.java).run {
            putExtra(Constants.SHOW_SCREEN, screenType)
            startActivity(this)
        }

    }
}