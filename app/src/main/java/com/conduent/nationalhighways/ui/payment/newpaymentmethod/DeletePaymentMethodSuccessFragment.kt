package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentDeletePaymentMethodSuccessBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants

class DeletePaymentMethodSuccessFragment :
    BaseFragment<FragmentDeletePaymentMethodSuccessBinding>(), View.OnClickListener {
    private var navFlow: String = ""
    private var lowBalance: String = ""
    private var topUpBalance: String = ""
    private var accountNumber: String = ""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletePaymentMethodSuccessBinding =
        FragmentDeletePaymentMethodSuccessBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBar()
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBar()
        }
        binding.feedbackBt.movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("SetTextI18n")
    override fun initCtrl() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        lowBalance = arguments?.getString(Constants.THRESHOLD_AMOUNT) ?: ""
        topUpBalance = arguments?.getString(Constants.TOP_UP_AMOUNT) ?: ""
        accountNumber = arguments?.getString(Constants.ACCOUNT_NUMBER) ?: ""
        var lBalance = 0.0
        var tBalance = 0.0
        if (lowBalance.isNotEmpty()) {
            lBalance = lowBalance.replace("£", "").replace(",", "").toDouble()
        }
        if (topUpBalance.isNotEmpty()) {
            tBalance = topUpBalance.replace("£", "").replace(",", "").toDouble()
        }

        if (navFlow == Constants.THRESHOLD) {
            binding.btnContinue.text = requireActivity().resources.getString(R.string.str_continue)
            binding.maximumVehicleAdded.text = getString(R.string.str_threshold_limit)
            if (lowBalance.isEmpty()) {
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_top_amount_limit,
                    (String.format("%.2f", tBalance))
                )
            } else if (topUpBalance.isEmpty()) {
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_low_balance_limit,
                    (String.format("%.2f", lBalance))
                )
            } else {
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_low_balance_limit,
                    (String.format("%.2f", lBalance))
                ) + "\n" + getString(
                    R.string.str_your_top_amount_limit,
                    (String.format("%.2f", tBalance))
                )
            }




            binding.cancelBtn.visibility = View.GONE
        } else if (navFlow == Constants.DELETE_CARD) {
            binding.btnContinue.text = resources.getString(R.string.str_continue)
            binding.maximumVehicleAdded.text = getString(R.string.payment_method_deleted)

        }

        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                val bundle = Bundle()
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(R.id.deletePaymentMethodSuccessFragment_to_paymentMethodFragment)
            }

            R.id.cancel_btn -> {
                findNavController().navigate(R.id.deletePaymentMethodSuccessFragment_to_paymentMethodFragment)

            }
        }

    }

}