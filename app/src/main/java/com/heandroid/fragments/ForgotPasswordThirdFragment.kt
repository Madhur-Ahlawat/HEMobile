package com.heandroid.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordThirdBinding
import com.heandroid.model.GetSecurityCodeResponseModel
import com.heandroid.model.VerifySecurityCodeRequestModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import com.heandroid.view.PasswordResetActivity
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_forgot_password_sent.*


/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordThirdFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private lateinit var dataBinding: FragmentForgotPasswordThirdBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(Constants.MODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_third,
            container,
            false
        )
        return dataBinding.root
    }

    private var mType = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (param1 == Constants.EMAIL) {
            dataBinding.topTitle.text = getString(R.string.str_check_your_mail)
            mType = "Email"
        } else {
            dataBinding.topTitle.text = getString(R.string.str_check_text_message)
            mType = "Mobile Number"

        }
        setViewModel()
        setBtnNormal()
        dataBinding.edtOtp.doOnTextChanged { _, _, _, count ->

            if (count > 0)
                setBtnActivated()
            else
                setBtnNormal()
        }

        dataBinding.btnVerify.setOnClickListener {
            if (dataBinding.edtOtp.text!!.isNotEmpty()) {
                Navigation.findNavController(dataBinding.root)
                    .navigate(
                        R.id.action_forgotPasswordThirdFragment_to_forgotPasswordFourthFragment
                    )
            } else {

                Toast.makeText(
                    requireActivity(),
                    "Please enter OTP received to $mType ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        dataBinding.resendTxt.setOnClickListener {

        }
    }

    private fun setBtnActivated() {
        dataBinding.btnVerify.isEnabled = true
        dataBinding.btnVerify.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.white
            )
        )
    }

    private fun setBtnNormal() {
        dataBinding.btnVerify.isEnabled = false
        dataBinding.btnVerify.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.color_7D7D7D
            )
        )
    }

    private fun callApiToVerifyCode() {
        if (!TextUtils.isEmpty(dataBinding.edtOtp.text.toString())) {
            var securityCodeObj = sessionManager.fetchSecurityCodeObj()
            if (securityCodeObj != null) {

                var requestParam = VerifySecurityCodeRequestModel(
                    edt_code.text.toString(),
                    securityCodeObj.otpExpiryInSeconds,
                    securityCodeObj.referenceId,
                    true
                )
                if (requestParam != null) {
                    viewModel.verifySecurityCodeApi(requestParam)

                    viewModel.getSecurityCodeVal.observe(requireActivity(), Observer {
                        it.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    var response =
                                        resource.data!!.body() as GetSecurityCodeResponseModel
                                    Log.d(
                                        "VerifySecurityCode Page:  Response ::",
                                        response.toString()
                                    )

                                }
                                Status.ERROR -> {
                                    Toast.makeText(
                                        requireActivity(),
                                        resource.message,
                                        Toast.LENGTH_LONG
                                    ).show()

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


    private fun setViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }


}