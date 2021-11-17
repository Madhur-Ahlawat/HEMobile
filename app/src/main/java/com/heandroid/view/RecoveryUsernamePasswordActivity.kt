package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityLoginBinding
import com.heandroid.databinding.ActivityRecoveryUsernamePasswordBinding
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.LoginViewModel

class RecoveryUsernamePasswordActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: LoginViewModel
    private lateinit var databinding: ActivityRecoveryUsernamePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding =
            DataBindingUtil.setContentView(this, R.layout.activity_recovery_username_password)
        sessionManager = SessionManager(this)
        setupViewModel()
        databinding.lifecycleOwner = this
        databinding.btnNext.setOnClickListener {
            databinding.llEnterDetails.visibility = GONE
            databinding.llUsername.visibility = VISIBLE
            setUserName()
        }

        databinding.btnLogin.setOnClickListener {
            var intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private fun setUserName() {

        // TODO api call to get user name based on account number and postcode

        databinding.tvUsername.text = getString(R.string.txt_username_string , "j****@g***.com")
    }

    private fun setupViewModel() {
        // to do call view model setup

    }
}