package com.heandroid.ui.account.communication

import com.heandroid.databinding.ActivityCommunicationBinding
import com.heandroid.databinding.ActivityInProgressBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CommunicationActivity : BaseActivity<ActivityCommunicationBinding>() {

    lateinit var binding: ActivityCommunicationBinding
    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = ActivityCommunicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}