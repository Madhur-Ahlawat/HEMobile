package com.conduent.nationalhighways.ui.checkpaidcrossings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.checkpaidcrossings.UsedChargesModel
import com.conduent.nationalhighways.databinding.AdapterUsedChargesBinding

class UsedChargesAdapter(
    private var list: MutableList<UsedChargesModel?>?
) : RecyclerView.Adapter<UsedChargesAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            AdapterUsedChargesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list?.get(position))

    }

    override fun getItemCount(): Int = list?.size ?: 0

    class HistoryViewHolder(var binding: AdapterUsedChargesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: UsedChargesModel?) {
            data?.run {
                binding.vrmNo.text = vrm

            }
        }
    }
}