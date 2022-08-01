package com.heandroid.ui.vehicle.vehiclegroup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.databinding.ItemVehicleGroupCrossingBinding
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.Utils.getDirection

class VehicleGroupCrossingHistoryAdapter(
    private var list: MutableList<CrossingHistoryItem?>?
) : RecyclerView.Adapter<VehicleGroupCrossingHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            ItemVehicleGroupCrossingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    override fun getItemCount(): Int = list?.size ?: 0

    class HistoryViewHolder(var binding: ItemVehicleGroupCrossingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CrossingHistoryItem?) {
            data?.run {
                binding.apply {
                    date.text = DateUtils.convertDateFormat(transactionDate,0)
                    time.text = exitTime?.let { DateUtils.convertTimeFormat(it,0) }
                    direction.text = getDirection(exitDirection)
                }
            }
        }
    }
}