package com.conduent.nationalhighways.ui.account.creation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.databinding.VehiclelistlayoutBinding

class VehicleListAdapter(private val context: Context,private val list: ArrayList<String>,private val vehicleCallback:VehicleListCallBack):
    RecyclerView.Adapter<VehicleListAdapter.VehicleListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleListViewHolder= VehicleListViewHolder(
        VehiclelistlayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        )

    override fun onBindViewHolder(holder: VehicleListAdapter.VehicleListViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VehicleListViewHolder(var binding:VehiclelistlayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    interface VehicleListCallBack{
        fun vehicleListCallBack(position: Int,value:String)
    }


}