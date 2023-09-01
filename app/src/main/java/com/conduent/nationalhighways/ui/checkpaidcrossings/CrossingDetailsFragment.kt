package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.FragmentCrossingDetailsBinding
import com.conduent.nationalhighways.databinding.ItemRecentTansactionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.GenericRecyclerViewAdapter
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@AndroidEntryPoint
class CrossingDetailsFragment : BaseFragment<FragmentCrossingDetailsBinding>(),
    View.OnClickListener {
    private var mLayoutManager: LinearLayoutManager? = null
    private var topup: String? = null
    private var loader: LoaderDialog? = null
    private var data: CrossingDetailsModelsResponse? = null
    private val countPerPage = 10
    private var startIndex = 1
    private var noOfPages = 1
    private val viewModel: DashboardViewModel by viewModels()
    private val recentTransactionAdapter: GenericRecyclerViewAdapter<TransactionData> by lazy { createPaymentsHistoryListAdapter() }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCrossingDetailsBinding =
        FragmentCrossingDetailsBinding.inflate(inflater, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {


        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }

        data.let {
            val crossings = it?.unusedTrip?.toInt()
            binding.fullName.text = it?.referenceNumber
            binding.companyName.text = it?.plateNumber
            binding.address.text = crossings.toString()+ " crossings"
            binding.emailAddress.text = it?.expirationDate?.let { it1 ->
                DateUtils.convertDateFormatToDateFormat(
                    it1
                )
            }
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH)
            val date = LocalDateTime.parse(it?.expirationDate, formatter)
            if(date.isBefore(current)){
                binding.errorTxt.visible()
                binding.errorTxt.text = getString(R.string.your_credit_expired_on_s_you_must_pay_for_any_further_crossings_you_intend_to_make
                , it?.expirationDate?.let { it1 -> DateUtils.convertDateFormatToDateFormat(it1) })
            }
            if(crossings==0){
                binding.transferBtn.gone()
                binding.errorTxt.visible()
                binding.errorTxt.text = getString(R.string.you_have_no_credit_left_you_must_pay_for_any_further_crossings_you_intend_to_make)
            }
        }
        initLoaderDialog()
        initTransactionsRecyclerView()
        getPaymentHistoryList(startIndex)
    }


    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.transferBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observer() {
        observe(viewModel.paymentHistoryLiveData, ::handlePaymentResponse)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.transfer_btn -> {
                val bundle = Bundle().apply {
                    putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    putParcelable(Constants.NAV_DATA_KEY, data)
                }
                findNavController().navigate(
                    R.id.action_crossing_details_to_find_vehicles,
                    bundle
                )
            }

            R.id.cancel_btn -> {
                findNavController().popBackStack()
            }
            R.id.nextBtn -> {
                findNavController().navigate(
                    R.id.action_crossing_details_to_create_account
                )
            }

        }


    }

    private fun getPaymentHistoryList(
        index: Int
    ) {
        if (loader?.isVisible == false && loader?.isAdded == true) {
            loader?.showsDialog
        }
        HomeActivityMain.dateRangeModel =
            PaymentDateRangeModel(
                filterType = Constants.PAYMENT_FILTER_SPECIFIC,
                DateUtils.lastPriorDate(-30), DateUtils.currentDate(), ""
            )
        val request = AccountPaymentHistoryRequest(
            index,
            Constants.PAYMENT,
            countPerPage,
            HomeActivityMain.dateRangeModel?.startDate,
            HomeActivityMain.dateRangeModel?.endDate,
            HomeActivityMain.dateRangeModel?.vehicleNumber
        )
        viewModel.paymentHistoryDetails(request)
    }

    private fun initLoaderDialog() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    private fun initTransactionsRecyclerView() {
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvRecenrTransactions.run {
            if (itemDecorationCount == 0) {
                addItemDecoration(RecyclerViewItemDecorator(10, 1))
            }
            layoutManager = mLayoutManager
            adapter = recentTransactionAdapter
        }
    }

    fun createPaymentsHistoryListAdapter() = GenericRecyclerViewAdapter(
        getViewLayout = { R.layout.item_recent_tansactions },
        areItemsSame = ::areRecentTransactionsSame,
        areItemContentsEqual = ::areRecentTransactionsSame,
        onBind = { recentTransactionItem, viewDataBinding, _ ->
            with(viewDataBinding as ItemRecentTansactionsBinding) {
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
                        verticalStripTransactionType.setBackgroundColor(resources.getColor(R.color.green_status))
                        indicatorIconTransactionType.setImageDrawable(resources.getDrawable(R.drawable.ic_euro_circular_green))
                        topup = "+" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.green_status))
                    } else {
                        verticalStripTransactionType.setBackgroundColor(resources.getColor(R.color.red_status))
                        indicatorIconTransactionType.setImageDrawable(resources.getDrawable(R.drawable.ic_car_grey))
                        topup = "-" + recentTransactionItem.amount
                        valueTopUpAmount.text = topup
                        valueTopUpAmount.setTextColor(resources.getColor(R.color.red_status))
                    }
                    root.setOnClickListener {
                        HomeActivityMain.crossing = recentTransactionItem
                        val bundle = Bundle()
//                        bundle.putInt(Constants.FROM, Constants.FROM_ALL_TRANSACTIONS_TO_DETAILS)
                        if (HomeActivityMain.crossing?.activity.equals("Toll")) {
                            findNavController().navigate(
                                R.id.action_crossing_details_to_tollDetails,
                                bundle
                            )
                        } else {
                            findNavController().navigate(
                                R.id.action_crossing_details_to_topUpDetails,
                                bundle
                            )
                        }
                    }
                }
            }
        }
    )

    fun areRecentTransactionsSame(item1: TransactionData, item2: TransactionData): Boolean {
        return ((item1.transactionNumber == item2.transactionNumber) && (item1.transactionNumber == item2.transactionNumber) && (item1.transactionNumber == item2.transactionNumber))
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
                        binding.tvNoHistory.gone()
                        binding.rvRecenrTransactions.visible()
                        HomeActivityMain.paymentHistoryListData.clear()
                        HomeActivityMain.paymentHistoryListData.addAll(it)
                        HomeActivityMain.paymentHistoryListData =
                            sortTransactionsDateWiseDescending(HomeActivityMain.paymentHistoryListData).toMutableList()
                        recentTransactionAdapter.submitList(
                            sortTransactionsDateWiseDescending(
                                HomeActivityMain.paymentHistoryListData
                            )
                        )
                    } else {
                        binding.rvRecenrTransactions.gone()
                        binding.tvNoHistory.visible()
//                        binding.paginationLayout.gone()
                    }
                } ?: run {
                    binding.rvRecenrTransactions.gone()
                    binding.tvNoHistory.visible()
//                    binding.paginationLayout.gone()
                }
            }

            is Resource.DataError -> {
                binding.rvRecenrTransactions.gone()
                binding.tvNoHistory.visible()
//                binding.paginationLayout.gone()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactionsDateWiseDescending(transactions: MutableList<TransactionData?>): MutableList<TransactionData> {
        var transactionListSorted: MutableList<TransactionData> = mutableListOf()
        for (transaction in transactions) {
            if (transactionListSorted?.isEmpty() == true) {
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
}