package com.heandroid.ui.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentConfirmationBinding
import com.heandroid.ui.base.BaseFragment

class MakeOffPaymentConfirmationFragment : BaseFragment<FragmentMakeOffPaymentConfirmationBinding>(), View.OnClickListener, MakeOffPaymentVehicleAdapter.OnVehicleItemClickedListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentConfirmationBinding = FragmentMakeOffPaymentConfirmationBinding.inflate(inflater,container,false)
    override fun init() {
        binding.rvVechileList.layoutManager=LinearLayoutManager(requireActivity())
        binding.rvVechileList.adapter=MakeOffPaymentVehicleAdapter(requireActivity(),null , this)
    }
    override fun initCtrl() {
        binding.btnPayNow.setOnClickListener(this)
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnPayNow ->{ findNavController().navigate(R.id.action_makeOffPaymentConfirmationFragment_to_makeOffPaymentSuccessfulFragment) }
        }
    }

    override fun onViewClicked()
    {
        Toast.makeText(requireContext(), "Vehicle selected", Toast.LENGTH_LONG).show()
    }
}