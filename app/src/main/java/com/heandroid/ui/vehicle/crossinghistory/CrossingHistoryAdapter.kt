package com.heandroid.ui.vehicle.crossinghistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.databinding.AdapterCrossingHistoryBinding
import com.heandroid.ui.vehicle.vehiclehistory.VehicleHistoryCrossingHistoryFragment
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Utils.getDirection
import com.heandroid.utils.common.Utils.loadStatus

class CrossingHistoryAdapter(
    private val myFragment: Fragment?,
    private var list: MutableList<CrossingHistoryItem?>?
) : RecyclerView.Adapter<CrossingHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            AdapterCrossingHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list?.get(position))

        holder.binding.cvMain.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Constants.DATA, list?.get(position))
            if (myFragment is VehicleHistoryCrossingHistoryFragment)
                it.findNavController().navigate(
                    R.id.action_vehicleHistoryCrossingHistoryFragment_to_crossingHistoryMakePaymentFragment2,
                    bundle
                )
            else
                it.findNavController().navigate(
                    R.id.action_crossingHistoryFragment_to_crossingHistoryMakePaymentFragment,
                    bundle
                )
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    class HistoryViewHolder(var binding: AdapterCrossingHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CrossingHistoryItem?) {
            data?.run {
                binding.apply {
                    tvDate.text = "${DateUtils.convertDateFormat(transactionDate,0)} ${DateUtils.convertTimeFormat(exitTime,0)}"
                    tvVrm.text = plateNumber
                    tvDirection.text = getDirection(exitDirection)
                    loadStatus(prepaid, tvStatus)
                }
            }
        }
    }
}