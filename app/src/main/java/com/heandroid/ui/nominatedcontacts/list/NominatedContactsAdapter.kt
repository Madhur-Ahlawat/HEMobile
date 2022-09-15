package com.heandroid.ui.nominatedcontacts.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData
import com.heandroid.databinding.NominatedContactsAdapterBinding
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible

class NominatedContactsAdapter(
    private val mContext: Context,
    private var secAccountList: MutableList<SecondaryAccountData?>?,
    private val listener: NominatedContactListener
) : RecyclerView.Adapter<NominatedContactsAdapter.VrmHeaderViewHolder>() {


    class VrmHeaderViewHolder(var binding: NominatedContactsAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setView(context: Context, contact: SecondaryAccountData) {

            binding.nameTitle.text = "${contact.firstName}${contact.lastName}"
            binding.emailIdStr.text = contact.emailAddress
            binding.mobileNumberStr.text = contact.phoneNumber

            if (contact.accountStatus.equals("INITIATED", true) || contact.accountStatus.equals(
                    Constants.EXPIRED,
                    true
                )
            ) {
                if(contact.accountStatus.equals(Constants.EXPIRED))
                    binding.statusTitleStr.text = Constants.EXPIRED
                else
                    binding.statusTitleStr.text = "Pending"

                binding.removeBtn.text = context.getString(R.string.str_remove)
                binding.resendBtn.text = context.getString(R.string.str_resend)
            } else {
                binding.statusTitleStr.gone()
                binding.statusTitle.gone()
                binding.removeBtn.text = context.getString(R.string.str_edit)
                binding.resendBtn.text = context.getString(R.string.str_remove)

            }

            binding.rightsStr.text = contact.mPermissionLevel
            Logg.logging("NominatedContactsAdapter", "contact.isExpanded ${contact.isExpanded}")

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
        if (position == secAccountList?.size && secAccountList!!.isEmpty())
            return

        secAccountList?.get(position)?.let {
            holder.setView(mContext, it)
        }

        holder.binding.arrowTitleLyt.setOnClickListener {

            if (secAccountList!!.isEmpty() && secAccountList!!.size == position)
                return@setOnClickListener

            val accList = secAccountList?.get(position)
            accList?.isExpanded = (accList?.isExpanded == false)
            if (accList != null) {
                listener.onItemClick("open", accList, position, accList.isExpanded)
            }

            secAccountList?.get(position)?.isExpanded = accList?.isExpanded == true
            notifyItemChanged(position)

        }

        holder.binding.resendBtn.setOnClickListener {
            val accList = secAccountList?.get(position)
            accList?.let {
                listener.onItemClick(
                    holder.binding.resendBtn.text.toString(),
                    it,
                    position,
                    false
                )
            }


        }
        holder.binding.removeBtn.setOnClickListener {
            val accList = secAccountList?.get(position)

            accList?.let {
                listener.onItemClick(
                    holder.binding.removeBtn.text.toString(),
                    it,
                    position,
                    false
                )
            }


        }

    }

    override fun getItemCount(): Int {
        return secAccountList?.size ?: 0
    }
}
