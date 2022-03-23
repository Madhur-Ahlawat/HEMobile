package com.heandroid.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.databinding.AdapterMakeOffPaymentVechileBinding

class MakeOffPaymentVechileAdapter(private val context: Context?,private var list: MutableList<*>?) : RecyclerView.Adapter<MakeOffPaymentVechileAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakeOffPaymentVechileAdapter.MyViewHolder = MyViewHolder(AdapterMakeOffPaymentVechileBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun onBindViewHolder(holder: MakeOffPaymentVechileAdapter.MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int =/* list?.size?:0*/10
    inner class MyViewHolder(var binding : AdapterMakeOffPaymentVechileBinding) : RecyclerView.ViewHolder(binding.root){
    }
}