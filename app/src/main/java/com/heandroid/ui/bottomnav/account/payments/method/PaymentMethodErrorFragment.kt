package com.heandroid.ui.bottomnav.account.payments.method

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentPaymentMethodErrorBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodErrorFragment: BaseFragment<FragmentPaymentMethodErrorBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPaymentMethodErrorBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.retryPayment.setOnClickListener(this@PaymentMethodErrorFragment)
        binding.cancelPayment.setOnClickListener(this@PaymentMethodErrorFragment)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.retry_payment -> {
                findNavController().popBackStack()
            }

            R.id.cancel_payment -> {
                findNavController().navigate(R.id.action_paymentMethodErrorFragment_to_paymentMethodFragment)
            }
        }
    }
}