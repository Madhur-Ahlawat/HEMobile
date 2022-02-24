package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.ActivityForgotPasswordBinding
import com.heandroid.model.ConfirmationOptionRequestModel
import com.heandroid.model.ConfirmationOptionsResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.utils.SessionManager
import com.heandroid.utils.Utils
import com.heandroid.viewmodel.*
import kotlinx.android.synthetic.main.tool_bar_white.view.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityForgotPasswordBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this , R.layout.activity_forgot_password)
        sessionManager = SessionManager((this))

        dataBinding.toolBarLyt.btnBack.setOnClickListener {
            Logg.logging("Forgot","  back button called  ${dataBinding.fragmentContainerView.childCount}")

            onBackPressed()

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Logg.logging("Forgot","  onBackPressed called  ${dataBinding.fragmentContainerView.childCount}")

    }



}