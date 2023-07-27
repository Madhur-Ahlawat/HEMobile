package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentPaymentMethod2Binding
import com.conduent.nationalhighways.ui.base.BaseFragment

class NewPaymentMethodFragment : BaseFragment<FragmentPaymentMethod2Binding>() {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentMethod2Binding= FragmentPaymentMethod2Binding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}