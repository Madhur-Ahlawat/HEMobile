package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentDeletePaymentMethodSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletePaymentMethodSuccessFragment :
    BaseFragment<FragmentDeletePaymentMethodSuccessBinding>(), View.OnClickListener {
    private var navFlow: String = ""
    private var lowBalance: Double = 0.0
    private var topUpBalance: Double = 0.0


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletePaymentMethodSuccessBinding =
        FragmentDeletePaymentMethodSuccessBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        lowBalance = arguments?.getDouble(Constants.THRESHOLD_AMOUNT) ?: 0.0
        topUpBalance = arguments?.getDouble(Constants.TOP_UP_AMOUNT) ?: 0.0

        if (navFlow == Constants.THRESHOLD) {
            binding.maximumVehicleAdded.text = getString(R.string.str_threshold_limit)
            binding.textMaximumVehicle.text = getString(
                R.string.str_your_low_balance_limit,
                (String.format("%.2f", lowBalance)),
                (String.format("%.2f", topUpBalance))
            )

            binding.cancelBtn.visibility = View.GONE
        }

        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                findNavController().navigate(R.id.deletePaymentMethodSuccessFragment_to_paymentMethodFragment)
            }

            R.id.cancel_btn -> {
                findNavController().navigate(R.id.deletePaymentMethodSuccessFragment_to_paymentMethodFragment)

            }
        }

    }

}