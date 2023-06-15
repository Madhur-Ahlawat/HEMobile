package com.conduent.nationalhighways.ui.account.creation.controller

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
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
        binding.toolBarLyt.backButton.setOnClickListener { onBackPressed() }





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

}