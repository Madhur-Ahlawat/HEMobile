package com.conduent.nationalhighways.ui.bottomnav.dashboard.adapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.AdapterAccountPaymentHistoryBinding
import com.conduent.nationalhighways.databinding.ItemRecentTansactionsBinding
import com.conduent.nationalhighways.ui.bottomnav.account.payments.history.AccountPaymentHistoryFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardFragment
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants

class RecentTransactionsAdapter(
    val fragment: Fragment,
    private val transactionDataList: List<TransactionData?>?
) : RecyclerView.Adapter<RecentTransactionsAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder(
            ItemRecentTansactionsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(transactionDataList?.get(position))
        holder.binding.clMain.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Constants.DATA, transactionDataList?.get(position))
            if (fragment is DashboardFragment)
                it.findNavController().navigate(
                    R.id.action_accountPaymentHistoryFragment_to_accountPaymentHistoryItemDetailFragment,
                    bundle
                )
        }
    }

    override fun getItemCount(): Int {
        return transactionDataList!!.size
    }

    class PaymentViewHolder(var binding: ItemRecentTansactionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionData: TransactionData?) {
            transactionData?.run {
                binding?.apply {
                    valueTopUpAmount.text = amount
                    valueCurrentBalance.text = balance
                    if (valueTopUpAmount?.text?.contains("-") == false) {
                        verticalStripTransactionType.setBackgroundColor(Color.GREEN)
                        indicatorIconTransactionType.setBackgroundColor(Color.GREEN)

                    } else {
                        verticalStripTransactionType.setBackgroundColor(Color.RED)
                        indicatorIconTransactionType.setBackgroundColor(Color.RED)

                    }
                }
//                binding.transactionDate.text = "${exitTime?.let { DateUtils.convertTimeFormat(it,0) }}, ${DateUtils.convertDateFormat(transactionDate, 0)}"
//                binding.transactionNumber.text = transactionNumber
//                binding.paymentAmount.text = amount
//                binding.topUpPlan.text = exitPlazaName
            }
        }
    }

}