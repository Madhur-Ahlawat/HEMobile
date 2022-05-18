package com.heandroid.ui.payment

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.payment.MakeOffPaymentCrossingModel
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.AdapterMakeOffPaymentCrossingBinding
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible

class MakeOffPaymentCrossingAdapter(private val context: Context?,
                                    private var list : MutableList<VehicleResponse?>?,
                                    private val listner : FutureCrossingQuantityListner?) : RecyclerView.Adapter<MakeOffPaymentCrossingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakeOffPaymentCrossingAdapter.MyViewHolder
    = MyViewHolder(AdapterMakeOffPaymentCrossingBinding.inflate(LayoutInflater.from(context),parent,false))


    override fun onBindViewHolder(holder: MakeOffPaymentCrossingAdapter.MyViewHolder, position: Int) {
       holder.bind(list?.get(position))
    }

    override fun getItemViewType(position: Int): Int = position
    override fun getItemCount(): Int = list?.size?:0

    inner class MyViewHolder(var binding : AdapterMakeOffPaymentCrossingBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(data : VehicleResponse?){
          data?.run {
                when(isExpanded){
                    true -> {
                        binding.llBelowContainer.visible()
                        binding.tvVehicleNo.setTypeface(binding.tvVehicleNo.typeface, Typeface.BOLD) }
                    else -> {
                        binding.llBelowContainer.gone()
                        binding.tvVehicleNo.setTypeface(binding.tvVehicleNo.typeface, Typeface.NORMAL)
                    }
                }
                binding.tvAmount.text=context?.getString(R.string.price,""+price)
                binding.tvVehicleNo.text=plateInfo?.number
                binding.tvFutureCrossingQty.text=futureQuantity.toString()
              binding.tvCrossingQty.text = pastQuantity.toString()
                binding.cvMain.setOnClickListener(this@MyViewHolder)
                binding.ivAdd.setOnClickListener(this@MyViewHolder)
                binding.ivMinus.setOnClickListener(this@MyViewHolder)
            }
        }

        override fun onClick(v: View?) {
            when(v?.id){
                R.id.cvMain ->{
                    list?.get(absoluteAdapterPosition)?.isExpanded=!(list?.get(absoluteAdapterPosition)?.isExpanded?:false)
                    notifyItemChanged(absoluteAdapterPosition)
                }

                R.id.ivAdd ->{ listner?.onAdd(absoluteAdapterPosition) }
                R.id.ivMinus ->{
                    if((list?.get(absoluteAdapterPosition)?.futureQuantity?:0)>0)
                    listner?.onMinus(absoluteAdapterPosition) }
            }
        }

    }
}