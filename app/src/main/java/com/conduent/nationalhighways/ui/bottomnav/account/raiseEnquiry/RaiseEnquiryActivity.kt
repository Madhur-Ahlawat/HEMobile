package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityRaiseEnquiryBinding
import com.conduent.nationalhighways.databinding.FragmentLoginChangesBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RaiseEnquiryActivity : BaseActivity<ActivityRaiseEnquiryBinding>() {

    private lateinit var binding: ActivityRaiseEnquiryBinding

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = ActivityRaiseEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initCtrl()
    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
    }

    private fun initCtrl() {

    }

}