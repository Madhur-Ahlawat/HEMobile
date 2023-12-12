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
import com.bumptech.glide.Glide
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingRecentTransactionsResponseModel
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingRecentTransactionsResponseModelItem
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingTransactionsRequestModel
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.FragmentCrossingDetailsBinding
import com.conduent.nationalhighways.databinding.ItemRecentTansactionsCheckedCrossingsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.startNormalActivity
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
    var apiState = Constants.UNAVAILABLE
    private var startIndex = 1
    private var noOfPages = 1
    private val viewModel: DashboardViewModel by viewModels()
    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private val recentTransactionAdapter: GenericRecyclerViewAdapter<CheckedCrossingRecentTransactionsResponseModelItem> by lazy { createPaymentsHistoryListAdapter() }

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
            if(crossings==1){
                binding.address.text = resources.getString(R.string.str_crossing_data,crossings.toString())
            }else{
                binding.address.text =  resources.getString(R.string.str_crossings_data,crossings.toString())
            }
            binding.emailAddress.text = it?.expirationDate?.let { it1 ->
                DateUtils.convertDateFormatToDateFormat(
                    it1
                )
            }
            binding?.valueVehicleRegistrationNumber?.text=it?.plateNumberToTransfer
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH)
            val date = LocalDateTime.parse(it?.expirationDate, formatter)
            if(date.isBefore(current)){
                binding.errorTxt.visible()
                binding.errorTxt.text = getString(R.string.your_credit_expired_on_s_you_must_pay_for_any_further_crossings_you_intend_to_make
                , it?.expirationDate?.let { it1 -> DateUtils.convertDateFormatToDateFormat(it1) })
            }
            if(crossings==0){
                binding.transferBtn.gone()
                binding.errorTxt.visible()
                binding.emailCard.gone()
                binding.errorTxt.text = getString(R.string.you_have_no_credit_left_you_must_pay_for_any_further_crossings_you_intend_to_make)
            }
            else{
                binding.transferBtn.visible()
                binding.errorTxt.gone()
                binding.emailCard.visible()
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
        observe(viewModel.paymentHistoryLiveDataCheckedCrossing, ::handlePaymentResponse)
    }
    private fun navigateLandingActivity() {
        requireActivity().startNewActivityByClearingStack(LandingActivity::class.java) {
            putString(Constants.SHOW_SCREEN, Constants.LANDING_SCREEN)
            putString(Constants.NAV_FLOW_FROM, Constants.CHECK_FOR_PAID_CROSSINGS)
        }
        requireActivity().finish()
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> {
                navigateLandingActivity()
            }
            R.id.transfer_btn -> {
                val bundle = Bundle().apply {
                    putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    putParcelable(Constants.NAV_DATA_KEY, data)
                }
                HomeActivityMain.setTitle(getString(R.string.transfer_remaining_credit))
                findNavController().navigate(
                    R.id.action_crossing_details_to_find_vehicles,
                    bundle
                )
            }

            R.id.cancel_btn -> {
                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
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
        val request = CheckedCrossingTransactionsRequestModel(transactionType = "Toll_Transaction")
        viewModel.paymentHistoryDetailsCheckCrossings(request)
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
        getViewLayout = { R.layout.item_recent_tansactions_checked_crossings },
        areItemsSame = ::areRecentTransactionsSame,
        areItemContentsEqual = ::areRecentTransactionsSame,
        onBind = { recentTransactionItem, viewDataBinding, _ ->
            with(viewDataBinding as ItemRecentTansactionsCheckedCrossingsBinding) {
                viewDataBinding.apply {
                    tvDate.text = recentTransactionItem.entryDate+" "+recentTransactionItem.exitTime
                    tvTransactionType.text =
                        recentTransactionItem.activity?.substring(0, 1)!!.toUpperCase().plus(
                            recentTransactionItem.activity?.substring(
                                1,
                                recentTransactionItem.activity!!.length
                            )!!.toLowerCase()
                        )
                    if (recentTransactionItem.activity?.lowercase()?.contains("toll") == false) {
                        verticalStripTransactionType.setBackgroundColor(resources.getColor(R.color.green_status))
                        indicatorIconTransactionType.setImageDrawable(resources.getDrawable(R.drawable.ic_euro_circular_green))
                        topup = "+" + recentTransactionItem.amount
                    } else {
                        if (recentTransactionItem.exitDirection.equals("N")) {
                            tvTransactionType.text =
                                tvTransactionType.context.getString(R.string.northbound)
                        } else {
                            tvTransactionType.text =
                                tvTransactionType.context.getString(R.string.southbound)
                        }
                        verticalStripTransactionType.background.setTint(
                            resources.getColor(
                                R.color.red_status
                            )
                        )
                        if (HomeActivityMain.accountDetailsData?.accountInformation?.accSubType.equals(
                                Constants.EXEMPT_PARTNER
                            )
                        ) {
                            topup = recentTransactionItem.amount
                        } else {
                            topup = "-" + recentTransactionItem.amount
                        }
                        indicatorIconEuro.gone()
                        Glide.with(indicatorIconTransactionType.context)
                            .load(indicatorIconTransactionType.context.getDrawable(R.drawable.ic_car_grey))
                            .into(indicatorIconTransactionType)
                    }
                    root.setOnClickListener {
                        HomeActivityMain.checkedCrossing = recentTransactionItem
                        HomeActivityMain.crossing=null
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                        bundle.putParcelable(Constants.NAV_DATA_KEY,data)
                        if (HomeActivityMain.checkedCrossing?.activity?.toLowerCase().equals("toll")) {
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

    fun areRecentTransactionsSame(item1: CheckedCrossingRecentTransactionsResponseModelItem, item2: CheckedCrossingRecentTransactionsResponseModelItem): Boolean {
        return ((item1.entryTime == item2.entryTime) && (item1.entryTime == item2.entryTime) && (item1.entryTime == item2.entryTime))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handlePaymentResponse(resource: Resource<CheckedCrossingRecentTransactionsResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.isNotEmpty()) {
                        binding.tvNoHistory.gone()
                        binding.rvRecenrTransactions.visible()
                        HomeActivityMain.paymentHistoryListDataCheckedCrossings.clear()
                        HomeActivityMain.paymentHistoryListDataCheckedCrossings.addAll(it)
                        HomeActivityMain.paymentHistoryListDataCheckedCrossings =
                            sortTransactionsDateWiseDescendingCheckedCrossings(HomeActivityMain.paymentHistoryListDataCheckedCrossings).toMutableList()
                        recentTransactionAdapter.submitList(
                            HomeActivityMain.paymentHistoryListDataCheckedCrossings
                        )
                    } else {
                        binding.rvRecenrTransactions.gone()
                        binding.tvNoHistory.visible()
                    }
                } ?: run {
                    binding.rvRecenrTransactions.gone()
                    binding.tvNoHistory.visible()
                }
            }

            is Resource.DataError -> {
                binding.rvRecenrTransactions.gone()
                binding.tvNoHistory.visible()
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
    }@RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactionsDateWiseDescendingCheckedCrossings(transactions: MutableList<CheckedCrossingRecentTransactionsResponseModelItem?>): MutableList<CheckedCrossingRecentTransactionsResponseModelItem> {
        var transactionListSorted: MutableList<CheckedCrossingRecentTransactionsResponseModelItem> = mutableListOf()
        for (transaction in transactions) {
            if (transactionListSorted?.isEmpty() == true) {
                transactionListSorted.add(transaction!!)
            } else {
                if (DateUtils.compareDates(
                        transactionListSorted.last().entryDate + " " + transactionListSorted.last().exitTime,
                        transaction?.entryDate + " " + transaction?.exitTime
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