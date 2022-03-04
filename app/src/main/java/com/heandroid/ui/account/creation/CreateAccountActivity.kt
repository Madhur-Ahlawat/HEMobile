package com.heandroid.ui.account.creation

import androidx.navigation.NavController
import com.heandroid.databinding.ActivityCreateAccountBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountActivity : BaseActivity<Any>() {
    lateinit var binding: ActivityCreateAccountBinding

    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun observeViewModel() {
    }

}