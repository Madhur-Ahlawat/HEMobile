package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.databinding.AdapterCasesEnquiryListBinding
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.listener.ItemClickListener
import com.conduent.nationalhighways.utils.common.Utils

class CasesEnquiryListAdapter(val listener:ItemClickListener,val caseEnquiryList: ArrayList<ServiceRequest>) :
    RecyclerView.Adapter<CasesEnquiryListAdapter.CasesEnquiryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesEnquiryListViewHolder {
        return CasesEnquiryListViewHolder(
            AdapterCasesEnquiryListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = caseEnquiryList.size

    override fun onBindViewHolder(holder: CasesEnquiryListViewHolder, position: Int) {
        holder.binding.viewModel = caseEnquiryList[position]
        val itemData = caseEnquiryList[position]
        holder.itemView.setOnClickListener {
            listener.onItemClick(caseEnquiryList[position], holder.absoluteAdapterPosition)
        }
        val builder = StringBuilder()
        for (i in 0 until
                itemData.id!!.length) {
            builder.append(itemData.id[i])
            builder.append("\u00A0")
        }
        holder.binding.referenceNumberTv.contentDescription = Utils.accessibilityForNumbers(itemData.id.toString())
    }

    class CasesEnquiryListViewHolder(var binding: AdapterCasesEnquiryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}






