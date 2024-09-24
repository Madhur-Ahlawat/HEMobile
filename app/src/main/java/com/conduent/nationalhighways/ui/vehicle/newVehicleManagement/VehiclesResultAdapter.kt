package com.conduent.nationalhighways.ui.vehicle.newVehicleManagement

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.VehiclelistlayoutBinding

class VehiclesResultAdapter(
    private val context: Context,
    private val list: ArrayList<NewVehicleInfoDetails>
) :
    RecyclerView.Adapter<VehiclesResultAdapter.VehicleListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleListViewHolder = VehicleListViewHolder(
        VehiclelistlayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    )

    override fun onBindViewHolder(holder: VehicleListViewHolder, position: Int) {
        val plateNumber = list.get(position).plateNumber
        val isDblaAvailable = list.get(position).isDblaAvailable
        holder.binding.vehiclePlateNumber.text = plateNumber

        holder.binding.deleteVehicle.visibility = View.INVISIBLE
        holder.binding.updateVehicle.visibility = View.INVISIBLE
        holder.binding.txtPlateNumber.visibility = View.VISIBLE

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VehicleListViewHolder(var binding: VehiclelistlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

}