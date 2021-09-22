package com.heandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.model.PaymentModel

class PaymentHistoryAdapter (private val mContext: Context): RecyclerView.Adapter<PaymentHistoryAdapter.PaymentViewHolder>() {

    var paymentList : List<PaymentModel> = mutableListOf()
    fun setList(list: List<PaymentModel>)
    {
        if(list!=null)
        {
            paymentList = list
        }
    }
    class PaymentViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvTransID: TextView = itemView.findViewById(R.id.tv_transaction_id)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)

       fun setView(mContext: Context, paymentModel: PaymentModel)
       {
           tvDate.text = paymentModel.transactionDate
           tvTransID.text= paymentModel.vehicleTypeId
           tvAmount.text = paymentModel.amount.toString()
       }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_payment_item, parent, false)
        return PaymentHistoryAdapter.PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        if(position>0)
        {
            val paymentModel = paymentList[position-1]
            holder.setView(mContext , paymentModel)
        }
        else
        {

        }

    }

    override fun getItemCount(): Int{
        return if (paymentList == null) {
            0
        } else {
            paymentList?.size+1
        }
    }
}