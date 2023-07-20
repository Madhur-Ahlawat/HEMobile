package com.conduent.nationalhighways.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.FragmentTollDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.accountDetailsData
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TollDetailsFragment : BaseFragment<FragmentTollDetailsBinding>() {

    private var dateRangeModel: PaymentDateRangeModel? = null
    private var topup: String? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTollDetailsBinding.inflate(inflater, container, false)


    override fun init() {

    }

    override fun onResume() {
        super.onResume()
        binding.crossingAmount.text = crossing?.balance
        binding.tvAccountNumberValue.text = accountDetailsData?.accountInformation?.number
        binding.tvVehicleRegistrationValue.text = dateRangeModel?.vehicleNumber
        binding.tvTimeValue.text = crossing?.exitTime
        binding.tvLocationValue.text = crossing?.entryPlaza
        binding.tvStatusValue.text = crossing?.tranSettleStatus
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}