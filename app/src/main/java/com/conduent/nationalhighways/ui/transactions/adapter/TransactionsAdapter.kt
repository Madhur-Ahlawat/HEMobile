package com.conduent.nationalhighways.ui.transactions.adapter;

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import java.text.SimpleDateFormat

class TransactionsAdapter constructor() :
    PagingDataAdapter<TransactionData, TransactionsAdapter.ViewHolder>(differCallback) {
    private lateinit var binding: ItemAllTansactionsBinding
    private lateinit var context: Context
    private var transactionItem: TransactionData? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemAllTansactionsBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        val dfDate = SimpleDateFormat("dd MMM yyyy")
        private var topup: String? = null

        @SuppressLint("SetTextI18n")
        fun bind(recentTransactionItem: TransactionData) {
            binding.apply {
                if (transactionItem == null) {
                    transactionItem = recentTransactionItem
                    headerDate.text = recentTransactionItem.transactionDate
                    headerDate.visible()
                } else {
                    if (dfDate.parse(transactionItem!!.transactionDate) != dfDate.parse(
                            recentTransactionItem.transactionDate
                        )
                    ) {
                        headerDate.text = recentTransactionItem.transactionDate
                        headerDate.visible()
                    } else {
                        headerDate.gone()
                    }
                    transactionItem = recentTransactionItem
                }

                valueCurrentBalance.text = recentTransactionItem.balance
//                    tvTransactionType.text =
//                        recentTransactionItem.activity?.substring(0, 1)!!.toUpperCase().plus(
//                            recentTransactionItem.activity?.substring(
//                                1,
//                                recentTransactionItem.activity.length
//                            )!!.toLowerCase()
//                        )
                if (recentTransactionItem.amount?.contains("-") == false) {
                    tvTransactionType.text = context.resources.getString(R.string.top_up)
                    verticalStripTransactionType.background.setTint(context.resources.getColor(R.color.green_status))
                    topup = "+" + recentTransactionItem.amount
                    valueTopUpAmount.text = topup
                    valueTopUpAmount.setTextColor(context.resources.getColor(R.color.green_status))
                } else {
                    tvTransactionType.text = recentTransactionItem.exitDirection
                    verticalStripTransactionType.background.setTint(context.resources.getColor(R.color.red_status))
                    topup = "-" + recentTransactionItem.amount
                    valueTopUpAmount.text = topup
                    valueTopUpAmount.setTextColor(context.resources.getColor(R.color.red_status))
                }

                root.setOnClickListener {
                    HomeActivityMain.setTitle(context.resources.getString(R.string.payment_details))
                    val bundle = Bundle()
                    HomeActivityMain.crossing = recentTransactionItem
//                        bundle.putInt(Constants.FROM, Constants.FROM_ALL_TRANSACTIONS_TO_DETAILS)
                    if (HomeActivityMain.crossing!!.activity.equals("Toll")) {
//                        findNavController().navigate(
//                            R.id.action_crossingHistoryFragment_to_tollDetails,
//                            bundle
//                        )
                    } else {
//                        findNavController().navigate(
//                            R.id.action_crossingHistoryFragment_to_topUpDetails,
//                            bundle
//                        )
                    }
                }
                if (recentTransactionItem.activity.equals("Toll")) {
                    indicatorIconTransactionType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_car_grey))
                    indicatorIconEuro.gone()
                } else {
                    indicatorIconTransactionType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_euro_circular_green))
                    indicatorIconEuro.visible()
                }
            }
        }
    }

    private var onItemClickListener: ((TransactionData) -> Unit)? = null

    fun setOnItemClickListener(listener: (TransactionData) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<TransactionData>() {
            override fun areItemsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
                return oldItem.entryTime == oldItem.entryTime
            }

            override fun areContentsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
                return oldItem.entryTime==newItem.entryTime
            }
        }
    }

}