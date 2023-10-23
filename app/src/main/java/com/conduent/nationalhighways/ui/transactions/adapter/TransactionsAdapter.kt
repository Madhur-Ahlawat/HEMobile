package com.conduent.nationalhighways.ui.transactions.adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.ItemAllTansactionsBinding
import com.conduent.nationalhighways.ui.transactions.ViewAllTransactionsFragment
import java.text.SimpleDateFormat


class TransactionsAdapter(
    var context: ViewAllTransactionsFragment,
    var transactionItemList: MutableList<String>, var transactionItemHashMap:MutableMap<String,MutableList<TransactionData>>
) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {
    class TransactionViewHolder(binding: ItemAllTansactionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    private var binding: ItemAllTansactionsBinding? = null
    private var transactionItem: TransactionData? = null
    private var pos: Int = -1
    val dfDate = SimpleDateFormat("dd MMM yyyy")
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
        var innerAdapter:TransactionsInnerAdapter?=null
        var recentTransactionItem = transactionItemList.get(position)
        binding?.apply {
            headerDate!!.text = dfDate.parse(
                recentTransactionItem
            ).toString()
            innerAdapter=TransactionsInnerAdapter(context,transactionItemHashMap.get(recentTransactionItem)!!)
            rvCrossings.layoutManager=LinearLayoutManager(context.requireActivity()).apply { orientation=LinearLayoutManager.VERTICAL }
            rvCrossings.adapter=innerAdapter
        }
    }

}