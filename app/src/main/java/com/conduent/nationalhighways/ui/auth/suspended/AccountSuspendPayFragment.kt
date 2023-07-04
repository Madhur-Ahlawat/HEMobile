package com.conduent.nationalhighways.ui.auth.suspended

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendPayBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class AccountSuspendPayFragment : BaseFragment<FragmentAccountSuspendPayBinding>(),View.OnClickListener {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendPayBinding =
        FragmentAccountSuspendPayBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.btnPay.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnPay->{

            }
            R.id.btnCancel->{

            }
        }
    }

}