package com.conduent.nationalhighways.ui.account.creation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.VehiclelistlayoutBinding
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils

class VehicleListAdapter(
    private val context: Context, private val list: MutableList<NewVehicleInfoDetails>,
    private val vehicleCallback: VehicleListCallBack, private val showRemoveButton: Boolean = true
) :
    RecyclerView.Adapter<VehicleListAdapter.VehicleListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleListViewHolder = VehicleListViewHolder(
        VehiclelistlayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    )

    override fun onBindViewHolder(holder: VehicleListViewHolder, position: Int) {
        val plateNumber = list[position].plateNumber
        val isDblaAvailable = list[position].isDblaAvailable
        holder.binding.vehiclePlateNumber.text = plateNumber
        holder.binding.vehiclePlateNumber.contentDescription =
            Utils.accessibilityForNumbers(plateNumber ?: "")
        holder.binding.deleteVehicle.setOnClickListener {
            vehicleCallback.vehicleListCallBack(
                position,
                Constants.REMOVE_VEHICLE,
                plateNumber,
                isDblaAvailable
            )

        }
        holder.binding.updateVehicle.setOnClickListener {
            vehicleCallback.vehicleListCallBack(
                position,
                Constants.EDIT_VEHICLE,
                plateNumber,
                isDblaAvailable
            )
        }
        if (showRemoveButton.not()) {
            holder.binding.deleteVehicle.visibility = View.INVISIBLE
            holder.binding.txtPlateNumber.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VehicleListViewHolder(var binding: VehiclelistlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface VehicleListCallBack {
        fun vehicleListCallBack(
            position: Int,
            value: String,
            plateNumber: String?,
            isDblaAvailable: Boolean?
        )
    }


}