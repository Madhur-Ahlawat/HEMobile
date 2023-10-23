package com.conduent.nationalhighways.ui.transactions.adapter;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.databinding.ItemCrossingsBinding
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.transactions.ViewAllTransactionsFragment
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import java.text.SimpleDateFormat


class TransactionsInnerAdapter(
    var viewAllTransactionsFragment: ViewAllTransactionsFragment,
    var transactionItemsList: List<TransactionData>
) :
    RecyclerView.Adapter<TransactionsInnerAdapter.TransactionViewHolder>() {
    class TransactionViewHolder(binding: ItemCrossingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
    private var binding: ItemCrossingsBinding? = null
    private var transactionItem: TransactionData? = null
    private var pos: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemCrossingsBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return transactionItemsList.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        pos = -1
        var topup: String? = null
        var recentTransactionItem = transactionItemsList.get(position)
        binding?.apply {
            valueCurrentBalance.text = recentTransactionItem.balance
            if (recentTransactionItem.activity?.toLowerCase()?.contains("toll") == false) {
                indicatorIconEuro.visible()
                indicatorIconTransactionType.post {
                    Runnable {
                        indicatorIconTransactionType.setImageDrawable(
                            viewAllTransactionsFragment.resources.getDrawable(
                                R.drawable.ic_euro_circular_green
                            )
                        )
                    }
                }
                tvTransactionType.text =
                    viewAllTransactionsFragment.resources.getString(R.string.top_up)
                verticalStripTransactionType.background.setTint(
                    viewAllTransactionsFragment.resources.getColor(
                        R.color.green_status
                    )
                )
                topup = "+" + recentTransactionItem.amount
                valueTopUpAmount.text = topup
                valueTopUpAmount.setTextColor(viewAllTransactionsFragment.resources.getColor(R.color.green_status))
            } else {
                tvTransactionType.text = recentTransactionItem.exitPlazaName
                verticalStripTransactionType.background.setTint(
                    viewAllTransactionsFragment.resources.getColor(
                        R.color.red_status
                    )
                )
                topup = "-" + recentTransactionItem.amount
                valueTopUpAmount.text = topup
                indicatorIconEuro.gone()

                indicatorIconTransactionType.post {
                    Runnable {
                        indicatorIconTransactionType.setImageDrawable(
                            viewAllTransactionsFragment.resources.getDrawable(
                                R.drawable.ic_car_grey
                            )
                        )
                    }
                }
                valueTopUpAmount.setTextColor(viewAllTransactionsFragment.resources.getColor(R.color.red_status))
            }
        }
        holder.itemView.setOnClickListener {
            pos = position!!
            val bundle = Bundle()
            HomeActivityMain.crossing = transactionItemsList.get(pos)
            if (HomeActivityMain.crossing!!.activity?.toLowerCase().equals("toll")) {
                viewAllTransactionsFragment.findNavController().navigate(
                    R.id.action_crossingHistoryFragment_to_tollDetails,
                    bundle
                )
            } else {
                viewAllTransactionsFragment.findNavController().navigate(
                    R.id.action_crossingHistoryFragment_to_topUpDetails,
                    bundle
                )
            }
        }
    }

}