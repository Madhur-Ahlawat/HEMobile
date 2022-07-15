package com.heandroid.ui.nominatedcontacts.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData
import com.heandroid.databinding.NominatedContactsAdapterBinding
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

            if (contact.accountStatus.equals("INITIATED", true)) {
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
        secAccountList?.get(position)?.let {
            holder.setView(mContext, it)
        }

        holder.binding.arrowTitleLyt.setOnClickListener {

            val accList = secAccountList?.get(position)
            Logg.logging("NominatedContactsAdapter", "accList?.isExpanded ${accList?.isExpanded}")
         accList?.isExpanded = !accList?.isExpanded!!
            listener.onItemClick("open", accList!!, position, accList.isExpanded)

            secAccountList?.get(position)?.isExpanded = accList.isExpanded
            notifyItemChanged(position)

        }

        holder.binding.resendBtn.setOnClickListener {
            Logg.logging(
                "TESTSTR",
                "testess createAccount called on adapter called ${holder.binding.resendBtn.text}"
            )
            val accList = secAccountList?.get(position)
            listener.onItemClick(
                holder.binding.resendBtn.text.toString(),
                accList!!,
                position,
                false
            )

        }
        holder.binding.removeBtn.setOnClickListener {
            val accList = secAccountList?.get(position)

            Logg.logging(
                "TESTSTR",
                "testess createAccount called on adapter called ${holder.binding.removeBtn.text}"
            )
            listener.onItemClick(
                holder.binding.removeBtn.text.toString(),
                accList!!,
                position,
                false
            )

        }

    }

    override fun getItemCount(): Int {
        return secAccountList?.size ?: 0
    }
}
