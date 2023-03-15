package com.conduent.nationalhighways.ui.account.creation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.databinding.AdapterSelectAddressBinding

class SelectAddressAdapter(private val context: Context?,
                           private var list: ArrayList<String>):
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

    }

    override fun getItemCount(): Int {
        return list.size
    }
}