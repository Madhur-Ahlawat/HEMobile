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
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData
import com.heandroid.data.model.vehicle.VehicleResponse

class NominatedContactsAdapter(
    private val mContext: Context,
) : RecyclerView.Adapter<NominatedContactsAdapter.VrmHeaderViewHolder>() {

    private var secAccountList: List<SecondaryAccountData?> = mutableListOf()

    fun setList(list: ArrayList<SecondaryAccountData?>) {
            secAccountList = list
    }

    class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTxt: AppCompatTextView = itemView.findViewById(R.id.name_title)
        private val arrowImg: AppCompatImageView = itemView.findViewById(R.id.arrow_img)

        fun setView(context: Context, vehicleItem: SecondaryAccountData) {
//            arrowImg.animate().rotation(180f).start()

            if (vehicleItem.isExpanded) {
                arrowImg.animate().rotation(180f).start()
            } else {
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
        secAccountList[position]?.let {
            holder.setView(mContext, it)
        }

    }

    override fun getItemCount(): Int {
        return secAccountList.size
    }
}
