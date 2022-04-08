package com.heandroid.ui.bottomnav.account.payments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.accountpayment.TransactionData
import com.heandroid.databinding.AdapterAccountPaymentHistoryBinding
import com.heandroid.utils.common.Constants

class AccountPaymentHistoryAdapter(
    val fragment: Fragment,
    private val transactionDataList: List<TransactionData?>?
) : RecyclerView.Adapter<AccountPaymentHistoryAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder(
            AdapterAccountPaymentHistoryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(transactionDataList?.get(position))
        holder.binding.cvMain.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Constants.DATA, transactionDataList?.get(position))
            if (fragment is AccountPaymentHistoryFragment)
                it.findNavController().navigate(
                    R.id.action_accountPaymentHistoryFragment_to_accountPaymentHistoryItemDetailFragment,
                    bundle
                )
        }
    }

    override fun getItemCount(): Int = transactionDataList?.size ?: 0

    class PaymentViewHolder(var binding: AdapterAccountPaymentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionData: TransactionData?) {
            transactionData?.run {
                binding.transactionDate.text = transactionDate
                binding.transactionNumber.text = transactionNumber
                binding.paymentAmount.text = amount
            }
        }
    }

}