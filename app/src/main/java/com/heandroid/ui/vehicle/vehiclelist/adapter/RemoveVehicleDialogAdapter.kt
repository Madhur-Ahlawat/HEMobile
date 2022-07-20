package com.heandroid.ui.vehicle.vehiclelist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.ItemRemoveVehicleBinding
import com.heandroid.ui.vehicle.vehiclelist.dialog.RemoveVehicleDialog

class RemoveVehicleDialogAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<RemoveVehicleDialogAdapter.RemoveVehicleHolder>() {

    var vehicleList: List<VehicleResponse?> = mutableListOf()

    fun setList(list: ArrayList<VehicleResponse?>) {
        list.let {
            vehicleList = list
        }
    }

    class RemoveVehicleHolder(var binding: ItemRemoveVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView(vehicleItem: VehicleResponse) {
            binding.vehicleName.text = vehicleItem.plateInfo?.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoveVehicleHolder {
        val binding = ItemRemoveVehicleBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return RemoveVehicleHolder(binding)
    }

    override fun onBindViewHolder(holder: RemoveVehicleHolder, position: Int) {
        val vehicleItem = vehicleList[position]
        vehicleItem?.let {
            holder.setView(vehicleItem)
            holder.binding.vehicleCheckBox.setOnCheckedChangeListener { _, _ ->
                if(fragment is RemoveVehicleDialog){
                    fragment.addRemoveVehicleData(it.vehicleInfo?.rowId)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
