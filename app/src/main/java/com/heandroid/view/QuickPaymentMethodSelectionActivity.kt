package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentQuickPaymentSelectionBinding
import com.heandroid.utils.Constants

class QuickPaymentMethodSelectionActivity : AppCompatActivity() {

    private lateinit var dataBinding: FragmentQuickPaymentSelectionBinding

    private var selectionMode = Constants.APPLE_PAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_quick_payment_selection)
        setView()
    }

    private fun setView() {
        dataBinding.backArrow.setOnClickListener {
            finish()
        }
        dataBinding.continueBtn.setOnClickListener {
            var intent = Intent(this, PayNowActivity::class.java)
            startActivity(intent)
        }
        dataBinding.rbApplePay.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectionMode = Constants.APPLE_PAY
                dataBinding.rbBankGooglePay.isChecked = !isChecked
                dataBinding.rbPayPal.isChecked = !isChecked
                setBtnActivated()
            } else {
                if (!dataBinding.rbApplePay.isChecked && !dataBinding.rbBankGooglePay.isChecked && !dataBinding.rbPayPal.isChecked) {
                    setBtnNormal()
                }
            }
        }
        dataBinding.rbBankGooglePay.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectionMode = Constants.GOOGLE_PAY
                dataBinding.rbApplePay.isChecked = !isChecked
                dataBinding.rbPayPal.isChecked = !isChecked
                setBtnActivated()
            } else {
                if (!dataBinding.rbApplePay.isChecked && !dataBinding.rbBankGooglePay.isChecked && !dataBinding.rbPayPal.isChecked) {
                    setBtnNormal()
                }
            }
        }

        dataBinding.rbPayPal.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectionMode = Constants.PAY_PAL
                dataBinding.rbApplePay.isChecked = !isChecked
                dataBinding.rbBankGooglePay.isChecked = !isChecked
                setBtnActivated()
            } else {
                if (!dataBinding.rbApplePay.isChecked && !dataBinding.rbBankGooglePay.isChecked && !dataBinding.rbPayPal.isChecked) {
                    setBtnNormal()
                }
            }
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