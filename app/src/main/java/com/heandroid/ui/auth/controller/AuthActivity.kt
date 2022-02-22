package com.heandroid.ui.auth.controller

import com.heandroid.BuildConfig
import com.heandroid.databinding.ActivityAuthBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityAuthBinding

    override fun initViewBinding() {
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun observeViewModel() {}

}