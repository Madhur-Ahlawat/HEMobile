package com.conduent.nationalhighways.ui.viewcharges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.tollrates.TollRatesResp
import com.conduent.nationalhighways.databinding.AdapterViewChargesBinding

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
/*
        if (list?.get(position)?.videoRate!=0.0){
            holder.binding.title2.text="£"+String.format("%.2f", list?.get(position)?.videoRate)

        }else{
            holder.binding.title2.text=context?.getString(R.string.str_free)

        }
*/
        if (list?.get(position)?.etcRate!=0.0){
            holder.binding.title3.text="£"+String.format("%.2f", list?.get(position)?.videoRate)
            holder.binding.title4.text="£"+String.format("%.2f", list?.get(position)?.etcRate)

        }else{
            holder.binding.title3.text=context?.getString(R.string.str_free)
            holder.binding.title4.text=context?.getString(R.string.str_free)

        }
        if (position==3){
            holder.binding.view.visibility= View.GONE
        }else{
            holder.binding.view.visibility= View.VISIBLE

        }



        holder.binding.apply {
            data = list?.get(position)
            executePendingBindings()
        }
    }

    inner class VehicleViewHolder(val binding: AdapterViewChargesBinding) : RecyclerView.ViewHolder(binding.root){

    }




}
