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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordSecondBinding
import com.heandroid.model.ConfirmationOptionsResponseModel
import com.heandroid.model.GetSecurityCodeRequestModel
import com.heandroid.model.GetSecurityCodeResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Common
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.utils.SessionManager
import com.heandroid.view.ForgotPasswordSentActivity
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordSecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordSecondFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var resp: ConfirmationOptionsResponseModel? = null

    private lateinit var dataBinding: FragmentForgotPasswordSecondBinding
    private var selectedOpt: String = ""

    private lateinit var sessionManager: SessionManager
    private var email: String = ""
    private var postcode: String = ""
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel
    private var accountNumber = ""
    private var optionVal: String = ""

    private val TAG = "ForgotPasswordSecondFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_second,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            resp = it.getSerializable(Constants.OPTIONS) as ConfirmationOptionsResponseModel
        }
        setupViewModel()
        setBtnNormal()
        sessionManager = SessionManager(requireActivity())
        accountNumber = sessionManager.fetchAccountNumber() ?: ""

        dataBinding.emailRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedOpt = Constants.EMAIL
                optionVal = resp!!.email
                setBtnActivated()
            }
        }

        dataBinding.textMessageRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedOpt = Constants.MESSAGE
                optionVal = resp!!.phone
                setBtnActivated()
            }
        }

        dataBinding.postMailRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedOpt = Constants.POST_MAIL
                optionVal = ""
                setBtnActivated()
            }
        }

        dataBinding.continueBtn.setOnClickListener {

            if (!TextUtils.isEmpty(selectedOpt)) {

                val response = GetSecurityCodeResponseModel("345677", 30, "qweerty", true)
                startVerifySecurityCodeScreen(response)
                sessionManager.saveCode(response.code)
                sessionManager.saveSecurityCodeObject(response)

//                getSecurityCodeApiCall()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Please select one mode for password recovery",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        setView(resp!!)

        Logg.logging(TAG, " response  $resp")

    }

    private fun setBtnActivated() {
        dataBinding.continueBtn.isEnabled = true
        dataBinding.continueBtn.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.white
            )
        )
    }

    private fun setBtnNormal() {
        dataBinding.continueBtn.isEnabled = false
        dataBinding.continueBtn.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.color_7D7D7D
            )
        )
    }

    private fun getSecurityCodeApiCall() {

        var agencyId = "12"

        var requestParam = GetSecurityCodeRequestModel(accountNumber, selectedOpt, optionVal)
        viewModel.getSecurityCodeApi(agencyId, requestParam)
        viewModel.getSecurityCodeVal.observe(requireActivity(), Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var response = resource.data!!.body() as GetSecurityCodeResponseModel
                        Log.d("GetSecurityCode Page:  Response ::", response.toString())
                        startVerifySecurityCodeScreen(response)
                        sessionManager.saveCode(response.code)
                        sessionManager.saveSecurityCodeObject(response)
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

    private fun startVerifySecurityCodeScreen(response: GetSecurityCodeResponseModel?) {
        if (selectedOpt == Constants.POST_MAIL) {

            val bundle = Bundle()
            bundle.putString(Constants.MODE, selectedOpt)
            Navigation.findNavController(dataBinding.root)
                .navigate(
                    R.id.action_forgotPasswordSecondFragment_to_forgotPasswordPostalFragment,
                    bundle
                )

        } else {
            val bundle = Bundle()
            bundle.putString(Constants.MODE, selectedOpt)
            Navigation.findNavController(dataBinding.root)
                .navigate(
                    R.id.action_forgotPasswordSecondFragment_to_forgotPasswordThirdFragment,
                    bundle
                )

        }

    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setView(dataFromApi: ConfirmationOptionsResponseModel) {

        val maskMail = Common.maskString(dataFromApi.email, 2, 10, '*')
        var length = dataFromApi.phone.length
        val maskPhone = dataFromApi.phone.substring(length - 5, length - 1)

        dataBinding.emailRadioBtn.text = "Email - $maskMail"
        dataBinding.textMessageRadioBtn.text = "Text message - (xxxx) xxxx -$maskPhone"
        dataBinding.postMailRadioBtn.text = "Post mail - 3113********,Ap***NC,***02"
    }
}