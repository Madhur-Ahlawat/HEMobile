package com.heandroid.ui.futureModule

import com.heandroid.databinding.ActivityInProgressBinding
import com.heandroid.databinding.ActivityLandingBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InProgressActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityInProgressBinding

    override fun initViewBinding() {
        binding = ActivityInProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }
}