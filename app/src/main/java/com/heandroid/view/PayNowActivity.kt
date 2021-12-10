package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentConfirmPaymentBinding

class PayNowActivity : AppCompatActivity() {

    private lateinit var dataBinding: FragmentConfirmPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this , R.layout.fragment_confirm_payment)
        setView()
    }

    private fun setView() {
        dataBinding.btnPayNow.setOnClickListener {
            var intent = Intent(this, ActivityPaypalPage::class.java)
            startActivity(intent)
        }

        dataBinding.backArrow.setOnClickListener {
            finish()
        }

        dataBinding.tvChange.setOnClickListener {
            finish()
        }
    }
}