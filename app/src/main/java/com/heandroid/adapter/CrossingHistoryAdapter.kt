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
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem

class CrossingHistoryAdapter(private val context: Context) :
    RecyclerView.Adapter<CrossingHistoryAdapter.HistoryViewHolder>() {

    private var list = mutableListOf<CrossingHistoryItem>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrossingHistoryAdapter.HistoryViewHolder =
        HistoryViewHolder(
            AdapterCrossingHistoryBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CrossingHistoryAdapter.HistoryViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    fun setListData(itemList: List<CrossingHistoryItem>) {
        itemList?.let {
            list = itemList as MutableList<CrossingHistoryItem>
            notifyDataSetChanged()
        }
    }

    fun addListData(itemList: List<CrossingHistoryItem>) {
        itemList?.let {
            list.addAll(itemList) as MutableList<CrossingHistoryItem>
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class HistoryViewHolder(var binding: AdapterCrossingHistoryBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(data:CrossingHistoryItem) {

            binding.apply {
                cvMain.setOnClickListener(this@HistoryViewHolder)
                tvStatus.text=data.prepaid
                tvDate.text = data.transactionDate + " " +data.exitTime
                when(data.entryDirection)
                {
                    "N"->{
                         tvDirection.text = "NORTHBOUND"}
                    "s"->{
                        tvDirection.text = "SOUTHBOUND"
                    }
                }

                tvVrm.text = data.plateNumber
//                if (absoluteAdapterPosition == 1) {
//                    tvStatus.text = context.getString(R.string.unpaid)
//                    tvStatus.setTextColor(
//                        ContextCompat.getColor(
//                            context,
//                            R.color.color_10403C
//                        )
//                    )
//                    tvStatus.setBackgroundColor(
//                        ContextCompat.getColor(
//                            context,
//                            R.color.FCD6C3
//                        )
//                    )
//
//                } else if (absoluteAdapterPosition == 2) {
//                    tvStatus.text = context.getString(R.string.refund)
//                    tvStatus.setTextColor(
//                        ContextCompat.getColor(
//                            context,
//                            R.color.color_594D00
//                        )
//                    )
//                    tvStatus.setBackgroundColor(
//                        ContextCompat.getColor(
//                            context,
//                            R.color.FFF7BF
//                        )
//                    )
//                }
            }
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.cvMain -> {
                    v.findNavController()
                        .navigate(R.id.action_crossingHistoryFragment_to_crossingHistoryMakePaymentFragment)
                }
            }
        }
    }
}