package com.heandroid.view

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentPaymentSuccessBinding

class ConfirmPaymentActivity : AppCompatActivity() {

    private lateinit var dataBinding: FragmentPaymentSuccessBinding
    private val TAG= ConfirmPaymentActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_payment_success)
        setView()
    }

    private fun setView() {
        dataBinding.rg.setOnCheckedChangeListener { group, checkedId ->
            setBtnActivated()
            Log.v(TAG, "checked called checkedId $checkedId")

            when (checkedId) {

                R.id.rb_create_account -> {
                    Log.v(TAG, "checked id_create_account id called")


                }


                R.id.rb_make_another_payment -> {
                    Log.v(TAG, "checked id_check_for_paid id called")

                }


            }


        }

        dataBinding.btnContinue.setOnClickListener {
            Log.d(TAG , "btn clicked")
            var intent = Intent(this, ActivityHome::class.java)
            startActivity(intent)
        }

        dataBinding.backArrow.setOnClickListener {
            finish()
        }
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

}