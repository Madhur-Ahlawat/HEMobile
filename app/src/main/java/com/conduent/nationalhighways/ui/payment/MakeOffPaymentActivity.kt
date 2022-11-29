package com.conduent.nationalhighways.ui.payment

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityMakeOffPaymentBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MakeOffPaymentActivity : BaseActivity<Any>() {
    private lateinit var binding : ActivityMakeOffPaymentBinding

    override fun initViewBinding() {
        binding=ActivityMakeOffPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.titleTxt.text = applicationContext.getString(R.string.str_make_one_of_payment)
        binding.include.backButton.setOnClickListener { onBackPressed() }

        AdobeAnalytics.setScreenTrack(
            "one of  payment",
            "one of payment",
            "english",
            "one of payment",
            "home",
            "one of payment",
            false
        )

    }

    override fun observeViewModel() { }

}