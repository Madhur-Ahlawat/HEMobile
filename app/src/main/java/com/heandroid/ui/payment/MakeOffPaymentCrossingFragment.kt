package com.heandroid.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentMakeOffPaymentCrossingBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakeOffPaymentCrossingFragment : BaseFragment<FragmentMakeOffPaymentCrossingBinding>(), FutureCrossingQuantityListner, View.OnClickListener {

    var list: MutableList<VehicleResponse?>? = ArrayList()
    private var totalPrice : Double? = 0.0

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentCrossingBinding = FragmentMakeOffPaymentCrossingBinding.inflate(inflater,container,false)
    override fun init() {
        list = arguments?.getParcelableArrayList<VehicleResponse?>(Constants.DATA) 

        for(i in list?.indices!!){
           val futureCrossingAmount = (list?.get(i)?.price?.times(list?.get(i)?.quantity?.toDouble()?:0.0))
           val payableCrossingAmount = (list?.get(i)?.price?:0.0).times(list?.get(i)?.quantity?.toDouble()?:0.0)
           totalPrice = totalPrice?.plus(payableCrossingAmount.plus(futureCrossingAmount?:0.0))
        }
        binding.tvTotalPaymentAmount.text=requireActivity().getString(R.string.price, " $totalPrice")
        binding.rvCrossing.layoutManager=LinearLayoutManager(requireActivity())
        binding.rvCrossing.adapter = MakeOffPaymentCrossingAdapter(requireActivity(),list,this)
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
        totalPrice = totalPrice?.plus(payableCrossingAmount.plus(futureCrossingAmount))
        binding.rvCrossing.adapter?.notifyItemChanged(position)

        binding.tvTotalPaymentAmount.text=requireActivity().getString(R.string.price, " $totalPrice")
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue -> {
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constants.DATA,ArrayList(list))
                findNavController().navigate(R.id.action_makeOneOffPaymentCrossingFragment_to_makeOffPaymentReceiptFragment,bundle)
            }
        }
    }
}