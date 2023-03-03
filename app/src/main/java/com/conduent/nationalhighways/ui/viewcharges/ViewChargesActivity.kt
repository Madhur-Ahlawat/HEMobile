package com.conduent.nationalhighways.ui.viewcharges

import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.tollrates.TollRatesResp
import com.conduent.nationalhighways.databinding.ActivityViewChargesBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.toolbar
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ViewChargesActivity : BaseActivity<ActivityViewChargesBinding>() {

    private val viewModel: ViewChargeViewModel by viewModels()

    private lateinit var binding: ActivityViewChargesBinding
    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding = ActivityViewChargesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_charges_6am_10pm))


        AdobeAnalytics.setScreenTrack(
            "view charges",
            "view charges",
            "english",
            "dart charge",
            "home",
            "view charges",
            sessionManager.getLoggedInUser()
        )

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