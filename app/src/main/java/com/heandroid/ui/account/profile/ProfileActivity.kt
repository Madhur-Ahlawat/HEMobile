package com.heandroid.ui.account.profile

import com.heandroid.R
import com.heandroid.databinding.ActivityProfileBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>() {

    private lateinit var binding: ActivityProfileBinding

    override fun initViewBinding() {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_account_management))
    }

    override fun observeViewModel() {
    }


}