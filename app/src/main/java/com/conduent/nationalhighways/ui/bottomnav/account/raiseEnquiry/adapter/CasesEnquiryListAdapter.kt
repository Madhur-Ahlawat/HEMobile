package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.databinding.AdapterCasesEnquiryListBinding
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.listener.ItemClickListener

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
        holder.itemView.setOnClickListener {
            listener.onItemClick(caseEnquiryList[position], holder.absoluteAdapterPosition)
        }
    }

    class CasesEnquiryListViewHolder(var binding: AdapterCasesEnquiryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}






