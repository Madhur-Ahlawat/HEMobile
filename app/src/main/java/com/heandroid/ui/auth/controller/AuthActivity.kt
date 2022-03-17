package com.heandroid.ui.auth.controller

import com.heandroid.databinding.ActivityAuthBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.logging.Logger


@AndroidEntryPoint
class AuthActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityAuthBinding

     var value = Constants.NORMAL_LOGIN_FLOW_CODE

    override fun initViewBinding() {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
         value = intent?.getIntExtra(Constants.FROM_DART_CHARGE_FLOW, Constants.NORMAL_LOGIN_FLOW_CODE)!!
        Logg.logging("AuthActivity", " value $value  ")
    }

    override fun observeViewModel() {}

}