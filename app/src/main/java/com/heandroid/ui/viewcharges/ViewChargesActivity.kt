package com.heandroid.ui.viewcharges

import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.model.tollrates.TollRatesResp
import com.heandroid.data.model.tollrates.ViewChargesResponse
import com.heandroid.databinding.ActivityViewChargesBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewChargesActivity : BaseActivity<ActivityViewChargesBinding>() {

    private val viewModel : ViewChargeViewModel by viewModels()

    private lateinit var binding: ActivityViewChargesBinding
    private var loader: LoaderDialog?=null


    override fun initViewBinding() {
        binding = ActivityViewChargesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_charges_6am_10pm))

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(supportFragmentManager,"")
        viewModel.tollRates()
    }


    override fun observeViewModel() {
        lifecycleScope.launch {
            observe(viewModel.tollRates,::handleTollRateResponse)
        }
    }

    private fun handleTollRateResponse(status: Resource<List<TollRatesResp>?>?) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                binding.topTitle.visible()
                binding.titleCard.visible()
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@ViewChargesActivity)
                    adapter = TollRateAdapter(this@ViewChargesActivity,status.data)
                }
            }
            is Resource.DataError -> { ErrorUtil.showError(binding.root, status.errorMsg) }
        }
    }


}