package com.heandroid.ui.vehicle.payment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityMakePaymentBinding

class MakeOneOffPayment : AppCompatActivity() {

    private lateinit var databinding: ActivityMakePaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_make_payment)
        setUpViewAction()
        setBtnActivated()
    }

    private fun setUpViewAction() {
        databinding.idToolBarLyt.titleTxt.text = getString(R.string.str_make_one_of_payment)

        databinding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }

//        databinding.continueBtn.setOnClickListener {
//            val intent = Intent(this, OneOfPayment::class.java)
//            startActivity(intent)
//        }
    }

    private fun setBtnActivated() {
        databinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        databinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        databinding.continueBtn.isEnabled = true
    }

    private fun setBtnNormal() {
        databinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey))
        databinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.black))

        databinding.continueBtn.isEnabled = false

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}