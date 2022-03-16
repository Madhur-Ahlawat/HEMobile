package com.heandroid.ui.startNow.contactdartcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.account.CaseEnquiryResponse
import com.heandroid.data.model.account.ServiceRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.databinding.AdapterCrossingHistoryBinding
import com.heandroid.databinding.ItemCaseHistoryBinding
import com.heandroid.ui.vehicle.vehiclehistory.VehicleHistoryCrossingHistoryFragment
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Utils.getDirection
import com.heandroid.utils.common.Utils.loadStatus

class CaseHistoryAdapter(
    private val myFragment: Fragment?,
    private var list: List<ServiceRequest?>?
) : RecyclerView.Adapter<CaseHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            ItemCaseHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list?.get(position))

        holder.binding.cvMain.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Constants.DATA, list?.get(position))
            if (myFragment is CaseHistoryDartChargeFragment)
                it.findNavController().navigate(
                    R.id.action_caseHistoryDartChargeFragment_to_caseHistoryDetailsDartChargeFragment,
                    bundle
                )
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    class HistoryViewHolder(var binding: ItemCaseHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {



        fun bind(data: ServiceRequest?) {
            data?.run {
                binding.apply {
                    caseNumber.text = id
                    caseDate.text = created
                    tvStatus.text = status
                    loadStatus("Y", tvStatus)
                }
            }
        }
    }
}