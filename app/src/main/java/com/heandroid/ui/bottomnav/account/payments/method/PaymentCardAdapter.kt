package com.heandroid.ui.bottomnav.account.payments.method

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.heandroid.R
import com.heandroid.data.model.payment.CardListResponseModel
import com.heandroid.databinding.AdapterPaymentCardBinding
import java.util.logging.Handler
import kotlin.math.abs

class PaymentCardAdapter(private val context: Context?,var list : MutableList<CardListResponseModel?>?,var listner : (Boolean?,Int?,CardListResponseModel?) -> Unit) : RecyclerView.Adapter<PaymentCardAdapter.MyViewHolder>() {

    override fun getItemCount(): Int = list?.size?:0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentCardAdapter.MyViewHolder = MyViewHolder(AdapterPaymentCardBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun onBindViewHolder(holder: PaymentCardAdapter.MyViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    inner class MyViewHolder(var binding: AdapterPaymentCardBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
           binding.rbDefaultMethod.setOnClickListener(this@MyViewHolder)
        }

        fun bind(data: CardListResponseModel?) {
            data?.run {
                binding.apply {
                    rbDefaultMethod.isChecked= check
                    val spannableString = if(bankAccount!!) SpannableString(bankAccountType+"\n"+ bankAccountNumber) else SpannableString(cardType+"\n"+ cardNumber)
                    spannableString?.setSpan( ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.txt_disable)), spannableString.length-(cardNumber?.length?:0), spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    rbDefaultMethod.text = spannableString
                }
            }
        }

        override fun onClick(v: View?) {
            for(i in list?.indices!!) { if(absoluteAdapterPosition!=i) list?.get(i)?.check=false }
            list?.get(absoluteAdapterPosition)?.check=!(list?.get(absoluteAdapterPosition)?.check?:false)
            listner.invoke(list?.get(absoluteAdapterPosition)?.check,absoluteAdapterPosition, list?.get(absoluteAdapterPosition))
            notifyItemRangeChanged(0,list?.size?:0)

    }}
}