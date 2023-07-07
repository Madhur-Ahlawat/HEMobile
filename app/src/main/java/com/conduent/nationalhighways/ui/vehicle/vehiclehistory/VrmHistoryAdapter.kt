package com.conduent.nationalhighways.ui.vehicle.vehiclehistory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.VehiclelistlayoutBinding
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.dialog.ItemClickListener

class VrmHistoryAdapter(private val context: Context?, private val onItemClick: ItemClickListener) :
    RecyclerView.Adapter<VrmHistoryAdapter.VehicleListViewHolder>() {

    var vehicleList: List<VehicleResponse?> = mutableListOf()

    fun setList(list: ArrayList<VehicleResponse?>) {
        list.let {
                vehicleList = list
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleListViewHolder= VehicleListViewHolder(
        VehiclelistlayoutBinding.inflate(LayoutInflater.from(context),parent,false)
    )

    override fun onBindViewHolder(holder: VehicleListViewHolder, position: Int) {
        val plateNumber = vehicleList.get(position)?.plateInfo?.number
        holder.binding.vehiclePlateNumber.text = plateNumber
        holder.binding.cardView.setOnClickListener{
            onItemClick.onItemClick(vehicleList.get(position),position)

        }
        holder.binding.deleteVehicle.setOnClickListener{
            onItemClick.onItemDeleteClick(vehicleList.get(position),position)

        }
        holder.binding.updateVehicle.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.baseline_keyboard_arrow_right_24) });
        holder.binding.updateVehicle.setOnClickListener{
            onItemClick.onItemClick(vehicleList.get(position),position)
        }

    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }

    class VehicleListViewHolder(var binding: VehiclelistlayoutBinding):RecyclerView.ViewHolder(binding.root) {
    }
}
