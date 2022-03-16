package com.heandroid.ui.startNow.contactdartcharge

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityContactDartChargeBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDartChargeActivity : BaseActivity<ActivityContactDartChargeBinding>() {

    private lateinit var binding: ActivityContactDartChargeBinding

    override fun initViewBinding() {
        binding = ActivityContactDartChargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {}

}