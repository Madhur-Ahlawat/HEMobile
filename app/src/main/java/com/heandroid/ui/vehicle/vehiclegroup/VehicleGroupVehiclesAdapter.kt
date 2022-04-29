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
import com.heandroid.databinding.ItemVehicleGroupVehicleBinding
import com.heandroid.databinding.ItemVehicleNameFilterBinding
import com.heandroid.utils.common.Constants

class VehicleGroupVehiclesAdapter(
    private val fragment: Fragment,
    var vehicleList: List<VehicleResponse?>
) :
    RecyclerView.Adapter<VehicleGroupVehiclesAdapter.FilterVehicleNamesHolder>() {


    class FilterVehicleNamesHolder(var binding: ItemVehicleGroupVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView(vehicleGroupName: String) {
            binding.tvVehicleGroupName.text = vehicleGroupName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterVehicleNamesHolder {
        val binding = ItemVehicleGroupVehicleBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterVehicleNamesHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterVehicleNamesHolder, position: Int) {
        val vehicleItem = vehicleList[position]
        vehicleItem?.let {
            vehicleItem.plateInfo?.number?.let { holder.setView(it) }
            holder.binding.mainLayout.setOnClickListener {
                if (fragment is VehicleGroupFragment) {
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, vehicleItem)
                    }
                    fragment.findNavController().navigate(
                        R.id.action_vehicleGroupFragment_to_vehicleGroupVehicleDetailsFragment,
                        bundle
                    )
                } else if (fragment is VehicleGroupAddVehicleFragment) {

                }
            }
            holder.binding.cbVehicleGroup.setOnClickListener {
                makeButton(holder)
            }
        }
    }

    private fun makeButton(holder: FilterVehicleNamesHolder) {
        if (fragment is VehicleGroupFragment) {
            fragment.setSelectedVehicle(
                vehicleList[holder.absoluteAdapterPosition]
            )
        } else if (fragment is VehicleGroupAddVehicleFragment) {
            fragment.setSelectedVehicle(
                vehicleList[holder.absoluteAdapterPosition]
            )
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
