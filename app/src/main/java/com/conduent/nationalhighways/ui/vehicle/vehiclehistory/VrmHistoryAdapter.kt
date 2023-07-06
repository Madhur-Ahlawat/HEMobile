package com.conduent.nationalhighways.ui.vehicle.vehiclehistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.VehiclelistlayoutBinding
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.dialog.ItemClickListener
import com.conduent.nationalhighways.utils.common.Constants

class VrmHistoryAdapter(private val context: Context?, private val onItemClick: ItemClickListener) :
    RecyclerView.Adapter<VrmHistoryAdapter.VehicleListViewHolder>() {

    var vehicleList: List<VehicleResponse?> = mutableListOf()

    fun setList(list: ArrayList<VehicleResponse?>) {
        list.let {
                vehicleList = list
        }
    }

    /*class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vehiclePlateNumber)
//        private val vrmCountry: AppCompatTextView = itemView.findViewById(R.id.vrm_country)

        fun setView(vehicleItem: VehicleResponse) {
            vrmNoTxt.text = vehicleItem.plateInfo?.number
//            vrmCountry.text = vehicleItem.plateInfo?.country
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehiclelistlayout, parent, false)
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

        holder.deleteVehicle.setOnClickListener{
            vehicleCallback.vehicleListCallBack(position,
                Constants.REMOVE_VEHICLE,plateNumber,isDblaAvailable)

        }
        holder.binding.updateVehicle.setOnClickListener{
            vehicleCallback.vehicleListCallBack(position,
                Constants.EDIT_VEHICLE,plateNumber,isDblaAvailable)
        }
    }*/


    /*override fun getItemCount(): Int {
        return vehicleList.size
    }*/

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
        holder.binding.updateVehicle.setOnClickListener{

        }

    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }

    class VehicleListViewHolder(var binding: VehiclelistlayoutBinding):RecyclerView.ViewHolder(binding.root) {
    }
}
