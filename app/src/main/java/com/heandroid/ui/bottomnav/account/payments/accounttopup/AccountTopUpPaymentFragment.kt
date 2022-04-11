package com.heandroid.ui.bottomnav.account.payments.accounttopup

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentAccountTopupPaymentBinding
import com.heandroid.databinding.FragmentCreateAccountTopUpBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountTopUpPaymentFragment: BaseFragment<FragmentAccountTopupPaymentBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentAccountTopupPaymentBinding.inflate(inflater, container, false)
    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}