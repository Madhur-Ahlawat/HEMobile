package com.heandroid.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.databinding.AdapterMakeOffPaymentVechileBinding

class MakeOffPaymentVehicleAdapter(private val context: Context?, private var list: MutableList<*>?, private var listener:  OnVehicleItemClickedListener) : RecyclerView.Adapter<MakeOffPaymentVehicleAdapter.MyViewHolder>() {

    interface OnVehicleItemClickedListener{

        fun onViewClicked()
        {

        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakeOffPaymentVehicleAdapter.MyViewHolder = MyViewHolder(AdapterMakeOffPaymentVechileBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun onBindViewHolder(holder: MakeOffPaymentVehicleAdapter.MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int =/* list?.size?:0*/10
    inner class MyViewHolder(var binding : AdapterMakeOffPaymentVechileBinding) : RecyclerView.ViewHolder(binding.root){

        fun setView()
        {
            binding.clRoot.setOnClickListener {
                listener?.onViewClicked()
            }
        }
    }
}