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
import com.heandroid.databinding.NominatedContactsAdapterBinding
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible

class NominatedContactsAdapter(
    private val mContext: Context,
) : RecyclerView.Adapter<NominatedContactsAdapter.VrmHeaderViewHolder>() {

    private var secAccountList: List<SecondaryAccountData?> = mutableListOf()

    fun setList(list: ArrayList<SecondaryAccountData?>) {
        secAccountList = list
    }

    class VrmHeaderViewHolder(var binding: NominatedContactsAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setView(context: Context, contact: SecondaryAccountData) {

            binding.nameTitle.text = "${contact.firstName}${contact.lastName}"
            binding.emailIdStr.text = contact.emailAddress
            binding.mobileNumberStr.text = contact.phoneNumber
            if (contact.accountStatus.equals("INITIATED", true))
                binding.statusTitleStr.text = "Pending"
            else
                binding.statusTitleStr.gone()

            binding.rightsStr.text = "Amend Account, Vehicle Data"

            if (contact.isExpanded) {
                binding.arrowImg.animate().rotation(180f).start()
                binding.bottomDetailsLyt.visible()
            } else {
                binding.arrowImg.animate().rotation(0f).start()
                binding.bottomDetailsLyt.gone()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VrmHeaderViewHolder {
        return VrmHeaderViewHolder(
            NominatedContactsAdapterBinding.inflate(
                LayoutInflater.from(mContext), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VrmHeaderViewHolder, position: Int) {
        secAccountList[position]?.let {
            holder.setView(mContext, it)

        }

        holder.binding.arrowTitleLyt.setOnClickListener {
            val accList = secAccountList[position]
            accList?.isExpanded = !accList!!.isExpanded
            secAccountList[position]?.isExpanded = accList.isExpanded
            notifyItemChanged(position)
        }

    }

    override fun getItemCount(): Int {
        return secAccountList.size
    }
}
