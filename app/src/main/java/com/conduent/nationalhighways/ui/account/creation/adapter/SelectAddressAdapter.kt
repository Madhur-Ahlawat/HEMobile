package com.conduent.nationalhighways.ui.account.creation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.databinding.AdapterSelectAddressBinding

class SelectAddressAdapter(private val context: Context?,
                           private var list: MutableList<DataAddress?>):
    RecyclerView.Adapter<SelectAddressAdapter.SelectAddressViewHolder>() {


    inner class SelectAddressViewHolder(var binding: AdapterSelectAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAddressViewHolder=SelectAddressViewHolder(
        AdapterSelectAddressBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )    )

    override fun onBindViewHolder(holder: SelectAddressViewHolder, position: Int) {
        holder.binding.address.text=list[position]?.country

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(mainList: MutableList<DataAddress?>) {
        this.list=mainList
        notifyDataSetChanged()

    }
}