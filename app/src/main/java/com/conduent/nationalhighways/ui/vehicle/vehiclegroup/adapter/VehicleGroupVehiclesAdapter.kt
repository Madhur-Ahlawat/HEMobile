package com.conduent.nationalhighways.ui.vehicle.vehiclegroup.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.ItemVehicleGroupVehicleBinding
import com.conduent.nationalhighways.ui.vehicle.vehiclegroup.VehicleGroupAddVehicleFragment
import com.conduent.nationalhighways.ui.vehicle.vehiclegroup.VehicleGroupFragment
import com.conduent.nationalhighways.utils.common.Constants

class VehicleGroupVehiclesAdapter(
    private val fragment: Fragment,
    var vehicleList: List<VehicleResponse?>
) :
    RecyclerView.Adapter<VehicleGroupVehiclesAdapter.FilterVehicleNamesHolder>() {
    private var checkedList = arrayListOf<String?>()


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
            holder.binding.cbVehicleGroup.isChecked = checkedList.contains(vehicleList[holder.absoluteAdapterPosition]?.plateInfo?.number)
            holder.binding.mainLayout.setOnClickListener {
                if (fragment is VehicleGroupFragment) {
                    fragment.vehicleGroup
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, vehicleItem)
                        putParcelable(Constants.VEHICLE_GROUP, fragment.vehicleGroup)
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
            holder.binding.vehicleGroupLayout.setOnClickListener {
                holder.binding.cbVehicleGroup.isChecked = !holder.binding.cbVehicleGroup.isChecked
                makeButton(holder)
            }
        }
    }

    private fun makeButton(holder: FilterVehicleNamesHolder) {
        if (checkedList.contains(vehicleList[holder.absoluteAdapterPosition]?.plateInfo?.number)){
            checkedList.remove(vehicleList[holder.absoluteAdapterPosition]?.plateInfo?.number)
        } else {
            checkedList.add(vehicleList[holder.absoluteAdapterPosition]?.plateInfo?.number)
        }

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
