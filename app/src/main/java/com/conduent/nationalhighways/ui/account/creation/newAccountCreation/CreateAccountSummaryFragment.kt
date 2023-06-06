package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.CreateAccountSummaryBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class CreateAccountSummaryFragment : BaseFragment<CreateAccountSummaryBinding>(),
    View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CreateAccountSummaryBinding= CreateAccountSummaryBinding.inflate(inflater,container,false)

    override fun init() {
        binding.btnNext.setOnClickListener(this)

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnNext->{

            }
        }
    }

}