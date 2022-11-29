package com.conduent.nationalhighways.ui.startNow

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityStartNowBaseBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartNowBaseActivity : BaseActivity<ActivityStartNowBaseBinding>() {

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

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
    }

    override fun observeViewModel() {}

    private fun setFragmentInView() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_start_now)

        when (screenType) {
            Constants.ABOUT_SERVICE -> {
                oldGraph.setStartDestination(R.id.aboutService)
            }
            Constants.CROSSING_SERVICE_UPDATE -> {
                oldGraph.setStartDestination(R.id.crossingUpdate)
            }
            Constants.CONTACT_DART_CHARGES -> {
                oldGraph.setStartDestination(R.id.contactDartCharge)
            }

        }
        navController.graph = oldGraph
    }

}