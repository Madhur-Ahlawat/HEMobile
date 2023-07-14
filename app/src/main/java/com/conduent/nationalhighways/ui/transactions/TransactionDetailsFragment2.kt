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
import com.conduent.nationalhighways.databinding.FragmentCrossingDetails2Binding
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
import com.conduent.nationalhighways.ui.payment.MakeOneOfPaymentViewModel
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.GenericRecyclerViewAdapter
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import javax.inject.Inject

@AndroidEntryPoint
class TransactionDetailsFragment2 : BaseFragment<FragmentCrossingDetails2Binding>() {

    private var dateRangeModel: PaymentDateRangeModel?=null
    private var topup: String?=null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCrossingDetails2Binding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun onResume() {
        super.onResume()
        binding.crossingAmount.text=DashboardFragmentNew.crossing?.amount
        binding.tvPaymentDateValue.text=DashboardFragmentNew.crossing?.transactionDate
        binding.tvPaymentTimeValue.text=DashboardFragmentNew.dateRangeModel?.vehicleNumber
        binding.tvPaymentReferenceValue.text=DashboardFragmentNew.crossing?.transactionNumber
        binding.tvTypeOfPaymentValue.text=DashboardFragmentNew.crossing?.activity
        binding.tvPaymentMethodValue.text=DashboardFragmentNew.crossing?.rebillPaymentType?.substring(0,
            DashboardFragmentNew.crossing?.rebillPaymentType?.indexOf("-")!! -1)
        binding.tvChannelValue.text=Constants.CHANNEL_WEB
        binding.tvFourDigitsOfTheCardValue.text=DashboardFragmentNew.crossing?.rebillPaymentType?.substring(DashboardFragmentNew.crossing?.rebillPaymentType?.indexOf("-")!!+1,DashboardFragmentNew.crossing?.rebillPaymentType?.length!!-1)
    }

    override fun initCtrl() {
    }

    override fun observer() {
        observe(dashboardViewModel.whereToReceivePaymentReceipt, ::receipt)
    }

    private fun receipt(resource: Resource<ResponseBody?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    //navigate to success page
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {}
        }

    }
}