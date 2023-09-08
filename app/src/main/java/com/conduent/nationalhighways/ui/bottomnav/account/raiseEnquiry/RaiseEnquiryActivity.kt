package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityRaiseEnquiryBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RaiseEnquiryActivity : BaseActivity<ActivityRaiseEnquiryBinding>() {

    private lateinit var binding: ActivityRaiseEnquiryBinding
    lateinit var navController: NavController
    val viewModel: RaiseNewEnquiryViewModel by viewModels()

    lateinit var listener: NavController.OnDestinationChangedListener
    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = ActivityRaiseEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initCtrl()
    }

    private fun init() {

        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
        binding.toolBarLyt.backButton.setOnClickListener {
            Log.e("TAG", "init: backClick" )
            onBackPressedDispatcher.onBackPressed()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_raise_enquiry_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                // Handle navigation events here
                when (destination.id) {
                    R.id.guidanceDocumentsFragment -> {
                        binding.toolBarLyt.titleTxt.setText(resources.getString(R.string.str_guidance_and_documents))
                    }

                    R.id.contactDartChargeFragment -> {
                        binding.toolBarLyt.titleTxt.setText(resources.getString(R.string.str_contact_dart_charge))
                    }


                    R.id.enquiryStatusFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_enquiry_status)
                    }

                    R.id.casesEnquiryDetailsFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_enquiry_status)
                    }

                    else -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
                    }

                    // Add more destination cases as needed
                }

                when (destination.id) {
                    R.id.enquirySuccessFragment -> {
                        binding.toolBarLyt.backButton.gone()
                    }
                    else -> {
                        binding.toolBarLyt.backButton.visible()
                    }
                }
            }
        navController.addOnDestinationChangedListener(listener)

    }

    private fun initCtrl() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::navController.isInitialized) {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

}