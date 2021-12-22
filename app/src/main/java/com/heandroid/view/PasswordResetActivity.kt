package com.heandroid.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.ActivityPasswordResetBinding
import com.heandroid.model.ConfirmationOptionsResponseModel
import com.heandroid.model.SetNewPasswordRequest
import com.heandroid.model.VerifySecurityCodeResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var dataBinding: ActivityPasswordResetBinding
    private var pwd: String = ""
    private var c_pwd: String = ""
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_password_reset)
        setupViewModel()
        sessionManager = SessionManager(this)
        dataBinding.btnSubmit.setOnClickListener {
            if (validate()) {
                // api call to reset password
                callApiToSetNewPassword()
            }
        }

    }

    private fun callApiToSetNewPassword() {
        var accountNumber = sessionManager.fetchAccountNumber()?:""
        var code = sessionManager.fetchCode()?:""
        var requestParam = accountNumber?.let {
            SetNewPasswordRequest(accountNumber ,code , pwd )
        }
        if(requestParam!=null)
        {
            viewModel.setNewPasswordApi(requestParam)
            viewModel.setNewPasswordVal.observe(this, Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var response = resource.data!!.body() as VerifySecurityCodeResponseModel
                            Log.d("SetNewPassword Page:  Response ::", response.toString())
                            Toast.makeText(
                                this,
                                "Password has been changed successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        Status.ERROR -> {
                            Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()

                        }
                        Status.LOADING -> {
                            // show/hide loader
                        }

                    }
                }
            })
        }


    }

    private fun setupViewModel() {

        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun validate(): Boolean {
        pwd = dataBinding.edtPwd.text.toString().trim()
        c_pwd = dataBinding.edtConfirmPwd.text.toString().trim()
        return if (TextUtils.isEmpty(pwd) || pwd.length < 8) {
            Toast.makeText(this, getString(R.string.err_pwd_empty), Toast.LENGTH_SHORT).show()
            false
        } else if (TextUtils.isEmpty(c_pwd) || c_pwd.length < 8) {
            Toast.makeText(this, getString(R.string.err_c_pwd_empty), Toast.LENGTH_SHORT).show()
            false

        } else if (pwd != c_pwd) {
            Toast.makeText(this, getString(R.string.err_pwd_and_c_pwd_not_same), Toast.LENGTH_SHORT)
                .show()
            false

        } else {
            true
        }

    }
}