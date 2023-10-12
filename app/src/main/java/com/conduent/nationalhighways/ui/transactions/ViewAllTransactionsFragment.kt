package com.conduent.nationalhighways.ui.transactions

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.AllTransactionsBinding
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.GenericRecyclerViewAdapter
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import javax.inject.Inject


@AndroidEntryPoint
class ViewAllTransactionsFragment : BaseFragment<AllTransactionsBinding>() {

    private var loading: Boolean=false
    private var pastVisibleItems: Int=0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0;
    private var currentPage: Int = 1
    private var transactionItem: TransactionData? = null
    private var paymentHistoryListData: MutableList<TransactionData?> = ArrayList()
    private var noOfPages: Int? = 0
    private var mLayoutManager: LinearLayoutManager? = null
    private var dateRangeModel: PaymentDateRangeModel? = null
    private var topup: String? = null
    private val countPerPage = 10
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
        transactionItem=null
    }

    override fun onResume() {
        super.onResume()
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        getPaymentHistoryList(currentPage)
    }

    override fun initCtrl() {
        initTransactionsRecyclerView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observer() {
        observe(dashboardViewModel.paymentHistoryLiveData, ::handlePaymentResponse)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
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
                        binding.ivNoTransactions.gone()
                        binding.tvNoTransactions.gone()
                        binding.rvRecenrTransactions.visible()
                        paymentHistoryListData.clear()
                        paymentHistoryListData.addAll(it)
                        paymentHistoryListData =
                            sortTransactionsDateWiseDescending(it).toMutableList()
                        recentTransactionAdapter.submitList(
                            paymentHistoryListData
                        )
                    } else {
                        binding.rvRecenrTransactions.gone()
                        binding.ivNoTransactions.visible()
                        binding.tvNoTransactions.visible()
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
                addItemDecoration(RecyclerViewItemDecorator(0, 1))
            }
            binding.rvRecenrTransactions.layoutManager = mLayoutManager
            adapter = recentTransactionAdapter
        }
    }

    val dfDate = SimpleDateFormat("dd MMM yyyy")

    fun createPaymentsHistoryListAdapter() = GenericRecyclerViewAdapter(
        getViewLayout = { R.layout.item_all_tansactions },
        areItemsSame = ::areRecentTransactionsSame,
        areItemContentsEqual = ::areRecentTransactionsSame,
        onBind = { recentTransactionItem, viewDataBinding, _ ->
            with(viewDataBinding as ItemAllTansactionsBinding) {
                viewDataBinding.apply {
                    if (transactionItem == null) {
                        transactionItem = recentTransactionItem
                        headerDate.text = recentTransactionItem.transactionDate
                        headerDate.visible()
                    } else {
                        if (dfDate.parse(transactionItem!!.transactionDate) != dfDate.parse(
                                recentTransactionItem.transactionDate
                            )
                        ) {
                            headerDate.text = recentTransactionItem.transactionDate
                            headerDate.visible()
                        } else {
                            headerDate.gone()
                        }
                        transactionItem = recentTransactionItem
                    }

                    valueCurrentBalance.text = recentTransactionItem.balance
//                    tvTransactionType.text =
//                        recentTransactionItem.activity?.substring(0, 1)!!.toUpperCase().plus(
//                            recentTransactionItem.activity?.substring(
//                                1,
//                                recentTransactionItem.activity.length
//                            )!!.toLowerCase()
//                        )
                    if (recentTransactionItem.amount?.contains("-") == false) {
                        tvTransactionType.text = resources.getString(R.string.top_up)
                        verticalStripTransactionType.background.setTint(resources.getColor(R.color.green_status))
                        topup = "+" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.green_status))
                    } else {
                        tvTransactionType.text = recentTransactionItem.exitDirection
                        verticalStripTransactionType.background.setTint(resources.getColor(R.color.red_status))
                        topup = "-" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.red_status))
                    }

                    root.setOnClickListener {
                        HomeActivityMain.setTitle(resources.getString(R.string.payment_details))
                        val bundle = Bundle()
                        crossing = recentTransactionItem
//                        bundle.putInt(Constants.FROM, Constants.FROM_ALL_TRANSACTIONS_TO_DETAILS)
                        if (crossing!!.activity.equals("Toll")) {
                            findNavController().navigate(
                                R.id.action_crossingHistoryFragment_to_tollDetails,
                                bundle
                            )
                        } else {
                            findNavController().navigate(
                                R.id.action_crossingHistoryFragment_to_topUpDetails,
                                bundle
                            )
                        }
                    }
                    if (recentTransactionItem.activity.equals("Toll")) {
                        indicatorIconTransactionType.setImageDrawable(resources.getDrawable(R.drawable.ic_car_grey))
                        indicatorIconEuro.gone()
                    } else {
                        indicatorIconTransactionType.setImageDrawable(resources.getDrawable(R.drawable.ic_euro_circular_green))
                        indicatorIconEuro.visible()
                    }
                }
            }
        }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactionsDateWiseDescending(transactions: MutableList<TransactionData?>): MutableList<TransactionData> {
        var transactionListSorted: MutableList<TransactionData> = mutableListOf()
        val dfDate = SimpleDateFormat("dd MMM yyyy")
        for (transaction in transactions) {
            if (transactionListSorted?.isEmpty() == true) {
                transaction!!.showDateHeader = true
                transactionListSorted.add(transaction!!)
            } else {
                if (DateUtils.compareDates(
                        transactionListSorted.last().transactionDate + " " + transactionListSorted.last().exitTime,
                        transaction?.transactionDate + " " + transaction?.exitTime
                    )
                ) {
                    transactionListSorted.add(transactionListSorted.size - 1, transaction!!)

                } else {
                    transactionListSorted.add(transaction!!)
                }
            }

        }
        return transactionListSorted
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as HomeActivityMain).showHideToolbar(true)
    }
}