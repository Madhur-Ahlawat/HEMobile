package com.conduent.nationalhighways.ui.viewcharges

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.tollrates.TollRatesResp
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityViewChargesBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ViewChargesActivity : BaseActivity<ActivityViewChargesBinding>(), LogoutListener {

    private val viewModel: ViewChargeViewModel by viewModels()

    private lateinit var binding: ActivityViewChargesBinding

    @Inject
    lateinit var sessionManager: SessionManager


    @Inject
    lateinit var api: ApiService

    override fun initViewBinding() {
        showLoaderDialog()
        binding = ActivityViewChargesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_create_an_account)
        binding.toolBarLyt.backButton.setOnClickListener {
            finish()
        }

        AdobeAnalytics.setScreenTrack(
            "view charges",
            "view charges",
            "english",
            "dart charge",
            "home",
            "view charges",
            sessionManager.getLoggedInUser()
        )
        viewModel.tollRates()
    }


    override fun observeViewModel() {
        lifecycleScope.launch {
            observe(viewModel.tollRates, ::handleTollRateResponse)
        }
    }

    private fun handleTollRateResponse(status: Resource<List<TollRatesResp?>?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {
                binding.topTitle.visible()
                binding.titleCard.visible()
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@ViewChargesActivity)

                    val mTollRatesList = ArrayList<TollRatesResp>()
                    status.data?.forEach {
                        when (it?.vehicleType) {
                            "A" -> {
                                mTollRatesList.add(
                                    TollRatesResp(
                                        it.vehicleId,
                                        resources.getString(R.string.str_motor_cycles),
                                        it.videoRate,
                                        it.etcRate,
                                        resources.getString(R.string.str_motor_cycles_)
                                    )
                                )

                            }

                            "B" -> {
                                mTollRatesList.add(
                                    TollRatesResp(
                                        it.vehicleId,
                                        resources.getString(R.string.str_cars_motor_homes),
                                        it.videoRate,
                                        it.etcRate,
                                        resources.getString(R.string.str_cars_motor_homes_)
                                    )
                                )

                            }

                            "C" -> {
                                mTollRatesList.add(
                                    TollRatesResp(
                                        it.vehicleId,
                                        resources.getString(R.string.str_vehicles_with_axles),
                                        it.videoRate,
                                        it.etcRate,
                                        resources.getString(R.string.str_vehicles_with_axles_)
                                    )
                                )

                            }

                            "D" -> {
                                mTollRatesList.add(
                                    TollRatesResp(
                                        it.vehicleId,
                                        resources.getString(R.string.str_vehicle_with_more_axles),
                                        it.videoRate,
                                        it.etcRate,
                                        resources.getString(R.string.str_vehicle_with_more_axles_)
                                    )
                                )

                            }

                            else -> {

                            }
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

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        LogoutUtil.stopLogoutTimer()
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }


}