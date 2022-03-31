package com.heandroid.ui.bottomnav.account.payments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentAccountPaymentMethodsBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentMethodsFragment : BaseFragment<FragmentAccountPaymentMethodsBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentAccountPaymentMethodsBinding.inflate(inflater, container, false)
    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}