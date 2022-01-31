package com.heandroid.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.ActivityForgotPasswordSentBinding
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Common
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_forgot_password_sent.*


class ForgotPasswordSentActivity : AppCompatActivity() {

    private lateinit var dataFromApi: GetSecurityCodeResponseModel
    private lateinit var dataBinding: ActivityForgotPasswordSentBinding
    private lateinit var sessionManager: SessionManager
    private var selectedMode: String = ""
    private var mode: Int = 0
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, (R.layout.activity_forgot_password_sent))
        sessionManager = SessionManager(this)
        setViewModel()
        getDataFromIntent()
        Log.d("selectedMode3 : ", selectedMode)
//        val mail_id = "cherukuri13@gmail.com"
//
//        val maskMail = Common.maskString(mail_id, 2, 10, '*')
//
//        dataBinding.mailedPasswordResetLink.text ="We have mailed the password reset link to $maskMail  to reset your password "

        dataBinding.backArrow.setOnClickListener {
            finish()
        }
        dataBinding.tvBack.setOnClickListener {
            finish()
        }
        setView()
        btn_verify.setOnClickListener {

            when {

                mode == Constants.EMAIL_MODE && selectedMode == Constants.EMAIL -> {
                    callApiToVerifyCode()
                    var intent = Intent(this, PasswordResetActivity::class.java)
                    startActivity(intent)
                }
                mode == Constants.MESSAGE_MODE && selectedMode == Constants.MESSAGE -> {
                    callApiToVerifyCode()
                    var intent = Intent(this, PasswordResetActivity::class.java)
                    startActivity(intent)
                }
                mode == Constants.POST_MAIL_MODE && selectedMode == Constants.POST_MAIL -> {
                    // do nothing
                    var intent = Intent(this, PasswordResetActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }

    private fun callApiToVerifyCode() {
        if(!TextUtils.isEmpty(edt_code.text.toString()))
        {
            var securityCodeObj = sessionManager.fetchSecurityCodeObj()
            if(securityCodeObj!=null)
            {

                var requestParam =  VerifySecurityCodeRequestModel(edt_code.text.toString() , securityCodeObj.otpExpiryInSeconds,securityCodeObj.referenceId, true)
                if (requestParam != null) {
                    viewModel.verifySecurityCodeApi(requestParam)

                    viewModel.getSecurityCodeVal.observe(this, Observer {
                        it.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    var response = resource.data!!.body() as GetSecurityCodeResponseModel
                                    Log.d("VerifySecurityCode Page:  Response ::", response.toString())
                                    var intent = Intent(this, PasswordResetActivity::class.java)
                                    startActivity(intent)
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
                    }


    }

    private fun getDataFromIntent() {

        var bundle = intent.getBundleExtra(Constants.DATA)
        bundle?.let {
//            email = bundle.getString(Constants.EMAIL , "")
//            postcode = bundle.getString(Constants.POST_CODE , "")

            selectedMode = bundle.getString(Constants.MODE, "")
            setView()

        }

    }

    private fun setViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }


    private fun setView() {
        dataBinding.title3.visibility = GONE
        dataBinding.title4.visibility = GONE
        dataBinding.title5.visibility = GONE
        when (selectedMode) {
            Constants.EMAIL -> {
                mode = Constants.EMAIL_MODE
                val mail_id = "cherukuri13@gmail.com"
                val maskMail = Common.maskString(mail_id, 2, 10, '*')
                dataBinding.title1.text = getString(R.string.str_check_your_mail)
                var normalTxt = getString(R.string.str_not_rcvd_security_code_in_mail)
                var clickableTxt = getString(R.string.str_resend_security_code)
                var dataString = "$normalTxt $clickableTxt"
                val spannableString = SpannableStringBuilder("$normalTxt $clickableTxt")
                val blackClr = ForegroundColorSpan(Color.BLACK)
                val blueClr = ForegroundColorSpan(Color.BLUE)
                var start = normalTxt.length
                var end = clickableTxt.length
                spannableString.setSpan(ForegroundColorSpan(Color.BLUE),
                    normalTxt.length, (dataString.length-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                val string = SpannableString("Text with a background color span")
                string.setSpan(ForegroundColorSpan(Color.RED), 12, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                dataBinding.title2.text = spannableString.toString()
                //dataBinding.title2.text = string




            }

            Constants.MESSAGE -> {
                val mail_id = "cherukuri13@gmail.com"
                mode = Constants.MESSAGE_MODE
                dataBinding.title1.text = getString(R.string.str_check_text_message)
               // dataBinding.title2.text = getString(R.string.str_not_rcvd_security_code_in_message)
                var normalTxt = getString(R.string.str_not_rcvd_security_code_in_message)
                var clickableTxt = getString(R.string.str_resend_security_code)
                var dataString = "$normalTxt $clickableTxt"
                val spannableString = SpannableStringBuilder("$normalTxt $clickableTxt")
                val blackClr = ForegroundColorSpan(Color.BLACK)
                val blueClr = ForegroundColorSpan(Color.BLUE)
                var start = normalTxt.length
                var end = clickableTxt.length
                spannableString.setSpan(ForegroundColorSpan(Color.BLUE),
                    normalTxt.length, (dataString.length-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                val string = SpannableString("Text with a background color span")
                string.setSpan(ForegroundColorSpan(Color.RED), 12, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                dataBinding.title2.text = spannableString.toString()


            }

            Constants.POST_MAIL -> {
                val mail_id = "cherukuri13@gmail.com"
                mode = Constants.POST_MAIL_MODE
                dataBinding.title1.text = getString(R.string.str_check_post)
                dataBinding.title2.text = getString(R.string.str_letter_posted_txt)
                dataBinding.title3.visibility = VISIBLE
                dataBinding.title4.visibility = VISIBLE
                dataBinding.title5.visibility = VISIBLE
                dataBinding.title3.text = getString(R.string.str_letter_not_received)
                dataBinding.title4.text = getString(R.string.str_dart_change_contact)
                dataBinding.title5.text = getString(R.string.str_contact_address)
                dataBinding.btnVerify.text = getString(R.string.str_accept)

            }
        }
    }
}