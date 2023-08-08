package com.conduent.nationalhighways.ui.auth.adapter

import android.R.attr.text
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.databinding.ItemPaymentMethodBinding
import com.conduent.nationalhighways.utils.common.Utils


class SuspendPaymentMethodAdapter(
    private var context: Context,
    var list: MutableList<CardListResponseModel?>?,
    private val paymentMethod: paymentMethodSelectCallBack
) :
    RecyclerView.Adapter<SuspendPaymentMethodAdapter.SuspendedViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuspendedViewHolder = SuspendedViewHolder(
        ItemPaymentMethodBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
    )


    override fun onBindViewHolder(
        holder: SuspendPaymentMethodAdapter.SuspendedViewHolder,
        position: Int
    ) {
        if (list?.get(position)?.cardType.equals("visa", true)) {
            holder.binding.ivCardType.setImageResource(R.drawable.visablue)
        } else if (list?.get(position)?.cardType.equals("maestro", true)) {
            holder.binding.ivCardType.setImageResource(R.drawable.maestro)

        } else {
            holder.binding.ivCardType.setImageResource(R.drawable.mastercard)

        }
        val htmlText = Html.fromHtml(list?.get(position)?.cardType+"<br>"+ list?.get(position)?.cardNumber?.let {
            Utils.maskCardNumber(
                it
            )
        })

        holder.binding.tvSelectPaymentMethod.text = htmlText


        holder.binding.layout.setOnClickListener {
            if (list?.get(position)?.isSelected == true){
                list?.get(position)?.isSelected=false
                holder.binding.radioButtonPaymentMethod.isChecked=false

            }else{
                list?.get(position)?.isSelected=true
                holder.binding.radioButtonPaymentMethod.isChecked=true


            }

            paymentMethod.paymentMethodCallback(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun updateList(paymentList: MutableList<CardListResponseModel?>?) {
        this.list = paymentList
        notifyDataSetChanged()

    }

    class SuspendedViewHolder(val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface paymentMethodSelectCallBack {
        fun paymentMethodCallback(position: Int)
    }

}