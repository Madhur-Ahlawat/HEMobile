package com.conduent.nationalhighways.ui.auth.controller

import com.conduent.nationalhighways.databinding.ActivityAuthBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityAuthBinding

    override fun initViewBinding() {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {}

}