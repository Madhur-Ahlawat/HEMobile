package com.heandroid.ui.startNow.guidancedocuments

import com.adobe.marketing.mobile.MobileCore
import com.heandroid.databinding.ActivityGuidanceAndDocumentsBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.AdobeAnalytics
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap

@AndroidEntryPoint
class GuidanceAndDocumentsActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityGuidanceAndDocumentsBinding

    override fun initViewBinding() {
        binding = ActivityGuidanceAndDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AdobeAnalytics.setScreenTrack("dart charge guidance and documents","dart charge guidance and documents","english","dart charge guidance and documents","landing","dart charge guidance and documents")

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