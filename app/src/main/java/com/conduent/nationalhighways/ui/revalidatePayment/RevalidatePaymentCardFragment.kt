package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRevalidatePaymentCardBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class RevalidatePaymentCardFragment : BaseFragment<FragmentRevalidatePaymentCardBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidatePaymentCardBinding = FragmentRevalidatePaymentCardBinding.inflate(inflater, container, false)

    override fun init() {

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}