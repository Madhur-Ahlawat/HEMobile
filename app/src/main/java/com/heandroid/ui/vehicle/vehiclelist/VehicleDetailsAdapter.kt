package com.heandroid.ui.vehicle.vehiclelist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleTitleAndSub

class VehicleDetailsAdapter(private val mContext: Context) :
    RecyclerView.Adapter<VehicleDetailsAdapter.VehicleViewHolder>() {

    var vehicleList: List<VehicleTitleAndSub> = mutableListOf()

    fun setList(list: List<VehicleTitleAndSub>?) {
        if (list != null) {
            vehicleList = list
        }
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_detail_title)
        private val vrmCountryTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_detail_txt)

        fun setView(context: Context, vehicleItem: VehicleTitleAndSub) {


            vrmNoTxt.text = vehicleItem.title

            vrmCountryTxt.text = vehicleItem.type
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_vehicle_details_row, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicleItem = vehicleList[position]
        holder.setView(mContext, vehicleItem)
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
