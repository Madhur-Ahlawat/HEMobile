package com.heandroid.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.databinding.AdapterCrossingHistoryBinding
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.utils.Utils.getDirection
import com.heandroid.utils.Utils.loadStatus


class CrossingHistoryAdapter(private val context: Context?,private var list: MutableList<CrossingHistoryItem?>?) : RecyclerView.Adapter<CrossingHistoryAdapter.HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder = HistoryViewHolder(AdapterCrossingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
       holder.bind(list?.get(position))
    }

    override fun getItemCount(): Int = list?.size?:0

    class HistoryViewHolder(var binding: AdapterCrossingHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data:CrossingHistoryItem?) {
            data?.run {
                binding.apply {
                    tvDate.text = "$transactionDate $exitTime"
                    tvVrm.text = plateNumber
                    tvDirection.text= getDirection(exitDirection)
                    loadStatus(prepaid,tvStatus)


                    cvMain.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putParcelable("data",data)
                        it.findNavController().navigate(R.id.action_crossingHistoryFragment_to_crossingHistoryMakePaymentFragment,bundle)
                    }
                }
            }
        }
    }
}