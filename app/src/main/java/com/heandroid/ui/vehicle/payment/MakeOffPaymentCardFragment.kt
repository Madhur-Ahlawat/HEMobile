package com.heandroid.ui.vehicle.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.CardNumberFormatterTextWatcher
import com.heandroid.utils.extn.addExpriryListner


class MakeOffPaymentCardFragment : BaseFragment<FragmentMakeOffPaymentCardBinding>(),View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentCardBinding = FragmentMakeOffPaymentCardBinding.inflate(inflater,container,false)
    override fun init() {
    }
    override fun initCtrl() {
        binding.tieCardNo.addTextChangedListener(CardNumberFormatterTextWatcher())
        binding.tieExpiryDate.addExpriryListner()
        binding.btnContinue.setOnClickListener(this)
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue -> {
                findNavController().navigate(R.id.action_makeOffPaymentCardFragment_to_makeOffPaymentConfirmationFragment)
            }
        }
    }


}