package com.conduent.nationalhighways.ui.futureModule

import com.conduent.nationalhighways.databinding.ActivityInProgressBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
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