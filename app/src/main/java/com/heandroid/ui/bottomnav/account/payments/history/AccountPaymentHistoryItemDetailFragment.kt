package com.heandroid.ui.bottomnav.account.payments.accountpaymenthistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.accountpayment.TransactionData
import com.heandroid.databinding.AccountPaymentHistoryItemDetailBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.Constants

class AccountPaymentHistoryItemDetailFragment :
    BaseFragment<AccountPaymentHistoryItemDetailBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AccountPaymentHistoryItemDetailBinding.inflate(inflater, container, false)

    override fun observer() {}

    override fun init() {
        arguments?.getParcelable<TransactionData?>(Constants.DATA)?.let { tData ->
            binding.data = tData
            binding.paymentDate.text = DateUtils.convertDateFormat(tData.transactionDate, 0)
        }
    }

    override fun initCtrl() {
        binding.apply {
            downloadReceiptBtn.setOnClickListener(this@AccountPaymentHistoryItemDetailFragment)
            backBtn.setOnClickListener(this@AccountPaymentHistoryItemDetailFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.downloadReceiptBtn -> {

            }
            R.id.backBtn -> {
                findNavController().popBackStack()
            }
        }
    }
}