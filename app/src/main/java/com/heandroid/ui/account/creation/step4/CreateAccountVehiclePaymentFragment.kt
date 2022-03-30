package com.heandroid.ui.account.creation.step4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentCreateAccountVehiclePaymentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants

class CreateAccountVehiclePaymentFragment  : BaseFragment<FragmentCreateAccountVehiclePaymentBinding>(), View.OnClickListener{
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateAccountVehiclePaymentBinding {
        return FragmentCreateAccountVehiclePaymentBinding.inflate(inflater, container, false)
    }

    override fun init() {
    }

    override fun initCtrl() {
        binding.payButton.setOnClickListener(this@CreateAccountVehiclePaymentFragment)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.pay_button ->{
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA,arguments?.getParcelable(Constants.DATA))
              //  findNavController().navigate(R.id.action_showVehicleDetailsFragment_to_choosePaymentFragment,bundle)
            }
          //  R.id.cancel_btn -> { findNavController().navigate(R.id.action_showVehicleDetailsFragment_to_findYourVehicleFragment) }
        }
    }
}