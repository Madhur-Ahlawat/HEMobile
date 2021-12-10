package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityPaypalPageBinding

class ActivityPaypalPage : AppCompatActivity() {

    lateinit var dataBinding: ActivityPaypalPageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_paypal_page)
        setUpViews()
    }

    private fun setUpViews() {
        dataBinding.confirmBtn.setOnClickListener {
            val intent = Intent(this, ConfirmPaymentActivity::class.java)
//            intent.putExtra("list", mVehicleDetails)
            startActivity(intent)


        }


    }
}