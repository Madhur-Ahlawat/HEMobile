package com.heandroid.ui.bottomnav.account.payments.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentAccountPaymentHistoryFilterBinding
import com.heandroid.ui.base.BaseFragment

class AccountPaymentHistoryFilterFragment :
    BaseFragment<FragmentAccountPaymentHistoryFilterBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountPaymentHistoryFilterBinding.inflate(inflater, container, false)

    override fun observer() {

    }

    override fun init() {
        binding.apply {

        }
    }

    override fun initCtrl() {
        binding.apply {
            applyBtn.setOnClickListener(this@AccountPaymentHistoryFilterFragment)
            closeImage.setOnClickListener(this@AccountPaymentHistoryFilterFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.applyBtn -> {
            }
            R.id.closeImage -> {
                findNavController().popBackStack()
            }
        }
    }
}