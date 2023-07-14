package com.conduent.nationalhighways.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.AllTransactionsBinding
import com.conduent.nationalhighways.databinding.FragmentCrossingDetailsBinding
import com.conduent.nationalhighways.databinding.FragmentDashboardBinding
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.databinding.ItemRecentTansactionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardFragmentNew
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.topup.ManualTopUpActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.GenericRecyclerViewAdapter
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionDetailsFragment : BaseFragment<FragmentCrossingDetailsBinding>() {

    private var dateRangeModel: PaymentDateRangeModel?=null
    private var topup: String?=null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCrossingDetailsBinding.inflate(inflater, container, false)


    override fun init() {

    }

    override fun onResume() {
        super.onResume()
        binding.crossingAmount.text=DashboardFragmentNew.crossing?.amount
        binding.tvAccountNumberValue.text=DashboardFragmentNew.accountDetailsData?.accountInformation?.number
        binding.tvVehicleRegistrationValue.text=DashboardFragmentNew.dateRangeModel?.vehicleNumber
        binding.tvTimeValue.text=DashboardFragmentNew.crossing?.entryTime
        binding.tvLocationValue.text=DashboardFragmentNew.crossing?.entryPlaza
        binding.tvStatusValue.text=DashboardFragmentNew.crossing?.tranSettleStatus
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}