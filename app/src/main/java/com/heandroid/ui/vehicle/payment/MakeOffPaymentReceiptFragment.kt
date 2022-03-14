package com.heandroid.ui.vehicle.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentReceiptBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MakeOffPaymentReceiptFragment : BaseFragment<FragmentMakeOffPaymentReceiptBinding>(), View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentReceiptBinding = FragmentMakeOffPaymentReceiptBinding.inflate(inflater,container,false)
    override fun init() {}
    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue ->{ findNavController().navigate(R.id.action_makeOffPaymentReceiptFragment_to_makeOffPaymentCardFragment) }
        }
    }
}