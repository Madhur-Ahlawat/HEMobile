package com.heandroid.ui.startNow.guidancedocuments

import com.heandroid.databinding.ActivityGuidanceAndDocumentsBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuidanceAndDocumentsActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityGuidanceAndDocumentsBinding

    override fun initViewBinding() {
        binding = ActivityGuidanceAndDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun observeViewModel() {

    }

    override fun onStart() {
        super.onStart()
    }


}