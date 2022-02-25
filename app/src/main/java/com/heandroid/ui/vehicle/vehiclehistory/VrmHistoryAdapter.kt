package com.heandroid.ui.vehicle.vehiclehistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener

class VrmHistoryAdapter(private val onItemClick: ItemClickListener) :
    RecyclerView.Adapter<VrmHistoryAdapter.VrmHeaderViewHolder>() {

    var vehicleList: List<VehicleResponse?> = mutableListOf()

    fun setList(list: ArrayList<VehicleResponse?>) {
        list.let {
            vehicleList = list
        }
    }

    class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        private val vrmCountry: AppCompatTextView = itemView.findViewById(R.id.vrm_country)

        fun setView(vehicleItem: VehicleResponse) {
            vrmNoTxt.text = vehicleItem.plateInfo.number
            vrmCountry.text = vehicleItem.plateInfo.country
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vrm_history_lyt, parent, false)
        return VrmHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: VrmHeaderViewHolder, position: Int) {
        val vehicleItem = vehicleList[position]
        if (vehicleItem != null) {
            holder.setView(vehicleItem)
        }
        holder.itemView.setOnClickListener {
            if (vehicleItem != null) {
                onItemClick.onItemClick(vehicleItem, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
