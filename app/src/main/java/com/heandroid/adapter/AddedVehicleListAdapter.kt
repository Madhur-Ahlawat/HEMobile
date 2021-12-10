package com.heandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.VehicleDetailsModel

import kotlinx.android.synthetic.main.adapter_vehicles_row.view.*

class AddedVehicleListAdapter(private val mContext: Context, var mListener: ItemClickListener) :
    RecyclerView.Adapter<AddedVehicleListAdapter.VehicleViewHolder>() {

    var vehicleList: List<VehicleDetailsModel> = mutableListOf()

    fun setList(list: List<VehicleDetailsModel>?) {
        if (list != null) {

            vehicleList = list
        }
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        private val vrmCountryTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_country)
        private val deleteImg: AppCompatImageView = itemView.findViewById(R.id.delete_img)

        fun setView(context: Context, vehicleItem: VehicleDetailsModel) {

            vrmNoTxt.text = vehicleItem.vrmNo
            vrmCountryTxt.text = vehicleItem.vrmCountry
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_vehicles_row, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {

        val vehicleItem = vehicleList[position]
        holder.setView(mContext, vehicleItem)
        holder.itemView.delete_img.setOnClickListener {
            mListener?.onItemDeleteClick(vehicleItem, position)
        }
        holder.itemView.vrm_title.setOnClickListener {
            mListener?.onItemClick(vehicleItem,position)
        }
    }

    override fun getItemCount(): Int {
        return if (vehicleList == null) {
            0
        } else {
            vehicleList?.size
        }
    }
}
