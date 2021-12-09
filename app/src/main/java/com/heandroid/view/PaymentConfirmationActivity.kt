package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentConfirmPaymentBinding

class PaymentConfirmationActivity : AppCompatActivity() {

    private lateinit var dataBinding: FragmentConfirmPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this , R.layout.fragment_confirm_payment)
    }
}