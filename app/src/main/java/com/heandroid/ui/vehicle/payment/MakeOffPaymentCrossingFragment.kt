package com.heandroid.ui.vehicle.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.payment.MakeOffPaymentCrossingModel
import com.heandroid.databinding.FragmentMakeOffPaymentCrossingBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakeOffPaymentCrossingFragment : BaseFragment<FragmentMakeOffPaymentCrossingBinding>(), FutureCrossingQuantityListner, View.OnClickListener {

    val list: MutableList<MakeOffPaymentCrossingModel?>? = ArrayList()
    private var totalPrice : Double? = 0.0

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentCrossingBinding = FragmentMakeOffPaymentCrossingBinding.inflate(inflater,container,false)
    override fun init() {
        for(i in 0..10) {list?.add(MakeOffPaymentCrossingModel(expand = false, quantity = 1, price = 5.0))}

        for(i in list?.indices!!){
           val futureCrossingAmount = (list[i]?.price?.times(list[i]?.quantity?.toDouble()?:0.0))
           val payableCrossingAmount = (list[i]?.price?:0.0).times(list[i]?.quantity?.toDouble()?:0.0)
           totalPrice = totalPrice?.plus(payableCrossingAmount.plus(futureCrossingAmount?:0.0))
        }
        binding.tvTotalPaymentAmount.text=requireActivity().getString(R.string.price, " $totalPrice")
        binding.rvCrossing.layoutManager=LinearLayoutManager(requireActivity())
        binding.rvCrossing.adapter=MakeOffPaymentCrossingAdapter(requireActivity(),list,this)
    }
    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
    }
    override fun observer() {}

    override fun onAdd(position: Int) {
        updateQuantity(position,+1)
    }

    override fun onMinus(position: Int) {
        updateQuantity(position,-1)
    }

    private fun updateQuantity(position: Int, quantity: Int){
        list?.get(position)?.quantity=(list?.get(position)?.quantity?:0)+(quantity)
        val futureCrossingAmount = (list?.get(position)?.price?:0.0)*(quantity)
        val payableCrossingAmount = (list?.get(position)?.price?:0.0)*(quantity)
        totalPrice = totalPrice?.plus(payableCrossingAmount?.plus(futureCrossingAmount))
        binding.rvCrossing.adapter?.notifyItemChanged(position)

        binding.tvTotalPaymentAmount.text=requireActivity().getString(R.string.price, " $totalPrice")
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue -> { findNavController().navigate(R.id.action_makeOneOffPaymentCrossingFragment_to_makeOffPaymentReceiptFragment) }
        }
    }
}