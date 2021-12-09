package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeSelctionPaymentRcptMethodBinding
import com.heandroid.utils.Utils

class PaymentReceiptSelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var dataBinding: FragmentMakeSelctionPaymentRcptMethodBinding
    private var selectedMode: String = "Email"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(
            this,
            R.layout.fragment_make_selction_payment_rcpt_method
        )
        setView()
    }

    private fun setView() {
        dataBinding.continueBtn.isEnabled = false
        val resource = arrayOf("Email", "Phone")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, resource
        )

        dataBinding.spinner.adapter = adapter
        dataBinding.spinner.onItemSelectedListener=this
        dataBinding.cbDeclare.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                dataBinding.continueBtn.isEnabled = true
                setBtnActivated()
            }
            else
            {
                setBtnNormal()
            }
        }

        dataBinding.continueBtn.setOnClickListener {
            if(selectedMode=="Email")
            {
                if(emailValidation())
                {
                    var intent = Intent(this, PaymentMethodSelectionActivity::class.java)
                    startActivity(intent)
                }
            }

            else if (selectedMode=="Phone")
            {
                if(phoneValidation())
                {
                    var intent = Intent(this, PaymentMethodSelectionActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    private fun emailValidation(): Boolean {
        var email = dataBinding.edtEmail.text.toString().trim()
        var cEmail = dataBinding.edtConfirmEmail.text.toString().trim()
        if(TextUtils.isEmpty(email)|| !Utils.isEmailValid(email))
        {
            showToast(getString(R.string.plz_enter_email))
            return false
        }
        else if(TextUtils.isEmpty(cEmail)|| !Utils.isEmailValid(cEmail))
        {
            showToast(getString(R.string.plz_enter_confirm_email))
            return false
        }
        else if(email!=cEmail)
        {
            showToast(getString(R.string.email_and_confirm_email_not_same))
            return false
        }

        else
        {
            return true
        }

    }

    private fun phoneValidation(): Boolean {
        var phone = dataBinding.edtPhone.text.toString().trim()
        var cPhone = dataBinding.edtConfirmPhone.text.toString().trim()
        if(TextUtils.isEmpty(phone))
        {
            showToast(getString(R.string.plz_enter_phone))
            return false
        }
        else if(TextUtils.isEmpty(cPhone))
        {
            showToast(getString(R.string.plz_enter_confirm_phone))
            return false
        }
        else if(phone!=cPhone)
        {
            showToast(getString(R.string.phone_and_confirm_phone_not_same))
            return false
        }

        else
        {
            return true
        }

    }

    private fun showToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }


    private fun setBtnActivated() {
        dataBinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        dataBinding.continueBtn.isEnabled = true
    }

    private fun setBtnNormal() {
        dataBinding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey))
        dataBinding.continueBtn.setTextColor(ContextCompat.getColor(this, R.color.black))

        dataBinding.continueBtn.isEnabled = false

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                selectedMode = "Email"
                setViewForEmail()
            }

            1 -> {
                selectedMode = "Phone"
                setViewForPhone()
            }
        }
    }

    private fun setViewForEmail() {

        dataBinding.llEmail.visibility =VISIBLE
        dataBinding.llPhone.visibility =GONE
    }

    private fun setViewForPhone() {

        dataBinding.llPhone.visibility = VISIBLE
        dataBinding.llEmail.visibility = GONE
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
           selectedMode = "Email"
    }
}