package com.conduent.nationalhighways.ui.auth.controller

import com.conduent.nationalhighways.databinding.ActivityAuthBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityAuthBinding
    public var previousScreen = "home"

    @Inject
     lateinit var sessionManager: SessionManager
    override fun initViewBinding() {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previousScreen = if (intent.getIntExtra(
                Constants.FROM_DART_CHARGE_FLOW,
                0
            ) == Constants.DART_CHARGE_FLOW_CODE
        ) {
            "contact dart charge"
        } else {
            "home"

        }


        AdobeAnalytics.setScreenTrack(
            "login",
            "login",
            "english",
            "login",
            previousScreen,
            "login",
            sessionManager.getLoggedInUser()
        )


    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)

    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    override fun observeViewModel() {}

}