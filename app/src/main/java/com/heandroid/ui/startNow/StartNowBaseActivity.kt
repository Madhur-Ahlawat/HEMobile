package com.heandroid.ui.startNow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityAuthBinding
import com.heandroid.databinding.ActivityStartNowBaseBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartNowBaseActivity : BaseActivity<Any?>(), View.OnClickListener {

    private lateinit var navController: NavController
    private var screenType: String = ""
    private lateinit var binding: ActivityStartNowBaseBinding

    override fun initViewBinding() {
        binding = ActivityStartNowBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.fragment_container)

        intent?.let {
            screenType = it.getStringExtra(Constants.SHOW_SCREEN).toString()
            setFragmentInView()
        }

        binding.apply {
            idToolBarLyt.btnBack.setOnClickListener {
                finish()
            }
        }

    }

    override fun observeViewModel() {
    }

    private fun setFragmentInView() {
        Logg.logging("Testing","StartNowBaseActivity,  calledd")
//        var oldGraph = navController.graph
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_start_now)

        when (screenType) {
            Constants.ABOUT_SERVICE -> {  oldGraph.setStartDestination(R.id.aboutService) }
            Constants.CROSSING_SERVICE_UPDATE -> {   oldGraph.setStartDestination(R.id.crossingUpdate) }
            Constants.CONTACT_DART_CHARGES -> {   oldGraph.setStartDestination(R.id.contactDartCharge) }

        }
        navController.graph = oldGraph
    }

    override fun onClick(v: View?) {

        v?.let {
            when (v.id) {


            }
        }
    }


}