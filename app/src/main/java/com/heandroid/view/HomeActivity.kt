package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityHomeBinding
import android.text.Spanned

import android.graphics.Typeface

import android.text.style.StyleSpan

import android.text.SpannableString
import androidx.core.content.ContextCompat


class HomeActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityHomeBinding
    private var TAG = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setUpViews()
        setBtnNormal()

    }

    private fun setBtnActivated() {
        dataBinding.btnContinue.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.btnContinue.setTextColor(ContextCompat.getColor(this, R.color.white))
        dataBinding.btnContinue.isEnabled = true
    }

    private fun setBtnNormal() {
        dataBinding.btnContinue.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey))
        dataBinding.btnContinue.setTextColor(ContextCompat.getColor(this, R.color.black))

        dataBinding.btnContinue.isEnabled = false

    }

    private fun setUpViews() {

        dataBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            setBtnActivated()
            Log.v(TAG, "checked called checkedId $checkedId")

            when (checkedId) {

                R.id.id_create_account -> {
                    Log.v(TAG, "checked id_create_account id called")
                    dataBinding.idOneOfPayment.text = getString(R.string.str_make_one_of_payment)

                }

                R.id.id_one_of_payment -> {
                    val ss = SpannableString(getString(R.string.str_make_one_of_payment_continue))
                    val boldSpan = StyleSpan(Typeface.BOLD)
                    ss.setSpan(boldSpan, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    dataBinding.idOneOfPayment.text = ss

                    Log.v(TAG, "checked id_one_of_payment id called")

                }
                R.id.id_resolve_penalty -> {
                    dataBinding.idOneOfPayment.text = getString(R.string.str_make_one_of_payment)

                    Log.v(TAG, "checked id_resolve_penalty id called")

                }
                R.id.id_check_for_paid -> {
                    dataBinding.idOneOfPayment.text = getString(R.string.str_make_one_of_payment)

                    Log.v(TAG, "checked id_check_for_paid id called")

                }
                R.id.id_view_charges -> {
                    dataBinding.idOneOfPayment.text = getString(R.string.str_make_one_of_payment)

                    Log.v(TAG, "checked id_view_charges id called")

                }


            }


        }

        dataBinding.btnContinue.setOnClickListener {

            val intent = Intent(this, ActivityPaymentStartPage::class.java)
            startActivity(intent)
        }

    }
}