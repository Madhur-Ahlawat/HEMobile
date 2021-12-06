package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.heandroid.R
import com.heandroid.databinding.ActivityOneOfPaymentBinding
import kotlinx.android.synthetic.main.tool_bar_title_back_btn.view.*

class ActivityOneOfPayment : AppCompatActivity() {

    private lateinit var databinding: ActivityOneOfPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this,R.layout.activity_one_of_payment)
        setUpViewAction()
        setBtnNormal()
    }

    private fun setUpViewAction() {

        databinding.idToolBarLyt.back_button.setOnClickListener {
            onBackPressed()
        }
        databinding.continueBtn.setOnClickListener {
            val intent = Intent(this, ActivityOneOfPayment::class.java)
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