package com.conduent.nationalhighways.ui.auth.suspended

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.annotation.meta.When

@AndroidEntryPoint
class AccountSuspendedFragment: BaseFragment<FragmentAccountSuspendHaltTopUpBinding>(), View.OnClickListener {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltTopUpBinding= FragmentAccountSuspendHaltTopUpBinding.inflate(inflater,container,false)

    override fun init() {

    }

    override fun initCtrl() {
        binding.btnTopUpNow.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnTopUpNow->{
                findNavController().navigate(R.id.action_accountSuspendedFragment_to_accountSuspendedPaymentFragment)
            }

        }
    }
}