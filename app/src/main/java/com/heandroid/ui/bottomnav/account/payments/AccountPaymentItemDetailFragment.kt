package com.heandroid.ui.bottomnav.account.payments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentAccountPaymentItemDetailBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentItemDetailFragment : BaseFragment<FragmentAccountPaymentItemDetailBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentAccountPaymentItemDetailBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.apply {

        }
    }

    override fun observer() {
    }
}