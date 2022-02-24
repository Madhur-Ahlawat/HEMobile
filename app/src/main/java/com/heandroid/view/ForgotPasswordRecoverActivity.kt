package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.heandroid.R
import com.heandroid.databinding.ActivityForgotPasswordRecoverBinding
import com.heandroid.model.ConfirmationOptionsResponseModel
import com.heandroid.model.GetSecurityCodeRequestModel
import com.heandroid.model.GetSecurityCodeResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Common
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory

class ForgotPasswordRecoverActivity:AppCompatActivity() {

    private var selectedOpt : String = ""
    private lateinit var dataBinding: ActivityForgotPasswordRecoverBinding
    private lateinit var dataFromApi: ConfirmationOptionsResponseModel
    private lateinit var sessionManager: SessionManager
    private var email:String=""
    private var postcode:String = ""
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel
    private var accountNumber = ""
    private var optionVal :String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_password_recover)
        setupViewModel()
        getDataFromIntent()
        sessionManager = SessionManager(this)
        accountNumber = sessionManager.fetchAccountNumber()?:""

        dataBinding.emailRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                selectedOpt = Constants.EMAIL
                optionVal = dataFromApi.email
            }
        }

        dataBinding.textMessageRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                selectedOpt = Constants.MESSAGE
                optionVal = dataFromApi.phone
            }
        }

        dataBinding.postMailRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                selectedOpt = Constants.POST_MAIL
                optionVal=""
            }
        }

        dataBinding.continueBtn.setOnClickListener {

            if(!TextUtils.isEmpty(selectedOpt)) {

                getSecurityCodeApiCall()
            }
            else
            {
                Toast.makeText(this, "Please select one mode for password recovery", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun getSecurityCodeApiCall() {

        var agencyId = "12"

        var requestParam = GetSecurityCodeRequestModel(selectedOpt , optionVal?:"")
        viewModel.getSecurityCodeApi(agencyId ,requestParam)
        viewModel.getSecurityCodeVal.observe(this, Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var response = resource.data!!.body() as GetSecurityCodeResponseModel
                        Log.d("GetSecurityCode Page:  Response ::",response.toString())
                        startVerifySecurityCodeScreen(response)
                        sessionManager.saveCode(response.code.toString()?:"")
                        sessionManager.saveSecurityCodeObject(response)
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()

                    }
                    Status.LOADING -> {
                        // show/hide loader
                    }

                }
            }
        } )

    }

    private fun startVerifySecurityCodeScreen(response: GetSecurityCodeResponseModel?) {
        var intent = Intent(this, ForgotPasswordSentActivity::class.java)
        var bundle = Bundle()
        bundle.putString(Constants.MODE , selectedOpt)
        intent.putExtra(Constants.DATA, bundle)
        startActivity(intent)
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }
    private fun getDataFromIntent() {

        var bundle = intent.getBundleExtra(Constants.DATA)
        bundle?.let {
//            email = bundle.getString(Constants.EMAIL , "")
//            postcode = bundle.getString(Constants.POST_CODE , "")

            dataFromApi = bundle.getSerializable(Constants.OPTIONS ) as ConfirmationOptionsResponseModel
            setView(dataFromApi)

        }

        //Log.d("data","$email $postcode")
    }

    private fun setView(dataFromApi: ConfirmationOptionsResponseModel) {

        val maskMail = Common.maskString(dataFromApi.email, 2, 10, '*')
        var length = dataFromApi.phone.length
        val maskPhone = dataFromApi.phone.substring(length-5 ,length-1)

        dataBinding.emailRadioBtn.text ="Email - $maskMail"
        dataBinding.textMessageRadioBtn.text = "Text message - (xxxx) xxxx -$maskPhone"
        dataBinding.postMailRadioBtn.text = "Post mail - 3113********,Ap***NC,***02"
    }


}