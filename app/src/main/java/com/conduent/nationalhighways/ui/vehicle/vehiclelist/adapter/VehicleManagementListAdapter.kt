package com.conduent.nationalhighways.ui.vehicle.vehiclelist.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.AdapterVehicleManagementListBinding
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Logg

class VehicleManagementListAdapter(private val mContext: Context):  RecyclerView.Adapter<VehicleManagementListAdapter.VrmNameViewHolder>(),
    View.OnClickListener {

    private lateinit var vehicleList: MutableList<VehicleResponse?>
    private var rowItem : VehicleResponse? = null

    fun setList(mList: ArrayList<VehicleResponse?>) {
        vehicleList = mList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmNameViewHolder {
        val binding = AdapterVehicleManagementListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VrmNameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VrmNameViewHolder, position: Int) {
        rowItem = vehicleList[position]
        rowItem?.let {
            holder.setView(rowItem!!)
            holder.binding.vrmParent.setOnClickListener{
                val pos: Int = holder.bindingAdapterPosition
                val bundle = Bundle()
                bundle.putParcelable(Constants.VEHICLE_ROW_ITEM, vehicleList[pos])
                Logg.logging("Testing"," vehicleList[pos] ${vehicleList[pos]}  ")
                it.findNavController().navigate(R.id.action_vehicleListFragment_to_vehicleManagementDetailFragment, bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }

    class VrmNameViewHolder(var binding: AdapterVehicleManagementListBinding): RecyclerView.ViewHolder(binding.root) {
        fun setView(rowItem: VehicleResponse) {
            binding.vrmTitle.text = rowItem.plateInfo?.number
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.vrmParent -> {
       }

        }
    }

}