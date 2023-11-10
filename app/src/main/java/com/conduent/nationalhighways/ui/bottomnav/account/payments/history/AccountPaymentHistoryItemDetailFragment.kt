package com.conduent.nationalhighways.ui.bottomnav.account.payments.history


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.AccountPaymentHistoryItemDetailBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.AccountPaymentActivity
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants

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
        if (requireActivity() is AccountPaymentActivity) {
            (requireActivity() as AccountPaymentActivity).hideTabLayout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (requireActivity() is AccountPaymentActivity) {
            (requireActivity() as AccountPaymentActivity).showTabLayout()
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