package com.heandroid.ui.vehicle.payment

import com.heandroid.databinding.ActivityMakeOffPaymentBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MakeOffPaymentActivity : BaseActivity<Any>() {
    private lateinit var binding : ActivityMakeOffPaymentBinding

    override fun initViewBinding() {
        binding=ActivityMakeOffPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun observeViewModel() {
    }

}