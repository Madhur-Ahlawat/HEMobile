package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentDeletePaymentMethodSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

class DeletePaymentMethodSuccessFragment :
    BaseFragment<FragmentDeletePaymentMethodSuccessBinding>(), View.OnClickListener {
    private var navFlow: String = ""
    private var lowBalance: String =""
    private var topUpBalance: String = ""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletePaymentMethodSuccessBinding =
        FragmentDeletePaymentMethodSuccessBinding.inflate(inflater, container, false)

    override fun init() {
    }

    @SuppressLint("SetTextI18n")
    override fun initCtrl() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        lowBalance = arguments?.getString(Constants.THRESHOLD_AMOUNT) ?:""
        topUpBalance = arguments?.getString(Constants.TOP_UP_AMOUNT) ?: ""
        var lBalance=0.0
        var tBalance=0.0
        if (lowBalance.isNotEmpty()){
             lBalance=lowBalance.replace("£","").toDouble()
        }
        if (topUpBalance.isNotEmpty()){
            tBalance=topUpBalance.replace("£","").toDouble()
        }

        if (navFlow == Constants.THRESHOLD) {
            binding.maximumVehicleAdded.text = getString(R.string.str_threshold_limit)
            if (lowBalance.isEmpty()){
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_top_amount_limit,
                    (String.format("%.2f", lBalance.toDouble()))
                )
            }else if (topUpBalance.isEmpty()){
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_low_balance_limit,
                    (String.format("%.2f", tBalance.toDouble()))
                )
            }else{
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_low_balance_limit,
                    (String.format("%.2f", lBalance.toDouble()))
                )+"\n"+getString(
                    R.string.str_your_top_amount_limit,
                    (String.format("%.2f", tBalance.toDouble())))
            }




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