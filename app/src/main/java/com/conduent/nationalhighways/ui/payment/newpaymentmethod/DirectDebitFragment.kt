package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentDirectDebitBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DirectDebitFragment : BaseFragment<FragmentDirectDebitBinding>() {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDirectDebitBinding = FragmentDirectDebitBinding.inflate(inflater, container, false)

    override fun init() {
        binding.webView.loadUrl("https://customer.nuapaytest.com/en/signup/sign-up-to-dart-charge/")

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}