package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.heandroid.R
import com.heandroid.databinding.OneOfPaymentActivityBinding
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class OneOfPayment : AppCompatActivity() {

    private lateinit var databinding: OneOfPaymentActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this,R.layout.one_of_payment_activity)
        setUpViewAction()
        setBtnNormal()
    }

    private fun setUpViewAction() {

        databinding.idToolBarLyt.back_button.setOnClickListener {
            onBackPressed()
        }
        databinding.continueBtn.setOnClickListener {
            val intent = Intent(this, OneOfPayment::class.java)
            startActivity(intent)
        }

    }

    private fun setBtnActivated() {
        databinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        databinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        databinding.continueBtn.isEnabled = true
    }

    private fun setBtnNormal() {
        databinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        databinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.black))

        databinding.continueBtn.isEnabled = false

    }

}