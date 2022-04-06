package com.heandroid.ui.bottomnav.account.payments.accountpaymenthistory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.accountpayment.TransactionData
import com.heandroid.databinding.AdapterAccountPaymentHistoryBinding

class AccountPaymentHistoryAdapter(val context: Context, private val transactionDataList: MutableList<TransactionData?>?) : RecyclerView.Adapter<AccountPaymentHistoryAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder(AdapterAccountPaymentHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(transactionDataList?.get(position))

    }

    override fun getItemCount(): Int = transactionDataList?.size ?: 0

    class PaymentViewHolder(var binding: AdapterAccountPaymentHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(transactionData: TransactionData?) {
            transactionData?.run {
                binding.transactionDate.text = transactionDate
                binding.transactionNumber.text = transactionNumber
                binding.paymentAmount.text = amount
            }
        }

    }

}