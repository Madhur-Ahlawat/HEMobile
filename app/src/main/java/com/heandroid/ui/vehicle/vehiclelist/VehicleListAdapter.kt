package com.heandroid.ui.vehicle.vehiclelist

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
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.model.vehicle.VehicleTitleAndSub
import com.heandroid.utils.DateUtils

class VehicleListAdapter(
    private val mContext: Context,
    private val onItemClick: ItemClickListener? = null
) : RecyclerView.Adapter<VehicleListAdapter.VrmHeaderViewHolder>() {

    private var vehicleList: List<VehicleResponse?> = mutableListOf()

    fun setList(list: ArrayList<VehicleResponse?>) {
        vehicleList = list
    }

    class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        val arrowImg: AppCompatImageView = itemView.findViewById(R.id.arrowImg)
        private val mRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
//        val cardViewTop: MaterialCardView = itemView.findViewById(R.id.cardview_top)

        fun setView(context: Context, vehicleItem: VehicleResponse) {
            vrmNoTxt.text = vehicleItem.plateInfo.number
//            arrowImg.animate().rotation(180f).start()

            if (vehicleItem.isExpanded) {
                mRecyclerView.visibility = View.VISIBLE
                arrowImg.animate().rotation(180f).start()
            } else {
                mRecyclerView.visibility = View.GONE
                arrowImg.animate().rotation(0f).start()

            }
            val mList = ArrayList<VehicleTitleAndSub>()
            mList.clear()

            for (i in 0..5) {
                when (i) {

                    0 -> {
                        val mem0 = VehicleTitleAndSub("Country", vehicleItem.plateInfo.country)
                        mList.add(mem0)
                    }
                    1 -> {
                        val mem1 = VehicleTitleAndSub("Make", vehicleItem.vehicleInfo.make)
                        mList.add(mem1)
                    }
                    2 -> {
                        val mem2 = VehicleTitleAndSub("Model", vehicleItem.vehicleInfo.model)
                        mList.add(mem2)
                    }
                    3 -> {
                        val mem2 = VehicleTitleAndSub("Colour", vehicleItem.vehicleInfo.color)
                        mList.add(mem2)
                    }
                    4 -> {
                        val mem2 =
                            VehicleTitleAndSub("Class", vehicleItem.vehicleInfo.vehicleClassDesc)
                        mList.add(mem2)
                    }
                    5 -> {
                        val mem2 = VehicleTitleAndSub(
                            "DateAdded",
                            DateUtils.convertDateFormat(vehicleItem.vehicleInfo.effectiveStartDate,1)
                        )
                        mList.add(mem2)
                    }
                }
            }
            val mAdapter = VehicleDetailsAdapter(context)
            mAdapter.setList(mList)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.adapter = mAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vrm_header_lyt, parent, false)
        return VrmHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: VrmHeaderViewHolder, position: Int) {
        vehicleList[position]?.let {
            holder.setView(mContext, it)
        }

        holder.arrowImg.setOnClickListener {
            vehicleList[position]?.let {
                onItemClick?.onItemClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
