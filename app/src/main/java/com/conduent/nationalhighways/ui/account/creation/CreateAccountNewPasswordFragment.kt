package com.conduent.nationalhighways.ui.account.creation


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentCreateAccountResendCodeBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class CreateAccountNewPasswordFragment : BaseFragment<FragmentCreateAccountResendCodeBinding>(),
View.OnClickListener {



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateAccountResendCodeBinding =
        FragmentCreateAccountResendCodeBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
    }

}