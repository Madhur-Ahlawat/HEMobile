package com.heandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.databinding.AdapterCrossingHistoryBinding
import kotlinx.android.synthetic.main.activity_profile.*

class CrossingHistoryAdapter(private val context: Context,
                             private val list: MutableList<Any?>?) : RecyclerView.Adapter<CrossingHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrossingHistoryAdapter.HistoryViewHolder =
       HistoryViewHolder(AdapterCrossingHistoryBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun onBindViewHolder(holder: CrossingHistoryAdapter.HistoryViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    override fun getItemCount(): Int = 5

    inner class HistoryViewHolder(var binding: AdapterCrossingHistoryBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(data: Any?) {
            binding.apply {
                cvMain.setOnClickListener(this@HistoryViewHolder)
                if(absoluteAdapterPosition==1){
                    tvPaymentStatus.text=context.getString(R.string.unpaid)
                    tvPaymentStatus.setTextColor(ContextCompat.getColor(context, R.color.color_10403C))
                    tvPaymentStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.FCD6C3))

                }else if(absoluteAdapterPosition==2) {
                    tvPaymentStatus.text=context.getString(R.string.refund)
                    tvPaymentStatus.setTextColor(ContextCompat.getColor(context, R.color.color_594D00))
                    tvPaymentStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.FFF7BF))
                }
            }
        }

        override fun onClick(v: View?) {
            when(v?.id){
                R.id.cvMain ->{
                    v.findNavController().navigate(R.id.action_crossingHistoryFragment_to_crossingHistoryMakePaymentFragment)
                }
            }
        }
    }
}