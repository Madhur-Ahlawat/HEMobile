package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentMakeOneOffPaymentSuccessfullyBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class MakeOneOffPaymentSuccessfullyFragment :
    BaseFragment<FragmentMakeOneOffPaymentSuccessfullyBinding>(), View.OnClickListener {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOneOffPaymentSuccessfullyBinding =
        FragmentMakeOneOffPaymentSuccessfullyBinding.inflate(inflater, container, false)

    override fun init() {

    }

    override fun initCtrl() {
        binding.createAccount.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.createAccount -> {
                findNavController().navigate(R.id.action_make_one_off_payment_successfully_to_createAccountPrerequisite)
            }
        }
    }

}