package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAccountSuccessfullyCreationBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class AccountSuccessfullyCreationFragment :
    BaseFragment<FragmentAccountSuccessfullyCreationBinding>(), View.OnClickListener {




    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuccessfullyCreationBinding =
        FragmentAccountSuccessfullyCreationBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.signIn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.signIn->{

            }
        }
    }


}