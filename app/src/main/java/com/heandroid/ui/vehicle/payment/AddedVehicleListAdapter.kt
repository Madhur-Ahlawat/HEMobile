package com.heandroid.ui.vehicle.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener


class AddedVehicleListAdapter(var mListener: ItemClickListener) :
    RecyclerView.Adapter<AddedVehicleListAdapter.VehicleViewHolder>() {

    var vehicleList: List<VehicleResponse> = mutableListOf()

    fun setList(list: List<VehicleResponse>?) {
        if (list != null) {
            vehicleList = list
        }
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        private val vrmCountryTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_country)
        val deleteImg: AppCompatImageView = itemView.findViewById(R.id.delete_img)

        fun setView(vehicleItem: VehicleResponse) {
            vrmNoTxt.text = vehicleItem.plateInfo.number
            vrmCountryTxt.text = vehicleItem.plateInfo.country
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_vehicles_row, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicleItem = vehicleList[position]
        holder.setView(vehicleItem)
        holder.deleteImg.setOnClickListener {
            mListener.onItemDeleteClick(vehicleItem, holder.absoluteAdapterPosition)
        }
        holder.itemView.setOnClickListener {
            mListener.onItemClick(vehicleItem, holder.absoluteAdapterPosition)
        }

    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
