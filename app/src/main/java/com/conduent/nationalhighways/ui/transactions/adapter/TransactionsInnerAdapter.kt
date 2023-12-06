package com.conduent.nationalhighways.ui.transactions.adapter;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.databinding.ItemCrossingsBinding
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardFragmentNew
import com.conduent.nationalhighways.ui.transactions.ViewAllTransactionsFragment
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import java.text.SimpleDateFormat


class TransactionsInnerAdapter(
    var viewAllTransactionsFragment: Fragment,
    var transactionItemsList: List<TransactionData>
) :
    RecyclerView.Adapter<TransactionsInnerAdapter.TransactionViewHolder>() {
    class TransactionViewHolder(binding: ItemCrossingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
    private var binding: ItemCrossingsBinding? = null
    private var pos: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemCrossingsBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        Log.e("TAG", "getItemCount: transactionItemsList "+transactionItemsList.size )
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
                Glide.with(indicatorIconTransactionType.context).load(indicatorIconTransactionType.context.getDrawable(R.drawable.ic_euro_circular_green)).into(indicatorIconTransactionType)
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
                if(HomeActivityMain.crossing?.exitDirection.equals("N")){
                    tvTransactionType.text = "Northbound"
                }
                else{
                    tvTransactionType.text = "Southbound"
                }
                verticalStripTransactionType.background.setTint(
                    viewAllTransactionsFragment.resources.getColor(
                        R.color.red_status
                    )
                )
                topup = "-" + recentTransactionItem.amount
                valueTopUpAmount.text = topup
                indicatorIconEuro.gone()
                Glide.with(indicatorIconTransactionType.context).load(indicatorIconTransactionType.context.getDrawable(R.drawable.ic_car_grey)).into(indicatorIconTransactionType)
                valueTopUpAmount.setTextColor(viewAllTransactionsFragment.resources.getColor(R.color.red_status))
            }
        }
        holder.itemView.setOnClickListener {
            pos = position!!
            val bundle = Bundle()
            HomeActivityMain.crossing = transactionItemsList.get(pos)
            if (HomeActivityMain.crossing?.activity?.lowercase().equals("toll")) {
                if(viewAllTransactionsFragment is ViewAllTransactionsFragment){
                    (viewAllTransactionsFragment as ViewAllTransactionsFragment).findNavController().navigate(
                        R.id.action_crossingHistoryFragment_to_tollDetails,
                        bundle
                    )
                }
                else{
                    (viewAllTransactionsFragment as DashboardFragmentNew).findNavController().navigate(
                        R.id.action_dashBoardFragment_to_tollDetails,
                        bundle
                    )
                }

            } else {
                if(viewAllTransactionsFragment is ViewAllTransactionsFragment) {
                    (viewAllTransactionsFragment as ViewAllTransactionsFragment).findNavController().navigate(
                        R.id.action_crossingHistoryFragment_to_topUpDetails,
                        bundle
                    )
                }
                else{
                    (viewAllTransactionsFragment as DashboardFragmentNew).findNavController().navigate(
                        R.id.action_dashBoardFragment_to_topUpDetails,
                        bundle
                    )
                }

            }
        }
    }

}