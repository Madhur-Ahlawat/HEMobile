package com.heandroid.ui.checkpaidcrossings

import com.heandroid.R
import com.heandroid.databinding.ActivityCheckPaidCrossingsBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.customToolbar
import com.heandroid.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossingActivity : BaseActivity<ActivityCheckPaidCrossingsBinding>() {

    private lateinit var binding: ActivityCheckPaidCrossingsBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityCheckPaidCrossingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customToolbar(getString(R.string.str_check_for_paid_crossing))
        initCtrl()
    }

    private fun initCtrl() {
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.clearAll()
    }

}