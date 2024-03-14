package com.conduent.nationalhighways.ui.transactions.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.ItemCrossingsBinding
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardFragmentNew
import com.conduent.nationalhighways.ui.transactions.ViewAllTransactionsFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.visible


class TransactionsInnerAdapterDashboard(
    private var viewAllTransactionsFragment: Fragment,
    private var transactionItemsList: List<TransactionData>,
    private var accSubType: String
) :
    RecyclerView.Adapter<TransactionsInnerAdapterDashboard.TransactionViewHolder>() {
    class TransactionViewHolder(binding: ItemCrossingsBinding) :
        RecyclerView.ViewHolder(binding.root)

    lateinit var binding: ItemCrossingsBinding
    private var pos: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemCrossingsBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return transactionItemsList.size
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder, position: Int
    ) {
        pos = -1
        var topup = ""
        val recentTransactionItem = transactionItemsList[holder.absoluteAdapterPosition]
        binding?.apply {
            if (accSubType == Constants.PAYG) {
                balanceCl.gone()
            }else{
                balanceCl.visible()
                valueCurrentBalance.text = recentTransactionItem.balance
            }
            if (recentTransactionItem.activity?.lowercase()?.contains("toll") == false) {
                indicatorIconEuro.visible()
                indicatorIconTransactionType.invisible()
                indicatorIconEuro.background=ResourcesCompat.getDrawable(indicatorIconEuro.context.resources, R.drawable.ic_euro_circular_green, null)
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
                if (recentTransactionItem.exitDirection.equals("N")) {
                    tvTransactionType.text =
                        tvTransactionType.context.getString(R.string.northbound)
                } else {
                    tvTransactionType.text =
                        tvTransactionType.context.getString(R.string.southbound)
                }
                verticalStripTransactionType.background.setTint(
                    viewAllTransactionsFragment.resources.getColor(
                        R.color.red_status
                    )
                )
                if (HomeActivityMain.accountDetailsData?.accountInformation?.accSubType.equals(
                        Constants.EXEMPT_PARTNER
                    )
                ) {
                    topup = recentTransactionItem.amount ?: ""
                } else {
                    topup = "-" + recentTransactionItem.amount
                }
                valueTopUpAmount.text = topup
                indicatorIconEuro.gone()
                Glide.with(indicatorIconTransactionType.context)
                    .load(
                        AppCompatResources.getDrawable(
                            viewAllTransactionsFragment.requireContext(),
                            R.drawable.ic_car_grey
                        )
                    )
                    .into(indicatorIconTransactionType)
                valueTopUpAmount.setTextColor(
                    viewAllTransactionsFragment.resources.getColor(
                        R.color.red_status,
                        null
                    )
                )
            }
        }
        holder.itemView.setOnClickListener {
            pos = holder.absoluteAdapterPosition
            val bundle = Bundle()
            HomeActivityMain.crossing = transactionItemsList[pos]
            if (HomeActivityMain.crossing?.activity?.lowercase().equals("toll")) {
                if (viewAllTransactionsFragment is ViewAllTransactionsFragment) {
                    (viewAllTransactionsFragment as ViewAllTransactionsFragment).findNavController()
                        .navigate(
                            R.id.action_crossingHistoryFragment_to_tollDetails,
                            bundle
                        )
                } else {
                    (viewAllTransactionsFragment as DashboardFragmentNew).findNavController()
                        .navigate(
                            R.id.action_dashBoardFragment_to_tollDetails,
                            bundle
                        )
                }

            } else {
                if (viewAllTransactionsFragment is ViewAllTransactionsFragment) {
                    (viewAllTransactionsFragment as ViewAllTransactionsFragment).findNavController()
                        .navigate(
                            R.id.action_crossingHistoryFragment_to_topUpDetails,
                            bundle
                        )
                } else {
                    (viewAllTransactionsFragment as DashboardFragmentNew).findNavController()
                        .navigate(
                            R.id.action_dashBoardFragment_to_topUpDetails,
                            bundle
                        )
                }

            }
        }
    }

}