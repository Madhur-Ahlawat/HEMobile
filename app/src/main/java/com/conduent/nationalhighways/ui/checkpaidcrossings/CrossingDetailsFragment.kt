package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
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
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
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
    private var topUp: String? = null
    private var data: CrossingDetailsModelsResponse? = null
    private val viewModel: DashboardViewModel by viewModels()
    private val recentTransactionAdapter: GenericRecyclerViewAdapter<CheckedCrossingRecentTransactionsResponseModelItem> by lazy { createPaymentsHistoryListAdapter() }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCrossingDetailsBinding =
        FragmentCrossingDetailsBinding.inflate(inflater, container, false)

    override fun init() {

        if (requireActivity() is CheckPaidCrossingActivity) {
            (requireActivity() as CheckPaidCrossingActivity).focusToolBarCrossingDetails()
        }

        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }

        data.let {
            val crossings = it?.unusedTrip?.toInt()
            binding.fullName.text = it?.referenceNumber
            binding.fullName.contentDescription =
                Utils.accessibilityForNumbers(it?.referenceNumber ?: "")
            if (crossings == 1) {
                binding.address.text =
                    resources.getString(R.string.str_crossing_data, crossings.toString())
            } else {
                binding.address.text =
                    resources.getString(R.string.str_crossings_data, crossings.toString())
            }
            binding.emailAddress.text = it?.expirationDate?.let { it1 ->
                DateUtils.convertStringDatetoAnotherFormat(
                    it1, DateUtils.mm_dd_yyyy_hh_mm_ss_a, DateUtils.dd_mmm_yyyy
                )
            }
            binding.valueVehicleRegistrationNumber.text = it?.plateNumberToTransfer
            binding.valueVehicleRegistrationNumber.contentDescription =
                Utils.accessibilityForNumbers(it?.plateNumberToTransfer ?: "")
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH)
            val date = LocalDateTime.parse(it?.expirationDate, formatter)
            if (date.isBefore(current)) {
                binding.errorTxt.visible()
                binding.errorTxt.text =
                    getString(R.string.your_credit_expired_on_s_you_must_pay_for_any_further_crossings_you_intend_to_make,
                        it?.expirationDate?.let { it1 -> DateUtils.convertDateFormatToDateFormat(it1) })
            }
            if (crossings == 0) {
                binding.transferBtn.gone()
                binding.errorTxt.visible()
                binding.emailCard.gone()
                binding.errorTxt.text =
                    getString(R.string.you_have_no_credit_left_you_must_pay_for_any_further_crossings_you_intend_to_make)
            } else {
                binding.transferBtn.visible()
                binding.errorTxt.gone()
                binding.emailCard.visible()
            }
        }
        initLoaderDialog()
        initTransactionsRecyclerView()
        getPaymentHistoryList()


        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.transferBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

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
                if(requireActivity() is HomeActivityMain){
                    (requireActivity() as HomeActivityMain).setTitle(getString(R.string.transfer_remaining_credit))
                }
                findNavController().navigate(
                    R.id.action_crossing_details_to_find_vehicles,
                    bundle
                )
            }

            R.id.cancel_btn -> {
                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
            }

        }


    }

    private fun getPaymentHistoryList() {
        showLoaderDialog()
        HomeActivityMain.dateRangeModel =
            PaymentDateRangeModel(
                filterType = Constants.PAYMENT_FILTER_SPECIFIC,
                DateUtils.lastPriorDate(-30), DateUtils.currentDate(), ""
            )
        val request = CheckedCrossingTransactionsRequestModel(transactionType = "Toll_Transaction")
        viewModel.paymentHistoryDetailsCheckCrossings(request)
    }

    private fun initLoaderDialog() {

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

    private fun createPaymentsHistoryListAdapter() = GenericRecyclerViewAdapter(
        getViewLayout = { R.layout.item_recent_tansactions_checked_crossings },
        areItemsSame = ::areRecentTransactionsSame,
        areItemContentsEqual = ::areRecentTransactionsSame,
        onBind = { recentTransactionItem, viewDataBinding, _ ->
            with(viewDataBinding as ItemRecentTansactionsCheckedCrossingsBinding) {
                viewDataBinding.apply {
                    tvDate.text = resources.getString(
                        R.string.concatenate_two_strings_with_space,
                        recentTransactionItem.entryDate, recentTransactionItem.exitTime
                    )
                    tvTransactionType.text =
                        recentTransactionItem.activity?.substring(0, 1)
                            ?.uppercase(Locale.getDefault())
                            .plus(
                                recentTransactionItem.activity?.substring(
                                    1,
                                    recentTransactionItem.activity?.length ?: 0
                                )?.lowercase(Locale.getDefault())
                            )
                    if (recentTransactionItem.activity?.lowercase()?.contains("toll") == false) {
                        verticalStripTransactionType.setBackgroundColor(
                            resources.getColor(
                                R.color.green_status,
                                null
                            )
                        )
                        indicatorIconTransactionType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_euro_circular_green,
                                null
                            )
                        )
                        topUp = "+" + recentTransactionItem.amount
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
                                R.color.red_status, null
                            )
                        )
                        topUp =
                            if (HomeActivityMain.accountDetailsData?.accountInformation?.accSubType.equals(
                                    Constants.EXEMPT_PARTNER
                                )
                            ) {
                                recentTransactionItem.amount
                            } else {
                                "-" + recentTransactionItem.amount
                            }
                        indicatorIconEuro.gone()
                        Glide.with(indicatorIconTransactionType.context)
                            .load(R.drawable.ic_car_grey)
                            .into(indicatorIconTransactionType)
                    }
                    root.setOnClickListener {
                        HomeActivityMain.checkedCrossing = recentTransactionItem
                        HomeActivityMain.crossing = TransactionData(
                            amount = recentTransactionItem.amount,
                            transactionDate = recentTransactionItem.txDate,
                            exitTime = recentTransactionItem.exitTime,
                            plateNumber = recentTransactionItem.plateNumber,
                            exitDirection = recentTransactionItem.exitDirection,
                            tranSettleStatus = ""
                        )
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                        if (HomeActivityMain.checkedCrossing?.activity?.lowercase()
                                .equals("toll")
                        ) {
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

    private fun areRecentTransactionsSame(
        item1: CheckedCrossingRecentTransactionsResponseModelItem,
        item2: CheckedCrossingRecentTransactionsResponseModelItem
    ): Boolean {
        return (item1.entryTime == item2.entryTime)
    }

    private fun handlePaymentResponse(resource: Resource<CheckedCrossingRecentTransactionsResponseModel?>?) {
        dismissLoaderDialog()
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

    private fun sortTransactionsDateWiseDescendingCheckedCrossings(transactions: MutableList<CheckedCrossingRecentTransactionsResponseModelItem?>): MutableList<CheckedCrossingRecentTransactionsResponseModelItem> {
        val transactionListSorted: MutableList<CheckedCrossingRecentTransactionsResponseModelItem> =
            mutableListOf()
        for (transaction in transactions) {
            if (transactionListSorted.isEmpty()) {
                transaction?.let { transactionListSorted.add(it) }
            } else {
                if (DateUtils.compareDates(
                        transactionListSorted.last().entryDate + " " + transactionListSorted.last().exitTime,
                        transaction?.entryDate + " " + transaction?.exitTime
                    )
                ) {
                    if (transaction != null) {
                        transactionListSorted.add(transactionListSorted.size - 1, transaction)
                    }

                } else {
                    if (transaction != null) {
                        transactionListSorted.add(transaction)
                    }
                }
            }
        }
        return transactionListSorted
    }
}