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
import kotlinx.android.synthetic.main.vrm_header_lyt.view.*

class VrmHistoryHeaderAdapter(
    private val mContext: Context) :
    RecyclerView.Adapter<VrmHistoryHeaderAdapter.VrmHeaderViewHolder>() {

    var vehicleList: List<VehicleTitleAndSub> = mutableListOf()

    fun setList(list: List<VehicleTitleAndSub>?) {
        if (list != null) {

            vehicleList = list
        }
    }

    class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val TAG = "VrmHeaderAdapter"

        private val vrmTitleTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_number)
        private val mRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)

        fun setView(context: Context, vehicleItem: VehicleTitleAndSub) {

            vrmNoTxt.text = "${vehicleItem.type}"
            vrmTitleTxt.text = "${vehicleItem.title}"

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vrm_history_header_adapter, parent, false)
        return VrmHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: VrmHeaderViewHolder, position: Int) {

        val vehicleItem = vehicleList[position]
        holder.setView(mContext, vehicleItem)

    }

    override fun getItemCount(): Int {
        return vehicleList.size

    }
}
