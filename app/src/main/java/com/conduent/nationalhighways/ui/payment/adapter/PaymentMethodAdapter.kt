package com.conduent.nationalhighways.ui.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.databinding.PaymentmethodadapterlayoutBinding

class PaymentMethodAdapter(private var context:Context,private var paymentList:ArrayList<String>,private val paymentMethodCallback:PaymentMethodCallback
):
    RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentMethodViewHolder= PaymentMethodViewHolder(PaymentmethodadapterlayoutBinding.inflate(
        LayoutInflater.from(context),parent,false))

    class PaymentMethodViewHolder(val binding:PaymentmethodadapterlayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onBindViewHolder(
        holder:PaymentMethodViewHolder,
        position: Int
    ) {
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    interface PaymentMethodCallback{
        fun paymentMethodCallback(position: Int)

    }
}