package com.heandroid.ui.startNow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.heandroid.R
import com.heandroid.databinding.ActivityAuthBinding
import com.heandroid.databinding.ActivityStartNowBaseBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartNowBaseActivity : BaseActivity<Any?>(), View.OnClickListener{

    private lateinit var navController: NavController
    private var screenType: String = ""
    private lateinit var binding: ActivityStartNowBaseBinding

    override fun initViewBinding() {
        binding = ActivityStartNowBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.fragment_container)
        binding.apply {
            idToolBarLyt.btnBack.setOnClickListener{
                finish()
            }
        }

    }

    override fun observeViewModel() {
        intent?.let {
            screenType = it.getStringExtra(Constants.SHOW_SCREEN).toString()
            setFragmentInView()
        }
    }

    private fun setFragmentInView() {
        when (screenType) {
            Constants.ABOUT_SERVICE -> {
                navController.navigate(R.id.aboutService)
            }

            Constants.CROSSING_SERVICE_UPDATE -> {
                navController.navigate(R.id.crossingUpdate)
            }
            Constants.CONTACT_DART_CHARGES -> {
                navController.navigate(R.id.contactDartCharge)
            }
        }
    }

    override fun onClick(v: View?) {

        v?.let {
            when(v.id)
            {


            }
        }
    }


}