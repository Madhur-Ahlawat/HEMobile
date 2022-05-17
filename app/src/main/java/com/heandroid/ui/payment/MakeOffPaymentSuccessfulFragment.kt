package com.heandroid.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentMakeOffPaymentSuccessfulBinding
import com.heandroid.ui.base.BaseFragment

class MakeOffPaymentSuccessfulFragment : BaseFragment<FragmentMakeOffPaymentSuccessfulBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentSuccessfulBinding = FragmentMakeOffPaymentSuccessfulBinding.inflate(inflater,container,false)
    override fun init() {}
    override fun initCtrl() {}
    override fun observer() {}
}