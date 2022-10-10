package com.heandroid.ui.viewcharges

import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.tollrates.TollRatesResp
import com.heandroid.databinding.ActivityViewChargesBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewChargesActivity : BaseActivity<ActivityViewChargesBinding>() {

    private val viewModel: ViewChargeViewModel by viewModels()

    private lateinit var binding: ActivityViewChargesBinding
    private var loader: LoaderDialog? = null


    override fun initViewBinding() {
        binding = ActivityViewChargesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_charges_6am_10pm))

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.tollRates()
    }


    override fun observeViewModel() {
        lifecycleScope.launch {
            observe(viewModel.tollRates, ::handleTollRateResponse)
        }
    }

    private fun handleTollRateResponse(status: Resource<List<TollRatesResp?>?>?) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                binding.topTitle.visible()
                binding.titleCard.visible()
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@ViewChargesActivity)

                    val mTollRatesList = ArrayList<TollRatesResp>()
                    status.data?.forEach {
                        if (it?.vehicleType == "A") {
                            mTollRatesList.add(
                                TollRatesResp(
                                    it?.vehicleId,
                                    "Motorcycle, \nmopeds,\nquad bikes",
                                    it?.videoRate,
                                    it?.etcRate
                                )
                            )

                        } else if (it?.vehicleType == "B") {
                            mTollRatesList.add(
                                TollRatesResp(
                                    it?.vehicleId,
                                    "Cars, \nmotorhomes,\nminibuses",
                                    it?.videoRate,
                                    it?.etcRate
                                )
                            )

                        } else if (it?.vehicleType == "C") {
                            mTollRatesList.add(
                                TollRatesResp(
                                    it?.vehicleId,
                                    "Vehicles with \n2 axles",
                                    it?.videoRate,
                                    it?.etcRate
                                )
                            )

                        } else if (it?.vehicleType == "D") {
                            mTollRatesList.add(
                                TollRatesResp(
                                    it?.vehicleId,
                                    "Vehicles with\n more than 2\n axles",
                                    it?.videoRate,
                                    it?.etcRate
                                )
                            )

                        } else {

                        }

                    }

                    adapter = TollRateAdapter(this@ViewChargesActivity, mTollRatesList)
                }
            }
            is Resource.DataError -> {

                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }
    }


}