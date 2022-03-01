package com.heandroid.ui.futureModule

import com.heandroid.databinding.ActivityInProgressBinding
import com.heandroid.databinding.ActivityLandingBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InProgressActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityLandingBinding

    override fun initViewBinding() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }
}