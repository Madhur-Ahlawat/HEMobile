package com.conduent.nationalhighways.ui.auth.suspended

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltReopenedBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSuspendReOpenFragment:BaseFragment<FragmentAccountSuspendHaltReopenedBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltReopenedBinding= FragmentAccountSuspendHaltReopenedBinding.inflate(inflater,container,false)

    override fun init() {

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}