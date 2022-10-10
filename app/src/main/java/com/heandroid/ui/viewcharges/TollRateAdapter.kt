package com.heandroid.ui.viewcharges

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.tollrates.TollRatesResp
import com.heandroid.databinding.AdapterViewChargesBinding

class TollRateAdapter(private val context: Context?, var list: List<TollRatesResp?>?) :
    RecyclerView.Adapter<TollRateAdapter.VehicleViewHolder>() {

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder =
        VehicleViewHolder(
            AdapterViewChargesBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TollRateAdapter.VehicleViewHolder, position: Int) {
        holder.binding.apply {
            data = list?.get(position)
            executePendingBindings()
        }
    }

    inner class VehicleViewHolder(val binding: AdapterViewChargesBinding) :
        RecyclerView.ViewHolder(binding.root)


}
