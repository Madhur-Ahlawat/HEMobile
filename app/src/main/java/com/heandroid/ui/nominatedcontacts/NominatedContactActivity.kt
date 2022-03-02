package com.heandroid.ui.nominatedcontacts

import com.heandroid.R
import com.heandroid.databinding.ActivityNominatedContactsBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NominatedContactActivity : BaseActivity<ActivityNominatedContactsBinding>() {

    private lateinit var binding: ActivityNominatedContactsBinding

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        binding = ActivityNominatedContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_nominated_contacts))
    }
}