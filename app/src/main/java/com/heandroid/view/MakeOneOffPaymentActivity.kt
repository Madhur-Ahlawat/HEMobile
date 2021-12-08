package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOneOffPaymentBinding

class MakeOneOffPaymentActivity : AppCompatActivity() {

    private lateinit var dataBinding: FragmentMakeOneOffPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_make_one_off_payment)
    }
}