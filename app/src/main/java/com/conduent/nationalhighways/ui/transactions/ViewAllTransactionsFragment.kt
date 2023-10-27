package com.conduent.nationalhighways.ui.transactions

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.AllTransactionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.paymentHistoryListData
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.transactions.adapter.LoadMoreAdapter
import com.conduent.nationalhighways.ui.transactions.adapter.TransactionsAdapter
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.Utils.sortTransactionsDateWiseDescending
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.isVisible
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import javax.inject.Inject


@AndroidEntryPoint
class ViewAllTransactionsFragment : BaseFragment<AllTransactionsBinding>() {

    private val countPerPage = 20
    private var noOfPages = 0
    private var paymentHistoryHashMap: MutableMap<String, MutableList<TransactionData>> =
        hashMapOf()
    private var paymentHistoryDatesList: MutableList<String> = mutableListOf()
    private var mLayoutManager: LinearLayoutManager? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    val dfDate = SimpleDateFormat("dd MMM yyyy")

    //    private val recentTransactionAdapter: GenericRecyclerViewAdapter<TransactionData> by lazy { createPaymentsHistoryListAdapter() }
    private var transactionsAdapter: TransactionsAdapter? = null

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
        transactionsAdapter = TransactionsAdapter(
            this@ViewAllTransactionsFragment,
            paymentHistoryDatesList,
            paymentHistoryHashMap
        )
        mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        binding.rvRecenrTransactions.run {
            if (itemDecorationCount == 0) {
                addItemDecoration(RecyclerViewItemDecorator(0, 1))
            }
        }


        binding.rvRecenrTransactions.apply {
            layoutManager = mLayoutManager
            adapter = transactionsAdapter
        }
        getPaymentHistoryList(1)
    }

    override fun onResume() {
        super.onResume()
//        getPaymentHistoryList(currentPage+1)
    }

    override fun initCtrl() {
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
                        binding.rvRecenrTransactions.visible()
                        paymentHistoryListData?.clear()
                        paymentHistoryListData?.addAll(it)
                        paymentHistoryListData =
                            sortTransactionsDateWiseDescending(paymentHistoryListData).toMutableList()
                        paymentHistoryDatesList.clear()
                        getDatesList(paymentHistoryListData)
                        transactionsAdapter?.notifyDataSetChanged()
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

    private fun getPaymentHistoryList(
        index: Int
    ) {
        val request = AccountPaymentHistoryRequest(
            index,
            Constants.ALL_TRANSACTION,
            100
        )
        dashboardViewModel.paymentHistoryDetails(request)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as HomeActivityMain).showHideToolbar(true)
    }

    fun getDatesList(transactionsList: MutableList<TransactionData>) {
        var tempDate: String? = null
        transactionsList.forEach {
            if (tempDate == null) {
                tempDate = it.transactionDate
                paymentHistoryDatesList.add(it.transactionDate!!)

            } else {
                if (!dfDate.parse(tempDate).equals(dfDate.parse(it.transactionDate))) {
                    paymentHistoryDatesList.add(it.transactionDate!!)
                    tempDate = it.transactionDate
                }
            }
            var transactionsListTemp =
                paymentHistoryHashMap.get(it.transactionDate) ?: mutableListOf()
            transactionsListTemp?.add(it)
            paymentHistoryHashMap.remove(it.transactionDate!!)
            paymentHistoryHashMap.put(it.transactionDate!!, transactionsListTemp!!)
        }
    }
}