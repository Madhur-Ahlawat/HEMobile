package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.TextureView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.ActivityRecoveryUsernamePasswordBinding
import com.heandroid.model.ForgotUsernameApiResponse
import com.heandroid.model.ForgotUsernameRequest
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.ViewModelFactory

class RecoveryUsernamePasswordActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: DashboardViewModel
    private lateinit var databinding: ActivityRecoveryUsernamePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding =
            DataBindingUtil.setContentView(this, R.layout.activity_recovery_username_password)
        sessionManager = SessionManager(this)
        setupViewModel()
        databinding.lifecycleOwner = this
        setBtnNormal()
        databinding.btnNext.isEnabled = false
        databinding.edtAccountNumber.doOnTextChanged { text, start, before, count ->
            setBtnActivated()
        }
        databinding.btnNext.setOnClickListener {
            hideSoftKeyboard()
            databinding.llEnterDetails.visibility = GONE
            databinding.llUsername.visibility = VISIBLE
            //setUserName()
            if(validation())
            {
                recoverUsername()
            }
        }

        databinding.btnLogin.setOnClickListener {
            var intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

        databinding.backArrow.setOnClickListener {
            finish()
        }


    }

    private fun validation(): Boolean {
        val accountNum = databinding.edtAccountNumber.text.toString().trim()
        val postalCode = databinding.edtPostCode.text.toString().trim()
        if(TextUtils.isEmpty(accountNum) || accountNum.length<3)
        {
            Toast.makeText(this , "Please enter account number" ,Toast.LENGTH_SHORT).show()
            return false
        }

        else if (TextUtils.isEmpty(postalCode))
        {
            Toast.makeText(this , "Please enter postal code" ,Toast.LENGTH_SHORT).show()
            return false
        }
        else
        {
            return true
        }
    }

    private fun recoverUsername() {
    databinding.progressLayout.visibility = VISIBLE
     //   val request = ForgotUsernameRequest(databinding.edtAccountNumber.text.toString().trim() , databinding.edtPostcode.text.toString().trim())
        val request = ForgotUsernameRequest("118489252" , "10002")
        val agencyId = "12"
        viewModel.recoverUsernameApi(agencyId , request)
        viewModel.forgotUsernameVal.observe(this, androidx.lifecycle.Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        databinding.progressLayout.visibility = GONE
                        var forgotUsernameResponse = resource.data!!.body() as ForgotUsernameApiResponse
                        Log.d("Recovery forgot username Page:: Recover username Response ::",forgotUsernameResponse.toString())
                        setUserName(forgotUsernameResponse)
                    }
                    Status.ERROR -> {
                        databinding.progressLayout.visibility = GONE
                        Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()

                    }
                    Status.LOADING -> {
                        databinding.progressLayout.visibility = VISIBLE
                        // show/hide loader
                    }

                }
            }
        })
    }

    private fun setUserName(forgotUsernameResponse: ForgotUsernameApiResponse) {

        // TODO api call to get user name based on account number and postcode

        val username = forgotUsernameResponse.userName
        val buffer:StringBuffer = StringBuffer()
        for (i in username.indices)
        {
            if(i>1 && i< username.length-2 )
            {
                buffer.append("*")
            }
            else
            {
                buffer.append(username[i])
            }
        }
        databinding.tvUsername.visibility = VISIBLE
        databinding.tvUsername.text = buffer.toString()
    }

    private fun setupViewModel() {
        // to do call view model setup
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
    }

    private fun hideSoftKeyboard()
    {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun setBtnActivated() {
        databinding.btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))
        databinding.btnLogin.backgroundTintList= ContextCompat.getColorStateList(this,R.color.btn_color )
        databinding.btnLogin.setTextColor(ContextCompat.getColor(this, R.color.white))
        databinding.btnLogin.isEnabled = true
    }

    private fun setBtnNormal() {
        databinding.btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.color_C9C9C9))
        databinding.btnLogin.setTextColor(ContextCompat.getColor(this, R.color.color_7D7D7D))
        databinding.btnLogin.isEnabled = false

    }
}