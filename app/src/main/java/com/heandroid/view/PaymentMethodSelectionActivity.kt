package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentPaymentSelectionBinding
import com.heandroid.utils.Constants

class PaymentMethodSelectionActivity : AppCompatActivity() {


    private lateinit var dataBinding: FragmentPaymentSelectionBinding
    private var selectedMode = Constants.QUICK_PAYMENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_payment_selection)
        setView()
    }

    private fun setView() {
        dataBinding.rbQuickPayment.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedMode = Constants.QUICK_PAYMENT
                dataBinding.rbCardPayment.isChecked = !isChecked
                dataBinding.rbBankTransfer.isChecked = !isChecked
                setBtnActivated()
            } else {
                if (!dataBinding.rbQuickPayment.isChecked && !dataBinding.rbBankTransfer.isChecked && !dataBinding.rbCardPayment.isChecked) {
                    setBtnNormal()
                }
            }
        }
        dataBinding.rbCardPayment.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedMode = Constants.CARD_PAYMENT
                dataBinding.rbQuickPayment.isChecked = !isChecked
                dataBinding.rbBankTransfer.isChecked = !isChecked
                setBtnActivated()
            } else {
                if (!dataBinding.rbQuickPayment.isChecked && !dataBinding.rbBankTransfer.isChecked && !dataBinding.rbCardPayment.isChecked) {
                    setBtnNormal()
                }
            }
        }
        dataBinding.rbBankTransfer.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedMode = Constants.BANK_TRANSFER
                dataBinding.rbQuickPayment.isChecked = !isChecked
                dataBinding.rbCardPayment.isChecked = !isChecked
                setBtnActivated()

            } else {
                if (!dataBinding.rbQuickPayment.isChecked && !dataBinding.rbBankTransfer.isChecked && !dataBinding.rbCardPayment.isChecked) {
                    setBtnNormal()
                }
            }
        }

        dataBinding.continueBtn.setOnClickListener {
            var intent = Intent(this, QuickPaymentMethodSelectionActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setBtnActivated() {
        dataBinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        dataBinding.continueBtn.isEnabled = true
    }

    private fun setBtnNormal() {
        dataBinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey))
        dataBinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.black))

        dataBinding.continueBtn.isEnabled = false

    }
}