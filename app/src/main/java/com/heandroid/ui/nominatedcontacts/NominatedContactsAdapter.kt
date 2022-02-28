package com.heandroid.ui.nominatedcontacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse

class NominatedContactsAdapter(
    private val mContext: Context,
) : RecyclerView.Adapter<NominatedContactsAdapter.VrmHeaderViewHolder>() {

    private var vehicleList: List<VehicleResponse?> = mutableListOf()

    fun setList(list: ArrayList<VehicleResponse?>) {
            vehicleList = list
    }

    class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        private val arrowImg: AppCompatImageView = itemView.findViewById(R.id.arrow_img)
        private val mRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
        val cardViewTop: MaterialCardView = itemView.findViewById(R.id.cardview_top)

        fun setView(context: Context, vehicleItem: VehicleResponse) {
            vrmNoTxt.text = "${vehicleItem.plateInfo.number}"
//            arrowImg.animate().rotation(180f).start()

            if (vehicleItem.isExpanded) {
                mRecyclerView.visibility = View.VISIBLE
                arrowImg.animate().rotation(180f).start()
            } else {
                mRecyclerView.visibility = View.GONE
                arrowImg.animate().rotation(0f).start()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nominated_contacts_adapter, parent, false)
        return VrmHeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: VrmHeaderViewHolder, position: Int) {
        vehicleList[position]?.let {
            holder.setView(mContext, it)
        }

        holder.cardViewTop.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}
