package com.conduent.nationalhighways.ui.startNow.guidancedocuments

import com.conduent.nationalhighways.databinding.ActivityGuidanceAndDocumentsBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GuidanceAndDocumentsActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityGuidanceAndDocumentsBinding

    @Inject
    lateinit var sessionManager: SessionManager
    override fun initViewBinding() {
        binding = ActivityGuidanceAndDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        AdobeAnalytics.setScreenTrack("dart charge guidance and documents","dart charge guidance and documents","english","dart charge guidance and documents","landing","dart charge guidance and documents")

        AdobeAnalytics.setScreenTrack(
            "dart charges guidance and documents",
            "dart charges guidance and documents",
            "english",
            "dart charges guidance and documents",
            "home",
            "dart charges guidance and documents",
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

    override fun observeViewModel() {

    }

    override fun onStart() {
        super.onStart()
    }


}