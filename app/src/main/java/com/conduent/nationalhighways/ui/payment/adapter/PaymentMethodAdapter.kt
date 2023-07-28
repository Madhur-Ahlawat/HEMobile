package com.conduent.nationalhighways.ui.payment.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.databinding.PaymentmethodadapterlayoutBinding

class PaymentMethodAdapter(
    private var context: Context,
    private var paymentList: MutableList<CardListResponseModel?>?,
    private val paymentMethodCallback: PaymentMethodCallback
) :
    RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentMethodViewHolder = PaymentMethodViewHolder(
        PaymentmethodadapterlayoutBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
    )

    class PaymentMethodViewHolder(val binding: PaymentmethodadapterlayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onBindViewHolder(
        holder: PaymentMethodViewHolder,
        position: Int
    ) {
        if (paymentList?.get(position)?.cardType.equals("visa", true)) {
            holder.binding.ivCardType.setImageResource(R.drawable.visablue)
        } else if (paymentList?.get(position)?.cardType.equals("maestro", true)) {
            holder.binding.ivCardType.setImageResource(R.drawable.maestro)

        } else {
            holder.binding.ivCardType.setImageResource(R.drawable.mastercard)

        }
        val htmlText =
            Html.fromHtml(paymentList?.get(position)?.cardType + "<br>" + paymentList?.get(position)?.cardNumber)

        holder.binding.tvSelectPaymentMethod.text = htmlText

        if (paymentList?.get(position)?.primaryCard == true) {
            holder.binding.textDefault.visibility = View.VISIBLE

        } else {
            holder.binding.textDefault.visibility = View.GONE

        }


        holder.binding.delete.setOnClickListener {

            paymentMethodCallback.paymentMethodCallback(
                position,
                com.conduent.nationalhighways.utils.common.Constants.DELETE_CARD
            )
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return paymentList?.size ?: 0
    }

    fun updateList(paymentList: MutableList<CardListResponseModel?>?) {
        this.paymentList = paymentList
        notifyDataSetChanged()

    }

    interface PaymentMethodCallback {
        fun paymentMethodCallback(position: Int, value: String)

    }
}