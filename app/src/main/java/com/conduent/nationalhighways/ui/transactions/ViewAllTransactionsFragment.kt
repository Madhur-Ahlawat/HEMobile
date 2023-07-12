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
import com.conduent.nationalhighways.databinding.FragmentDashboardBinding
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.databinding.ItemRecentTansactionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
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
class ViewAllTransactionsFragment : BaseFragment<AllTransactionsBinding>() {

    private var paymentHistoryListData: MutableList<TransactionData?> = ArrayList()
    private var noOfPages: Int?=0
    private var mLayoutManager: LinearLayoutManager?=null
    private var dateRangeModel: PaymentDateRangeModel?=null
    private var topup: String?=null
    private val countPerPage = 10
    private var startIndex = 1
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val recentTransactionAdapter: GenericRecyclerViewAdapter<TransactionData> by lazy { createPaymentsHistoryListAdapter() }

    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AllTransactionsBinding.inflate(inflater, container, false)


    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun onResume() {
        super.onResume()
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        getPaymentHistoryList(startIndex)
    }

    override fun initCtrl() {
        initTransactionsRecyclerView()
    }

    override fun observer() {
        observe(dashboardViewModel.paymentHistoryLiveData, ::handlePaymentResponse)
    }

    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.transactionList?.count?.let {
                    noOfPages = if (it.toInt() % countPerPage == 0) {
                        it.toInt() / countPerPage
                    } else {
                        (it.toInt() / countPerPage) + 1
                    }
                }
                resource.data?.transactionList?.transaction?.let {
                    if (it.isNotEmpty()) {
                        binding.rvRecenrTransactions.visible()
                        paymentHistoryListData.clear()
                        paymentHistoryListData.addAll(it)
                        paymentHistoryListData.addAll(it)
                        recentTransactionAdapter.submitList(paymentHistoryListData)
                    } else {
                        binding.rvRecenrTransactions.gone()
//                        binding.paginationLayout.gone()
                    }
                } ?: run {
                    binding.rvRecenrTransactions.gone()
//                    binding.paginationLayout.gone()
                }
            }

            is Resource.DataError -> {
                binding.rvRecenrTransactions.gone()
//                binding.paginationLayout.gone()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }
    }
    fun areRecentTransactionsSame(item1: TransactionData, item2: TransactionData): Boolean {
        return ((item1.transactionNumber == item2.transactionNumber) && (item1.transactionNumber == item2.transactionNumber) && (item1.transactionNumber == item2.transactionNumber))
    }
    private fun getPaymentHistoryList(
        index: Int
    ) {
        dateRangeModel =
            PaymentDateRangeModel(
                filterType = Constants.PAYMENT_FILTER_SPECIFIC,
                DateUtils.lastPriorDate(-30), DateUtils.currentDate(), ""
            )
        val request = AccountPaymentHistoryRequest(
            index,
            Constants.PAYMENT,
            countPerPage,
            dateRangeModel?.startDate,
            dateRangeModel?.endDate,
            dateRangeModel?.vehicleNumber
        )
        dashboardViewModel.paymentHistoryDetails(request)
    }
    private fun initTransactionsRecyclerView() {
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvRecenrTransactions.run {
            if (itemDecorationCount == 0) {
                addItemDecoration(RecyclerViewItemDecorator(20, 1))
            }
            binding.rvRecenrTransactions.layoutManager = mLayoutManager
            adapter = recentTransactionAdapter
        }
    }
    fun createPaymentsHistoryListAdapter() = GenericRecyclerViewAdapter(
        getViewLayout = { R.layout.item_recent_tansactions },
        areItemsSame = ::areRecentTransactionsSame,
        areItemContentsEqual = ::areRecentTransactionsSame,
        onBind = { recentTransactionItem, viewDataBinding, _ ->
            with(viewDataBinding as ItemAllTansactionsBinding) {
                viewDataBinding.apply {
                    valueCurrentBalance.text = recentTransactionItem.balance
                    tvTransactionType.text =
                        recentTransactionItem.activity?.substring(0, 1)!!.toUpperCase().plus(
                            recentTransactionItem.activity?.substring(
                                1,
                                recentTransactionItem.activity.length
                            )!!.toLowerCase()
                        )
                    if (recentTransactionItem.amount?.contains("-") == false) {
                        verticalStripTransactionType.background.setTint(resources.getColor(R.color.green_status))
                        indicatorIconTransactionType.background.setTint(resources.getColor(R.color.green_status))
                        topup = "+" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.green_status))
                    } else {
                        verticalStripTransactionType.background.setTint(resources.getColor(R.color.red_status))
                        indicatorIconTransactionType.background.setTint(resources.getColor(R.color.red_status))
                        topup = "-" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.red_status))
                    }
                    root.setOnClickListener {
                        valueTopUpAmount
                    }
                }
            }
        }
    )
}