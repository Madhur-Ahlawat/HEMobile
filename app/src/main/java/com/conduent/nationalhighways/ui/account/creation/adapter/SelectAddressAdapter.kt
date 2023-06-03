package com.conduent.nationalhighways.ui.account.creation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.databinding.AdapterSelectAddressBinding

class SelectAddressAdapter(private val context: Context?,
                           private var list: MutableList<DataAddress?>, private val address:AddressCallBack ):

    RecyclerView.Adapter<SelectAddressAdapter.SelectAddressViewHolder>() {




    inner class SelectAddressViewHolder(var binding: AdapterSelectAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAddressViewHolder=SelectAddressViewHolder(
        AdapterSelectAddressBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

    )

    override fun onBindViewHolder(holder: SelectAddressViewHolder, position: Int) {
        val strBuilder = java.lang.StringBuilder()
            .append(list[position]?.street)
            .append(", ")
            .append(list[position]?.town)
            .append(", ")
            .append(list[position]?.postcode)
        holder.binding.address.text = strBuilder

        holder.binding.radioButton.isChecked = list[position]?.isSelected==true

        holder.binding.radioButton.setOnClickListener{
            address.addressCallback(position)
            notifyDataSetChanged()

        }

        holder.binding.addressLayout.setOnClickListener{
            address.addressCallback(position)
            notifyDataSetChanged()
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(mainList: MutableList<DataAddress?>) {
        this.list=mainList
        notifyDataSetChanged()

    }
    interface AddressCallBack{
        fun addressCallback(position: Int)
    }

}