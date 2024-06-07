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

        var valuePayasYouGo = ""
        var valuePrepay = ""
        holder.binding.data = list?.get(position)
        if (list?.get(position)?.etcRate != 0.0) {

            valuePrepay =
                context?.getString(
                    R.string.price, String.format(
                        "%.2f",
                        list?.get(position)?.etcRate
                    )
                ) ?: ""

            valuePayasYouGo = context?.getString(
                R.string.price, String.format(
                    "%.2f",
                    list?.get(position)?.videoRate
                )
            ) ?: ""
        } else {
            valuePrepay = context?.getString(R.string.str_free) ?: ""
            valuePayasYouGo = context?.getString(R.string.str_free) ?: ""
        }

        holder.binding.valuePayAsYouGo.text = valuePayasYouGo
        holder.binding.valuePrepay.text = valuePrepay
        if (position == (list.orEmpty().size - 1)) {
            holder.binding.view.visibility = View.GONE
        } else {
            holder.binding.view.visibility = View.VISIBLE
        }

        holder.binding.rootDataCl.contentDescription =
            context?.resources?.getString(R.string.type_of_vehicle) + "," + list?.get(position)?.vehicleTypeDesc +
                    "\n" + context?.resources?.getString(R.string.str_pay_g_service_) + "," + valuePayasYouGo +
                    "\n" + context?.resources?.getString(R.string.str_pre_pay_service) + "," + valuePrepay

        holder.binding.apply {
            executePendingBindings()
        }
    }

    inner class VehicleViewHolder(val binding: AdapterViewChargesBinding) :
        RecyclerView.ViewHolder(binding.root)


}
