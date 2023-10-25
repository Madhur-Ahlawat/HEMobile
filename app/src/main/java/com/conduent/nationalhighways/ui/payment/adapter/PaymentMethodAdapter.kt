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
import com.conduent.nationalhighways.utils.common.Utils

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

        holder.binding.ivCardType.setImageResource(Utils.setCardImage(paymentList?.get(position)?.cardType?:""))

        if (paymentList?.get(position)?.emandateStatus == "PENDING" && paymentList?.get(position)?.bankAccount == true) {
            holder.binding.tvSelectPaymentMethod.text = context.getString(
                R.string.str_your_direct_debit_for,
                paymentList?.get(position)?.bankAccountNumber
            )
            holder.binding.ivCardType.setImageResource(R.drawable.directdebit)
            holder.binding.delete.visibility = View.GONE


        } else if (paymentList?.get(position)?.emandateStatus == "ACTIVE" && paymentList?.get(
                position
            )?.bankAccount == true
        ) {
            val htmlText =
                Html.fromHtml(
                    "Direct Debit" + "<br>" + paymentList?.get(
                        position
                    )?.bankAccountNumber?.let {
                        Utils.maskCardNumber(
                            it
                        )
                    })

            holder.binding.tvSelectPaymentMethod.text = htmlText
            holder.binding.ivCardType.setImageResource(R.drawable.directdebit)
            holder.binding.delete.visibility = View.VISIBLE


        } else {
            val htmlText =
                Html.fromHtml(
                    paymentList?.get(position)?.cardType + "<br>" + paymentList?.get(
                        position
                    )?.cardNumber?.let {
                        Utils.maskCardNumber(
                            it
                        )
                    })

            holder.binding.tvSelectPaymentMethod.text = htmlText
            holder.binding.delete.visibility = View.VISIBLE

        }



        if (paymentList?.get(position)?.primaryCard == true) {
            holder.binding.textDefault.visibility = View.VISIBLE
            holder.binding.textMakeDefault.visibility = View.GONE

        } else {
            holder.binding.textDefault.visibility = View.GONE
            holder.binding.textMakeDefault.visibility = View.VISIBLE


        }

        holder.binding.textMakeDefault.setOnClickListener {
            paymentMethodCallback.paymentMethodCallback(
                position,
                Constants.MAKE_DEFAULT
            )
        }

        holder.binding.delete.setOnClickListener {
            if (paymentList?.get(position)?.bankAccount == true && paymentList?.get(position)?.emandateStatus == "ACTIVE") {
                paymentMethodCallback.paymentMethodCallback(
                    position,
                    Constants.DIRECT_DEBIT
                )
            } else {
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
        this.paymentList = paymentList!!
        notifyDataSetChanged()

    }

    interface PaymentMethodCallback {
        fun paymentMethodCallback(position: Int, value: String)

    }
}