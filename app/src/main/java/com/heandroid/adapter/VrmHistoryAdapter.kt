package com.heandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.heandroid.R
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.VehicleResponse
import com.heandroid.model.VehicleTitleAndSub
import com.heandroid.utils.Logg

class VrmHistoryAdapter(private val mContext: Context, private val onItemClick: ItemClickListener) :
    RecyclerView.Adapter<VrmHistoryAdapter.VrmHeaderViewHolder>() {

    var vehicleList: List<VehicleResponse> = mutableListOf()

    fun setList(list: List<VehicleResponse>?) {
        if (list != null) {

            vehicleList = list
        }
    }

    class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val TAG = "VrmHeaderAdapter"

        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        private val vrmCountry: AppCompatTextView = itemView.findViewById(R.id.vrm_country)

        fun setView(context: Context, vehicleItem: VehicleResponse) {

            vrmNoTxt.text = "${vehicleItem.plateInfo.number}"
            vrmCountry.text = "${vehicleItem.plateInfo.country}"


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vrm_history_lyt, parent, false)
        return VrmHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: VrmHeaderViewHolder, position: Int) {

        val vehicleItem = vehicleList[position]
        holder.setView(mContext, vehicleItem)

        holder.itemView.setOnClickListener {
            onItemClick.onItemClick(vehicleItem, position)
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
