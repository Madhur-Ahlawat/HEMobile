package com.heandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R

import com.heandroid.model.ViewChargesResponse

class ViewChargesListAdapter(private val mContext: Context) : RecyclerView.Adapter<ViewChargesListAdapter.VehicleViewHolder>() {

    var vehicleList: List<ViewChargesResponse> = mutableListOf()

    fun setList(list: List<ViewChargesResponse>?) {
        if (list != null) {
            vehicleList = list
        }
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title1: TextView = itemView.findViewById(R.id.title1)
        private val title2: TextView = itemView.findViewById(R.id.title2)
        private val title3: TextView = itemView.findViewById(R.id.title3)
        private val title4: TextView = itemView.findViewById(R.id.title4)

        fun setView(context : Context ,viewCharges: ViewChargesResponse) {

            title1.text = viewCharges.mTypeOfVehicle
            title2.text = viewCharges.mOneOfPayment
            title3.text = viewCharges.mPayGService
            title4.text = viewCharges.mPrePayService
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_view_charges, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {

        val vehicleItem = vehicleList[position]
        holder.setView(mContext , vehicleItem)
    }

    override fun getItemCount(): Int {
        return if (vehicleList == null) {
            0
        } else {
            vehicleList?.size
        }
    }
}
