package com.heandroid.ui.bottomnav.account.payments.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.databinding.ItemPaginationNumberBinding
import hilt_aggregated_deps._com_heandroid_ui_account_profile_postcode_ProfilePostCodeFragment_GeneratedInjector

class AccountPaymentHistoryPaginationAdapter(
    val fragment: Fragment,
    var pageCount: Int,
    private var selectedPos : Int
) : RecyclerView.Adapter<AccountPaymentHistoryPaginationAdapter.PaginationNumberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaginationNumberViewHolder {
        return PaginationNumberViewHolder(
            ItemPaginationNumberBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PaginationNumberViewHolder, position: Int) {
        holder.bind(position + 1)
        if (position + 1 == selectedPos){
            setSelected(holder)
        } else {
            setUnChecked(holder)
        }
        holder.binding.mainLayout.setOnClickListener {
            setSelected(holder)
            if (fragment is AccountPaymentHistoryFragment) {
                fragment.setSelectedPosition(position + 1)
            }
        }
    }

    private fun setSelected(holder: PaginationNumberViewHolder) {
        fragment.context?.let {
            holder.binding.mainLayout.setCardBackgroundColor(it.getColor(R.color.green))
            holder.binding.tvNumber.setTextColor(it.getColor(R.color.white))
        }
    }

    private fun setUnChecked(holder: PaginationNumberViewHolder) {
        fragment.context?.let {
            holder.binding.mainLayout.strokeWidth = 2
            holder.binding.mainLayout.strokeColor = it.getColor(R.color.green)
        }
    }

    fun setCount(count : Int) {
        pageCount = count
    }

    fun setSelectedPosit(pos : Int) {
        selectedPos = pos
    }

    override fun getItemCount(): Int = pageCount

    class PaginationNumberViewHolder(var binding: ItemPaginationNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(count: Int) {
            binding.tvNumber.text = count.toString()
        }
    }
}