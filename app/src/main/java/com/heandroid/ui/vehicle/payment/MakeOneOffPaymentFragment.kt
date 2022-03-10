package com.heandroid.ui.vehicle.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentBinding
import com.heandroid.ui.base.BaseFragment

class MakeOneOffPaymentFragment : BaseFragment<FragmentMakeOffPaymentBinding>(),
    View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentBinding = FragmentMakeOffPaymentBinding.inflate(inflater,container,false)
    override fun init() {
        binding.tvLabel.text="•  You have made since 6 am yesterday morning. \n" +
                             "•  You are planning to make for next 12 months \n" +
                             "   are valid for same time period. \n" +
                             "•  You can add only 5 vehicle. \n"
    }
    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)

    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue ->{ findNavController().navigate(R.id.action_makeOneOffPaymentFragment_to_makeOneOffPaymentCrossingFragment) }
        }
    }
}