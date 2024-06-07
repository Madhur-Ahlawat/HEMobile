package com.conduent.nationalhighways.ui.bottomnav.account.payments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentAccountPaymentItemDetailBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentItemDetailFragment : BaseFragment<FragmentAccountPaymentItemDetailBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountPaymentItemDetailBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.apply {

        }
    }

    override fun observer() {
    }
}