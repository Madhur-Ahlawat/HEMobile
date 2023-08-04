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
import com.conduent.nationalhighways.utils.common.Constants

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

        }else if(paymentList?.get(position)?.cardType.equals(Constants.DIRECT_DEBIT, true)){
            holder.binding.ivCardType.setImageResource(R.drawable.directdebit)


        } else {
            holder.binding.ivCardType.setImageResource(R.drawable.mastercard)

        }

        if (paymentList?.get(position)?.cardType.equals(Constants.DIRECT_DEBIT, true)){
            holder.binding.tvSelectPaymentMethod.text = context.getString(R.string.str_your_direct_debit_for,paymentList?.get(position)?.cardNumber)

        }else{
            val htmlText =
                Html.fromHtml(paymentList?.get(position)?.cardType + "<br>" + paymentList?.get(position)?.cardNumber)

            holder.binding.tvSelectPaymentMethod.text = htmlText
        }



        if (paymentList?.get(position)?.primaryCard == true) {
            holder.binding.textDefault.visibility = View.VISIBLE
            holder.binding.textMakeDefault.visibility=View.GONE

        } else {
            holder.binding.textDefault.visibility = View.GONE
            holder.binding.textMakeDefault.visibility=View.VISIBLE


        }

        holder.binding.textMakeDefault.setOnClickListener{
            paymentMethodCallback.paymentMethodCallback(
                position,
                Constants.MAKE_DEFAULT
            )
        }

        holder.binding.delete.setOnClickListener {
            if (paymentList?.get(position)?.cardType.equals(Constants.DIRECT_DEBIT, true)){
                paymentMethodCallback.paymentMethodCallback(
                    position,
                    Constants.DIRECT_DEBIT
                )
            }else{
                paymentMethodCallback.paymentMethodCallback(
                    position,
                    Constants.DELETE_CARD
                )
            }


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