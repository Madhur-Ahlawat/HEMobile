package com.heandroid.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.databinding.AdapterCrossingHistoryBinding
import com.heandroid.fragments.VehicleHistoryCrossingHistoryFragment
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.utils.Utils
import com.heandroid.utils.Utils.getDirection
import com.heandroid.utils.Utils.loadStatus


private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CrossingHistoryItem>() {
    override fun areItemsTheSame(oldItem: CrossingHistoryItem, newItem: CrossingHistoryItem): Boolean =  oldItem.transactionNumber == newItem.transactionNumber
    override fun areContentsTheSame(oldItem: CrossingHistoryItem, newItem: CrossingHistoryItem): Boolean = oldItem==newItem
}

class CrossingHistoryAdapter(val myFragment : Fragment) : PagingDataAdapter<CrossingHistoryItem,RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =HistoryViewHolder(AdapterCrossingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is HistoryViewHolder) {
            holder.bind(getItem(position))
            holder.binding.cvMain.setOnClickListener{
                val bundle = Bundle()
                bundle.putParcelable("data",getItem(position))
                if(myFragment is VehicleHistoryCrossingHistoryFragment)
                    it.findNavController().navigate(R.id.action_vehicleHistoryCrossingHistoryFragment_to_crossingHistoryMakePaymentFragment2,bundle)
                    else
                    it.findNavController().navigate(R.id.action_crossingHistoryFragment_to_crossingHistoryMakePaymentFragment,bundle)
            }
        }
    }

    class HistoryViewHolder(var binding: AdapterCrossingHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data:CrossingHistoryItem?) {
            data?.run {
                binding.apply {
                    tvDate.text = "$transactionDate $exitTime"
                    tvVrm.text = plateNumber
                    tvDirection.text= getDirection(exitDirection)
                    loadStatus(prepaid,tvStatus)
                }
            }
        }
    }
}