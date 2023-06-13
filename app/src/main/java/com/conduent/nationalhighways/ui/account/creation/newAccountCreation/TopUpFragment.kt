package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class TopUpFragment : BaseFragment<FragmentTopUpBinding>(),View.OnClickListener {




    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTopUpBinding= FragmentTopUpBinding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.topUpBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.topUpBtn->{
                findNavController().navigate(R.id.action_topUpFragment_to_paymentFragment)
            }
        }

    }

}