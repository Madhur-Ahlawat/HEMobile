package com.conduent.nationalhighways.ui.transactions

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.AllTransactionsBinding
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.paymentHistoryListData
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.transactions.adapter.TransactionsAdapter
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils.sortTransactionsDateWiseDescending
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class ViewAllTransactionsFragment : BaseFragment<AllTransactionsBinding>(), BackPressListener {

    private val countPerPage = 20
    private var noOfPages = 0
    private var paymentHistoryHashMap: MutableMap<String, MutableList<TransactionData>> =
        hashMapOf()
    private var paymentHistoryDatesList: MutableList<String> = mutableListOf()
    private var mLayoutManager: LinearLayoutManager? = null
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private val dfDate = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    private var transactionsAdapter: TransactionsAdapter? = null

    @Inject
    lateinit var sessionManager: SessionManager
    private var transactionType = Constants.ALL_TRANSACTION

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AllTransactionsBinding.inflate(inflater, container, false)


    override fun init() {
        transactionsAdapter = TransactionsAdapter(
            this@ViewAllTransactionsFragment,
            paymentHistoryDatesList,
            paymentHistoryHashMap,
            dashboardViewModel.accountInformationData.value?.accSubType ?: ""
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

//        if (dashboardViewModel.accountInformationData.value?.accSubType.equals(Constants.PAYG)) {
//            transactionType = Constants.TOLL_TRANSACTION
//        }
        transactionType = Constants.TOLL_TRANSACTION

        getPaymentHistoryList()
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
            (requireActivity() as HomeActivityMain).setTitle(getString(R.string.transactions))
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
        setBackPressListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
        observe(dashboardViewModel.paymentHistoryLiveData, ::handlePaymentResponse)
    }


    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        Log.e("TAG", "getPaymentHistoryList: showLoaderDialog dismissLoaderDialog ")
        dismissLoaderDialog()
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
                        binding.apply {
                            ivNoTransactions.gone()
                            tvNoTransactions.gone()
                        }
                        binding.rvRecenrTransactions.visible()
                        paymentHistoryListData.clear()
                        paymentHistoryHashMap.clear()
                        paymentHistoryListData.addAll(it)
                        paymentHistoryListData =
                            sortTransactionsDateWiseDescending(paymentHistoryListData).toMutableList()
                        paymentHistoryDatesList.clear()
                        getDatesList(paymentHistoryListData)
                        transactionsAdapter?.notifyDataSetChanged()
                    } else {
                        binding.apply {
                            rvRecenrTransactions.gone()
                            ivNoTransactions.visible()
                            tvNoTransactions.visible()
                        }
                    }
                } ?: run {
                    binding.apply {
                        rvRecenrTransactions.gone()
                        ivNoTransactions.visible()
                        tvNoTransactions.visible()
                    }
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
    ) {
        showLoaderDialog()
        Log.e("TAG", "getPaymentHistoryList: showLoaderDialog ")
        var priorDays = 90
        var searchDate = "Transaction Date"
        if (dashboardViewModel.accountInformationData.value?.accSubType == "BUSINESS"
            || dashboardViewModel.accountInformationData.value?.accSubType === "NONREVENUE"
        ) {
            priorDays = 30
        }
        if(transactionType ==Constants.ALL_TRANSACTION){
            searchDate=""
        }
        val request = AccountPaymentHistoryRequest(
            1,
            transactionType,
            20,
            endDate = DateUtils.currentDateAs(DateUtils.dd_mm_yyyy),
            startDate = DateUtils.getLast90DaysDate(DateUtils.dd_mm_yyyy, priorDays),
            searchDate=searchDate
        )
        dashboardViewModel.paymentHistoryDetails(request)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }
    }

    private fun getDatesList(transactionsList: MutableList<TransactionData>) {
        var tempDate: String? = null
        transactionsList.forEach {
            if (tempDate == null) {
                tempDate = it.transactionDate
                paymentHistoryDatesList.add(it.transactionDate!!)

            } else {
                if ((dfDate.parse(tempDate.toString())
                        ?.equals(dfDate.parse(it.transactionDate.toString()))) == false
                ) {
                    paymentHistoryDatesList.add(it.transactionDate ?: "")
                    tempDate = it.transactionDate
                }
            }
            val transactionsListTemp =
                paymentHistoryHashMap[it.transactionDate] ?: mutableListOf()
            transactionsListTemp.add(it)
            paymentHistoryHashMap.remove(it.transactionDate ?: "")
            paymentHistoryHashMap[it.transactionDate ?: ""] = transactionsListTemp
        }
    }

    override fun onBackButtonPressed() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).backPressLogic()
        }
    }
}