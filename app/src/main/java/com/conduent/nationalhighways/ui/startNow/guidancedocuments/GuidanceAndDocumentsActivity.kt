package com.conduent.nationalhighways.ui.startNow.guidancedocuments

import com.conduent.nationalhighways.databinding.ActivityGuidanceAndDocumentsBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuidanceAndDocumentsActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityGuidanceAndDocumentsBinding

    override fun initViewBinding() {
        binding = ActivityGuidanceAndDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        AdobeAnalytics.setScreenTrack("dart charge guidance and documents","dart charge guidance and documents","english","dart charge guidance and documents","landing","dart charge guidance and documents")

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