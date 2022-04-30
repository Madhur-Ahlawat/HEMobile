package com.heandroid.ui.bottomnav.account.payments.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.ItemVehicleNameFilterBinding

class FilterVehicleNamesAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<FilterVehicleNamesAdapter.FilterVehicleNamesHolder>() {

    var vehicleList: List<VehicleResponse?> = mutableListOf()
    var checkedPos = -1

    fun setList(list: ArrayList<VehicleResponse?>) {
        list.let {
            vehicleList = list
        }
    }

    class FilterVehicleNamesHolder(var binding: ItemVehicleNameFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView(vehicleItem: VehicleResponse) {
            binding.rbVehicleName.text = vehicleItem.plateInfo?.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterVehicleNamesHolder {
        val binding = ItemVehicleNameFilterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterVehicleNamesHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterVehicleNamesHolder, position: Int) {
        val vehicleItem = vehicleList[position]
        vehicleItem?.let {
            holder.setView(vehicleItem)
            holder.binding.rbVehicleName.isChecked = holder.absoluteAdapterPosition == checkedPos
            holder.binding.mainLayout.setOnClickListener {
                makeButton(holder)
            }
            holder.binding.rbVehicleName.setOnClickListener {
                makeButton(holder)
            }
        }
    }

    private fun makeButton(holder: FilterVehicleNamesHolder) {
        holder.binding.rbVehicleName.isChecked = true
        checkedPos = holder.absoluteAdapterPosition
        notifyDataSetChanged()
        (fragment as AccountPaymentHistoryFragment).setSelectedVehicleName(
            holder.binding.rbVehicleName.text.toString().trim()
        )
    }

    fun clearCheckedButtons() {
        checkedPos = -1
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
