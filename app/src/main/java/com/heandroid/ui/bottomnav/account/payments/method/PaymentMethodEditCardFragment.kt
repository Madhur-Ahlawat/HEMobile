package com.heandroid.ui.bottomnav.account.payments.method

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.payment.AddCardModel
import com.heandroid.databinding.FragmentPaymentMethodEditCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodEditCardFragment : BaseFragment<FragmentPaymentMethodEditCardBinding>(),
    View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPaymentMethodEditCardBinding.inflate(inflater,container,false)
    override fun init() {
        binding.model=arguments?.getParcelable(DATA)
        binding.cbDefault.isChecked=binding.model.default
    }
    override fun initCtrl() {
        binding.apply {
            tvChangeCardNo.setOnClickListener(this@PaymentMethodEditCardFragment)
            tvChangeExpire.setOnClickListener(this@PaymentMethodEditCardFragment)
            tvChangeName.setOnClickListener(this@PaymentMethodEditCardFragment)
            tvChangeCVV.setOnClickListener(this@PaymentMethodEditCardFragment)
            btnDelete.setOnClickListener(this@PaymentMethodEditCardFragment)
            cbDefault.setOnCheckedChangeListener(this@PaymentMethodEditCardFragment)
        }
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvChangeCardNo -> {
                    val bundle= Bundle()
                    bundle.putParcelable(DATA,binding.model)
                    bundle.putBoolean("edit",true)
                    findNavController().navigate(R.id.action_paymentMethodEditCardFragment_to_paymentMethodCardFragment,bundle)
            }
            R.id.btnDelete -> {  findNavController().navigate(R.id.action_paymentMethodEditCardFragment_to_paymentMethodFragment) }
            else ->{ binding.tvChangeCardNo.performClick() }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        binding.model.default=isChecked
    }
}