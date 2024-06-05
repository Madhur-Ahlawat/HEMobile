package com.conduent.nationalhighways.ui.transactions.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardFragmentNew
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.RecyclerViewItemDecorator

class TransactionsAdapter(
    var context: Fragment,
    var transactionItemList: MutableList<String>,
    var transactionItemHashMap: MutableMap<String, MutableList<TransactionData>>,
    var accSubType:String
) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {
    class TransactionViewHolder(binding: ItemAllTansactionsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var binding: ItemAllTansactionsBinding? = null
    private var pos: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemAllTansactionsBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return transactionItemList.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        pos = -1
        var innerAdapter: TransactionsInnerAdapter? = null
        var recentTransactionItem = transactionItemList.get(position)
        Log.e("POSXJ220", transactionItemList.get(position))
        Log.e(
            "TAG",
            " POSXJ220 * * " + transactionItemHashMap.get(recentTransactionItem).orEmpty().size
        )
        binding?.apply {
            if (context is DashboardFragmentNew) {
                headerDate.gone()
            } else {
                headerDate.text = recentTransactionItem
                headerDate.visible()
            }
            innerAdapter = TransactionsInnerAdapter(
                context, Utils.sortTransactionsDateWiseDescending(
                    transactionItemHashMap.get(recentTransactionItem) ?: mutableListOf()
                ),accSubType
            )
            var layoutManager = LinearLayoutManager(context.requireContext())
            rvCrossings.layoutManager = layoutManager
            rvCrossings.adapter = innerAdapter
            rvCrossings.run {
                if (itemDecorationCount == 0) {
                    addItemDecoration(RecyclerViewItemDecorator(0, 1))
                }
            }
        }
    }

}