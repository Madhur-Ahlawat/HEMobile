package com.heandroid.ui.bottomnav.account.payments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentAccountPaymentHistoryFilterBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener

class AccountPaymentHistoryFilterDialog :
    BaseDialog<FragmentAccountPaymentHistoryFilterBinding>(), View.OnClickListener {

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAccountPaymentHistoryFilterBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        setBtnActivated()
    }

    override fun initCtrl() {
        binding.apply {
            applyBtn.setOnClickListener(this@AccountPaymentHistoryFilterDialog)
            closeImage.setOnClickListener(this@AccountPaymentHistoryFilterDialog)
        }
    }

    override fun observer() {
//        lifecycleScope.launch {
//            observe(viewModel.logout,::handleLogout)
//        }
    }

    companion object {
        const val TAG = "PaymentHistoryFilterDialog"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private var mListener: AccountPaymentHistoryFilterListener? = null

        fun newInstance(
            title: String,
            subTitle: String,
            listener: AccountPaymentHistoryFilterListener
        ): AccountPaymentHistoryFilterDialog {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = AccountPaymentHistoryFilterDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private fun setBtnActivated() {
        binding.applyBtnModel = true
    }

    private fun setBtnDisabled() {
        binding.applyBtnModel = false
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
//        dialog?.window?.setBackgroundDrawable(
//            ContextCompat.getDrawable(
//                requireContext(),
//                R.drawable.dialog_background
//            )
//        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.applyBtn -> {
                dialog?.dismiss()
                mListener?.onApplyFilterClick(null, null, null)
            }
            R.id.closeImage -> {
                dialog?.dismiss()
            }
        }
    }
}