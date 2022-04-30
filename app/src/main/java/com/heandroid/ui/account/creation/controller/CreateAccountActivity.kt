package com.heandroid.ui.account.creation.controller

import com.heandroid.R
import com.heandroid.databinding.ActivityCreateAccountBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountActivity : BaseActivity<Any>() {
    lateinit var binding: ActivityCreateAccountBinding

    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_create_account)
        binding.toolBarLyt.backButton.setOnClickListener { onBackPressed() }
    }

    override fun observeViewModel() {}

}