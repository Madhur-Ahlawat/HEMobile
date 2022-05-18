package com.heandroid.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.AdapterMakeOffPaymentVechileBinding

class MakeOffPaymentVehicleAdapter(
    private val context: Context?,
    private var list: MutableList<VehicleResponse>,
    private var listener: OnVehicleItemClickedListener
) : RecyclerView.Adapter<MakeOffPaymentVehicleAdapter.MyViewHolder>() {

    interface OnVehicleItemClickedListener {

        fun onViewClicked() {

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MakeOffPaymentVehicleAdapter.MyViewHolder = MyViewHolder(
        AdapterMakeOffPaymentVechileBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: MakeOffPaymentVehicleAdapter.MyViewHolder,
        position: Int
    ) {
        val mResp = list!![position]
        holder.setView(mResp)
    }

    override fun getItemCount(): Int = list?.size ?: 0
    inner class MyViewHolder(var binding: AdapterMakeOffPaymentVechileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setView(resp: VehicleResponse) {
            binding.clRoot.setOnClickListener {
                listener?.onViewClicked()
            }
            binding.totalPrice.text = "£ ${resp.price}"
            binding.vrmRegNo.text = "${resp?.newPlateInfo?.number}"
        }
    }
}