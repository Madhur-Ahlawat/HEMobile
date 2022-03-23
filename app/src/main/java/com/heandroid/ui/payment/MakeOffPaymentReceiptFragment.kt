package com.heandroid.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentReceiptBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MakeOffPaymentReceiptFragment : BaseFragment<FragmentMakeOffPaymentReceiptBinding>(), View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentReceiptBinding = FragmentMakeOffPaymentReceiptBinding.inflate(inflater,container,false)
    override fun init() {}
    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnContinue -> {
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constants.DATA,arguments?.getParcelableArrayList(Constants.DATA))
                findNavController().navigate(R.id.action_makeOffPaymentReceiptFragment_to_makeOffPaymentCardFragment) }
        }
    }
}