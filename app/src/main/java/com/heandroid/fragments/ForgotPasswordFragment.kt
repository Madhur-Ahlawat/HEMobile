package com.heandroid.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordBinding
import com.heandroid.model.ConfirmationOptionRequestModel
import com.heandroid.model.ConfirmationOptionsResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory

class ForgotPasswordFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentForgotPasswordBinding
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel
    private lateinit var sessionManager: SessionManager
    private var email: String = ""
    private var postcode: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)

        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        sessionManager = SessionManager((requireActivity()))

        dataBinding.edtPostcode.doOnTextChanged { _, _, _, _ ->
            if (dataBinding.edtEmail.text!!.isNotEmpty() && dataBinding.edtPostcode.text!!.isNotEmpty())
                setBtnActivated()
            else
                setBtnNormal()
        }
        dataBinding.edtEmail.doOnTextChanged { _, _, _, _ ->

            if (dataBinding.edtEmail.text!!.isNotEmpty() && dataBinding.edtPostcode.text!!.isNotEmpty())
                setBtnActivated()
            else
                setBtnNormal()
        }
        dataBinding.btnNext.setOnClickListener {

            hideSoftKeyboard()
            if (validate()) {
                sessionManager.saveAccountNumber(email)
                val response = ConfirmationOptionsResponseModel(
                    "4294274",
                    "christoper@gmail.com",
                    "9823233232"
                )
                startPasswordRecoveryOptionSelectionScreen(response)
//                callApiForGettingConfirmationOptions()
            }

        }

    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setBtnActivated() {
        dataBinding.btnNext.isEnabled = true
        dataBinding.btnNext.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
    }

    private fun setBtnNormal() {
        dataBinding.btnNext.isEnabled = false
        dataBinding.btnNext.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.color_7D7D7D
            )
        )
    }


    private fun callApiForGettingConfirmationOptions() {

        var agencyId = "12"
        var requestParam = ConfirmationOptionRequestModel(email, postcode)
        viewModel.getConfirmationOptionsApi(agencyId, requestParam)
        viewModel.confirmationOptionVal.observe(requireActivity(), androidx.lifecycle.Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var response = resource.data!!.body() as ConfirmationOptionsResponseModel
                        Log.d("ForgotPassword Page:  Response ::", response.toString())
                        startPasswordRecoveryOptionSelectionScreen(response)
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireActivity(), resource.message, Toast.LENGTH_LONG)
                            .show()

                    }
                    Status.LOADING -> {
                        // show/hide loader
                    }

                }
            }
        })

    }

    private fun startPasswordRecoveryOptionSelectionScreen(response: ConfirmationOptionsResponseModel) {
        val bundle = Bundle()

        bundle.putSerializable(Constants.OPTIONS, response)

        Navigation.findNavController(dataBinding.root)
            .navigate(R.id.action_forgotPasswordFragment_to_forgotPasswordSecondFragment, bundle)

    }


    private fun validate(): Boolean {
        email = dataBinding.edtEmail.text.toString().trim()
        postcode = dataBinding.edtPostcode.text.toString().trim()

        //if(TextUtils.isEmpty(email) || email.length<3 || !Utils.isEmailValid(email))
        return if (TextUtils.isEmpty(email) || email.length < 3) {
            Toast.makeText(requireActivity(), "Please enter email address", Toast.LENGTH_SHORT)
                .show()
            false

        } else if (TextUtils.isEmpty(postcode)) {
            Toast.makeText(requireActivity(), "Please enter postcode", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun hideSoftKeyboard() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

}