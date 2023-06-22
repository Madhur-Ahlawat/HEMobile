package com.conduent.nationalhighways.ui.account.creation.controller

import android.view.View
import android.widget.Toast
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountActivity : BaseActivity<Any>() {
    lateinit var binding: ActivityCreateAccountBinding
    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_create_an_account)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }


        AdobeAnalytics.setScreenTrack(
            "create account",
            "create account",
            "english",
            "create account",
            "home",
            "create account",
            sessionManager.getLoggedInUser()
        )


    }

    override fun observeViewModel() {}



    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                if (fragment is AccountSuccessfullyCreationFragment || NewCreateAccountRequestModel.isAccountTypeEditCall){

                }else{
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }
    }

