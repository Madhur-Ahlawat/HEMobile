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
import com.heandroid.databinding.HomeActivityBinding
import com.heandroid.utils.Constants
import kotlinx.android.synthetic.main.toolbar_with_logo.view.*

class ActivityHome : AppCompatActivity() {

    private lateinit var dataBinding: HomeActivityBinding
    private var TAG = "HomeActivity"
    private var selOption = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.home_activity)
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
                    selOption = Constants.CREATE_ACCOUNT

                }

                R.id.id_one_of_payment -> {
                    val ss = SpannableString(getString(R.string.str_make_one_of_payment_continue))
                    val boldSpan = StyleSpan(Typeface.BOLD)
                    ss.setSpan(boldSpan, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    selOption = Constants.ONE_OF_PAYMENT

                    dataBinding.idOneOfPayment.text = ss

                    Log.v(TAG, "checked id_one_of_payment id called")

                }
                R.id.id_resolve_penalty -> {
                    dataBinding.idOneOfPayment.text = getString(R.string.str_make_one_of_payment)
                    selOption = Constants.RESOLVE_PENALTY

                    Log.v(TAG, "checked id_resolve_penalty id called")

                }
                R.id.id_check_for_paid -> {
                    dataBinding.idOneOfPayment.text = getString(R.string.str_make_one_of_payment)
                    selOption = Constants.CHECK_FOR_PAID

                    Log.v(TAG, "checked id_check_for_paid id called")

                }
                R.id.id_view_charges -> {
                    dataBinding.idOneOfPayment.text = getString(R.string.str_make_one_of_payment)
                    selOption = Constants.VIEW_CHARGES

                    Log.v(TAG, "checked id_view_charges id called")

                }


            }


        }

        dataBinding.btnContinue.setOnClickListener {

            when (selOption) {
                Constants.VIEW_CHARGES -> {
                    val intent = Intent(this, ActivityViewCharges::class.java)
                    startActivity(intent)

                }
                Constants.CHECK_FOR_PAID -> {

                }
                Constants.RESOLVE_PENALTY -> {

                }
                Constants.ONE_OF_PAYMENT -> {
                    val intent = Intent(this, PaymentStartPage::class.java)
                    startActivity(intent)

                }
                Constants.CREATE_ACCOUNT -> {

                }


            }
        }

        dataBinding.idToolBarLyt.btn_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

    }

}