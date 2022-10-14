package com.conduent.nationalhighways.ui.account.creation.controller

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
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