package com.heandroid.ui.bottomnav.account.payments.method

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.heandroid.R
import com.heandroid.data.model.payment.CardListResponseModel
import com.heandroid.databinding.AdapterPaymentCardBinding
import kotlin.math.abs

class PaymentCardAdapter(private val context: Context?,val list : MutableList<CardListResponseModel?>?,var listner : (Boolean,Int,CardListResponseModel) -> Unit) : RecyclerView.Adapter<PaymentCardAdapter.MyViewHolder>() {
    override fun getItemCount(): Int = list?.size?:0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentCardAdapter.MyViewHolder = MyViewHolder(AdapterPaymentCardBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun onBindViewHolder(holder: PaymentCardAdapter.MyViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    inner class MyViewHolder(var binding: AdapterPaymentCardBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        fun bind(data: CardListResponseModel?) {
            data?.run {
              binding.apply {
                  rbDefaultMethod.isChecked=check

                  val spannableString = SpannableString("$bankAccountType\n$bankAccountNumber")
                  spannableString.setSpan( ForegroundColorSpan(Color.LTGRAY), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                  rbDefaultMethod.text = spannableString
                  rbDefaultMethod.setOnClickListener(this@MyViewHolder)
              }
            }
        }

        override fun onClick(v: View?) {
            when(v?.id){
                R.id.rbDefaultMethod ->{
                    for(i in list?.indices!!){
                        if(absoluteAdapterPosition!=i)
                        list[i]?.check=false
                    }
                    list[absoluteAdapterPosition]?.check=true
                    listner.invoke( binding.rbDefaultMethod.isChecked,absoluteAdapterPosition, list[absoluteAdapterPosition]!!)
                }
            }
        }
    }
}