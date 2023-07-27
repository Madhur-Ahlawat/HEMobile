package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.databinding.FragmentPaymentMethod2Binding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.payment.adapter.PaymentMethodAdapter

class NewPaymentMethodFragment : BaseFragment<FragmentPaymentMethod2Binding>(),PaymentMethodAdapter.PaymentMethodCallback {
    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    private lateinit var paymentList:ArrayList<String>



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentMethod2Binding= FragmentPaymentMethod2Binding.inflate(inflater,container,false)



    override fun initCtrl() {
        paymentList= ArrayList()

        binding.paymentRecycleView.layoutManager=LinearLayoutManager(requireContext())

        paymentMethodAdapter=PaymentMethodAdapter(requireContext(),paymentList,this)


    }
    override fun init() {
    }

    override fun observer() {
    }

    override fun paymentMethodCallback(position: Int) {
    }
}