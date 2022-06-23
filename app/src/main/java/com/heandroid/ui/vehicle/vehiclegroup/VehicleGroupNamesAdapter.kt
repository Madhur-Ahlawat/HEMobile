package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleGroupResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.ItemVehicleGroupNameBinding
import com.heandroid.databinding.ItemVehicleNameFilterBinding
import com.heandroid.utils.common.Constants

class VehicleGroupNamesAdapter(
    private val fragment: Fragment,
    var vehicleList: List<VehicleGroupResponse?>
) :
    RecyclerView.Adapter<VehicleGroupNamesAdapter.FilterVehicleNamesHolder>() {

    var checkedGroups: ArrayList<String> = ArrayList()

    class FilterVehicleNamesHolder(var binding: ItemVehicleGroupNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView(vehicleGroupName: String) {
            binding.tvVehicleGroupName.text = vehicleGroupName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterVehicleNamesHolder {
        val binding = ItemVehicleGroupNameBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterVehicleNamesHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterVehicleNamesHolder, position: Int) {
        val vehicleItem = vehicleList[position]
        vehicleItem?.let { item ->
            if (item.groupName.equals(
                    fragment.requireContext().getString(R.string.unallocated_vehicle), true
                ) && item.groupId.isEmpty()
            ) {
                holder.binding.cbVehicleGroup.isEnabled = false
            }
            holder.setView(vehicleItem.groupName)
            holder.binding.cbVehicleGroup.isChecked = checkedGroups.contains(vehicleItem.groupName)
            holder.binding.mainLayout.setOnClickListener {
                if (fragment is VehicleGroupMngmtFragment) {
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, vehicleItem)
                    }
                    fragment.findNavController().navigate(
                        R.id.action_vehicleGroupMngmtFragment_to_vehicleGroupFragment,
                        bundle
                    )
                }
            }
            holder.binding.cbVehicleGroup.setOnClickListener {
                makeButton(holder, item.groupId)
            }
            holder.binding.vehicleGroupLayout.setOnClickListener {
                if (holder.binding.cbVehicleGroup.isEnabled) {
                    holder.binding.cbVehicleGroup.isChecked =
                        !holder.binding.cbVehicleGroup.isChecked
                    makeButton(holder, item.groupId)
                }
            }
        }
    }

    private fun makeButton(holder: FilterVehicleNamesHolder, vehicleItem: String) {
        if (!checkedGroups.contains(vehicleItem)) {
            checkedGroups.add(vehicleItem)
        } else {
            checkedGroups.remove(vehicleItem)
        }
        (fragment as VehicleGroupMngmtFragment).setSelectedVehicleGroupId(
            vehicleList[holder.absoluteAdapterPosition]
        )
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
