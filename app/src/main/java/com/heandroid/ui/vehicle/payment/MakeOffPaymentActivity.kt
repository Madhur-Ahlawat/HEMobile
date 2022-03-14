package com.heandroid.ui.vehicle.payment

import com.heandroid.R
import com.heandroid.databinding.ActivityMakeOffPaymentBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MakeOffPaymentActivity : BaseActivity<Any>() {
    private lateinit var binding : ActivityMakeOffPaymentBinding

    override fun initViewBinding() {
        binding=ActivityMakeOffPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.titleTxt.text = applicationContext.getString(R.string.str_make_one_of_payment)
        binding.include.backButton.setOnClickListener { onBackPressed() }
    }

    override fun observeViewModel() { }

}