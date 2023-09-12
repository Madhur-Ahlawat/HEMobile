package com.conduent.nationalhighways.ui.auth.adapter

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
    private val paymentMethod: PaymentMethodSelectCallBack,
    var navFlow: String
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
        holder: SuspendedViewHolder,
        position: Int
    ) {
        var pos: Int
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
        },Html.FROM_HTML_MODE_COMPACT)


            if (list?.get(position)?.bankAccount==false){
                holder.binding.radioButtonPaymentMethod.isChecked = list?.get(position)?.primaryCard==true
                list?.get(position)?.isSelected=true
            }



        holder.binding.tvSelectPaymentMethod.text = htmlText


        holder.binding.layout.setOnClickListener {
            pos=position
            if (list?.get(pos)?.isSelected == true){
                list?.get(pos)?.isSelected=false
                holder.binding.radioButtonPaymentMethod.isChecked=false

            }else{
                list?.get(pos)?.isSelected=true
                holder.binding.radioButtonPaymentMethod.isChecked=true


            }
            notifyDataSetChanged()
            paymentMethod.paymentMethodCallback(pos)
        }
        holder.binding.radioButtonPaymentMethod.setOnClickListener {
            pos=position
            if (list?.get(pos)?.isSelected == true){
                list?.get(pos)?.isSelected=false
                holder.binding.radioButtonPaymentMethod.isChecked=false

            }else{
                list?.get(pos)?.isSelected=true
                holder.binding.radioButtonPaymentMethod.isChecked=true


            }
            notifyDataSetChanged()
            paymentMethod.paymentMethodCallback(pos)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun updateList(paymentList: MutableList<CardListResponseModel?>?, navFlow: String) {
        this.list = paymentList
        this.navFlow=navFlow
        notifyDataSetChanged()

    }

    class SuspendedViewHolder(val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface PaymentMethodSelectCallBack {
        fun paymentMethodCallback(position: Int)
    }

}