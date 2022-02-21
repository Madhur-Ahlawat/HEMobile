package com.heandroid.ui.vehicle.vehiclelist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.VehicleResponse

class VehicleListAdapter(private val mContext: Context) : RecyclerView.Adapter<VehicleListAdapter.VehicleViewHolder>() {

    var vehicleList: List<VehicleResponse> = mutableListOf()

    fun setList(list: List<VehicleResponse>?) {
        if (list != null) {
            vehicleList = list
        }
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvOne: TextView = itemView.findViewById(R.id.tv_one)
        private val tvTwo: TextView = itemView.findViewById(R.id.tv_two)
        private val tvClass: TextView = itemView.findViewById(R.id.tv_class)
        private val tvModel: TextView = itemView.findViewById(R.id.tv_model)
        private val tvDateAdded: TextView = itemView.findViewById(R.id.tv_date_added)

        fun setView(context : Context ,vehicleItem: VehicleResponse) {

            tvOne.text = vehicleItem.plateInfo.number
            tvTwo.text = vehicleItem.plateInfo.country
            tvClass.text = context.getString(R.string.txt_vehicle_class, "B")
            tvModel.text = context.getString(R.string.txt_vehicle_model, "2008 FELINE MISTRAL BLU HDI SS")
            tvDateAdded.text = context.getString(R.string.txt_vehicle_date_added, "11/06/2021")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_vehicle_item, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {

        val vehicleItem = vehicleList[position]
        holder.setView(mContext , vehicleItem)
    }

    override fun getItemCount(): Int {
        return if (vehicleList == null) {
            0
        } else {
            vehicleList?.size
        }
    }
}
