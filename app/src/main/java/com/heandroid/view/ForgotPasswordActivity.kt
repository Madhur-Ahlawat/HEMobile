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
import com.heandroid.R
import com.heandroid.databinding.ActivityForgotPasswordBinding
import com.heandroid.model.ConfirmationOptionRequestModel
import com.heandroid.model.ConfirmationOptionsResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import com.heandroid.utils.Utils
import com.heandroid.viewmodel.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: RecoveryUsernamePasswordViewModel
    private lateinit var dataBinding: ActivityForgotPasswordBinding
    private  var selectedMode:String = ""
    private var email:String =""
    private var postcode:String = ""
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this , R.layout.activity_forgot_password)
        selectedMode = intent.getStringExtra(Constants.MODE).toString()
        Log.d("selectedMode1", selectedMode)
        sessionManager = SessionManager((this))
        setupViewModel()
        dataBinding.nextBtn.setOnClickListener {

            hideSoftKeyboard()
            Log.d("SelectMode2", selectedMode)
            if(validate())
            {
                sessionManager.saveAccountNumber(email)
//                var dEmail = email
//                var dPostcode = postcode
//                var bundle = Bundle()
//                bundle.putString(Constants.EMAIL, dEmail)
//                bundle.putString(Constants.POST_CODE, dPostcode)
//                val mIntent = Intent(this, ForgotPasswordRecoverActivity::class.java)
//                intent.putExtra(Constants.DATA,bundle)
//                startActivity(mIntent)

                callApiForGettingConfirmationOptions()
            }

        }

        dataBinding.backArrow.setOnClickListener {
            finish()
        }

        dataBinding.tvBack.setOnClickListener {
            finish()
        }


    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun callApiForGettingConfirmationOptions() {

        var agencyId = "12"
        var requestParam = ConfirmationOptionRequestModel(email , postcode)
        viewModel.getConfirmationOptionsApi(agencyId , requestParam)
        viewModel.confirmationOptionVal.observe(this, androidx.lifecycle.Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var response = resource.data!!.body() as ConfirmationOptionsResponseModel
                        Log.d("ForgotPassword Page:  Response ::",response.toString())
                        startPasswordRecoveryOptionSelectionScreen(response)
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

    private fun startPasswordRecoveryOptionSelectionScreen(response: ConfirmationOptionsResponseModel) {
                var bundle = Bundle()
               // bundle.putString(Constants.EMAIL, dEmail)
                //bundle.putString(Constants.POST_CODE, dPostcode)
                bundle.putSerializable(Constants.OPTIONS , response )
                val mIntent = Intent(this, ForgotPasswordRecoverActivity::class.java)
                mIntent.putExtra(Constants.DATA,bundle)
                startActivity(mIntent)

    }


    private fun validate():Boolean
    {
        email = dataBinding.edtEmail.text.toString().trim()
        postcode = dataBinding.idPostcodeInput.text.toString().trim()

        //if(TextUtils.isEmpty(email) || email.length<3 || !Utils.isEmailValid(email))
        if(TextUtils.isEmpty(email) || email.length<3)
        {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show()
            return false;

        }

        else if(TextUtils.isEmpty(postcode))
        {
            Toast.makeText(this, "Please enter postcode", Toast.LENGTH_SHORT).show()
            return false;
        }

        else
        {
            return true;
        }
    }

    private fun hideSoftKeyboard()
    {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}