package com.conduent.nationalhighways.ui.vehicle.vehiclelist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleTitleAndSub
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.dialog.ItemClickListener
import com.conduent.nationalhighways.utils.DateUtils

class VehicleListAdapter(
    private val mContext: Context,
    private val onItemClick: ItemClickListener? = null,
    private var isBusinessAccount : Boolean = false
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

        fun setView(context: Context, vehicleItem: VehicleResponse?, isBusinessAccount: Boolean) {
            vrmNoTxt.text = vehicleItem?.plateInfo?.number
//            arrowImg.animate().rotation(180f).start()

            if (vehicleItem?.isExpanded == true) {
                mRecyclerView.visibility = View.VISIBLE
                arrowImg.animate().rotation(180f).start()
            } else {
                mRecyclerView.visibility = View.GONE
                arrowImg.animate().rotation(0f).start()

            }
            val mList = ArrayList<VehicleTitleAndSub>()
            mList.clear()

            for (i in 0..7) {
                when (i) {

                    0 -> {
                        val mem0 = VehicleTitleAndSub("Country", vehicleItem?.plateInfo?.country)
                        mList.add(mem0)
                    }

                    1 -> {
                        val mem1 = VehicleTitleAndSub("Make", vehicleItem?.vehicleInfo?.make)
                        mList.add(mem1)
                    }
                    2 -> {
                        val mem2 = VehicleTitleAndSub("Model", vehicleItem?.vehicleInfo?.model)
                        mList.add(mem2)
                    }
                    3 -> {
                        val mem2 = VehicleTitleAndSub("Colour", vehicleItem?.vehicleInfo?.color)
                        mList.add(mem2)
                    }
                    4 -> {
                        val mem2 =
                            VehicleTitleAndSub("Class", vehicleItem?.vehicleInfo?.vehicleClassDesc)
                        mList.add(mem2)
                    }
                    5 -> {
                        val mem2 = VehicleTitleAndSub("DateAdded", DateUtils.convertDateFormat(vehicleItem?.vehicleInfo?.effectiveStartDate,1))
                        mList.add(mem2)
                    }
                    6 -> {
                        val mem2 = VehicleTitleAndSub("Notes", vehicleItem?.plateInfo?.vehicleComments)
                        mList.add(mem2)
                    }
                    7 -> {
                        if (isBusinessAccount) {
                            val mem2 = VehicleTitleAndSub("Group name", vehicleItem?.vehicleInfo?.groupName)
                            mList.add(mem2)
                        }
                    }
                }
            }
            val mAdapter = VehicleDetailsAdapter(context)
            mAdapter.setList(mList)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            // mRecyclerView.setHasFixedSize(true)
            mRecyclerView.adapter = mAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vrm_header_lyt, parent, false)
        return VrmHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: VrmHeaderViewHolder, position: Int) {
        vehicleList[position]?.let {
            holder.setView(mContext, it, isBusinessAccount)
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
