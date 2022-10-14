package com.conduent.nationalhighways.ui.checkpaidcrossings

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCheckPaidCrossingsBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.customToolbar
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