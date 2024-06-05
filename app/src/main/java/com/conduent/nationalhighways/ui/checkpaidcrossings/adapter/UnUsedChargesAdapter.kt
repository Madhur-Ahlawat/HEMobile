package com.conduent.nationalhighways.ui.checkpaidcrossings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.checkpaidcrossings.UnUsedChargesModel
import com.conduent.nationalhighways.databinding.AdapterUnusedChargesBinding

class UnUsedChargesAdapter(
    private var list: MutableList<UnUsedChargesModel?>?, val listener: OnChangeClickListener
) : RecyclerView.Adapter<UnUsedChargesAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            AdapterUnusedChargesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list?.get(position))
        holder.binding.changeVehicle.setOnClickListener {
            list?.get(position)?.trip?.let { it1 -> listener.clickChange(it1) }
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    class HistoryViewHolder(var binding: AdapterUnusedChargesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: UnUsedChargesModel?) {
            binding.apply {
                tvTripCount.text = "${data?.trip}"
                vrmNo.text = data?.vrm
                tvExpiryDate.text = data?.expiryDate

            }
        }
    }
}