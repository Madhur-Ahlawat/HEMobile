package com.conduent.nationalhighways.ui.auth.adapter

import android.app.Activity
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.databinding.ItemPaymentMethodBinding
import com.conduent.nationalhighways.utils.common.Utils


class SuspendPaymentMethodAdapter(
    private var context: Activity,
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
        val model = list?.get(position)
        var htmlText: Spanned? = null

        if (model?.bankAccount == false) {
            holder.binding.ivCardType.setImageResource(
                Utils.setCardImage(
                    model.cardType
                )
            )
            htmlText =
                Html.fromHtml(model.cardType + "<br>" + model.cardNumber.let {
                    Utils.setStarmaskcardnumber(
                        context,
                        it
                    )
                }, Html.FROM_HTML_MODE_COMPACT)
        } else {
            holder.binding.ivCardType.setImageResource(R.drawable.directdebit)
            htmlText =
                Html.fromHtml(
                    "Direct Debit" + "<br>" + model?.bankAccountNumber?.let {
                        Utils.maskCardNumber(
                            it
                        )
                    })

        }


        holder.binding.tvSelectPaymentMethod.text = htmlText

        if (model?.bankAccount == false) {
            holder.binding.radioButtonPaymentMethod.isChecked =
                model.primaryCard == true
            model.isSelected = true
        }





        holder.binding.layout.setOnClickListener {
            pos = position
            if (list?.get(pos)?.isSelected == true) {
                list?.get(pos)?.isSelected = false
                holder.binding.radioButtonPaymentMethod.isChecked = false

            } else {
                list?.get(pos)?.isSelected = true
                holder.binding.radioButtonPaymentMethod.isChecked = true


            }
            notifyDataSetChanged()
            paymentMethod.paymentMethodCallback(pos)
        }
        holder.binding.radioButtonPaymentMethod.setOnClickListener {
            pos = position
            if (list?.get(pos)?.isSelected == true) {
                list?.get(pos)?.isSelected = false
                holder.binding.radioButtonPaymentMethod.isChecked = false

            } else {
                list?.get(pos)?.isSelected = true
                holder.binding.radioButtonPaymentMethod.isChecked = true


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
        this.navFlow = navFlow
        notifyDataSetChanged()

    }

    class SuspendedViewHolder(val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface PaymentMethodSelectCallBack {
        fun paymentMethodCallback(position: Int)
    }

}