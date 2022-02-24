package com.heandroid.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordFourthBinding
import com.heandroid.gone
import com.heandroid.model.GetSecurityCodeResponseModel
import com.heandroid.model.SetNewPasswordRequest
import com.heandroid.model.VerifySecurityCodeResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.visible

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordFourthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordFourthFragment : BaseFragment() {
    // TODO: Rename and change types of parameters

    private lateinit var sessionManager: SessionManager
    private lateinit var dataBinding: FragmentForgotPasswordFourthBinding
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel
    private var pwd: String = ""
    private var c_pwd: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_fourth,
            container,
            false
        )

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        dataBinding.btnSubmit.setOnClickListener {

            if (validate()) {
                dataBinding.btnSubmit.isEnabled=false
                dataBinding.progressBar.visible()
                callApiToSetNewPassword()
            }
        }

        dataBinding.edtNewPassword.doOnTextChanged { char, _, _, count ->
            if (dataBinding.edtConformPassword.text!!.isNotEmpty() && char?.length?:0 > 0) setBtnActivated()
            else setBtnNormal()
        }

        dataBinding.edtConformPassword.doOnTextChanged { char, _, _, count ->
            if (dataBinding.edtNewPassword.text!!.isNotEmpty() && char?.length?:0 > 0) setBtnActivated()
            else setBtnNormal()
        }
    }

    private fun setBtnActivated() {
        dataBinding.btnSubmit.isEnabled = true
        dataBinding.btnSubmit.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.white
            )
        )
    }

    private fun setBtnNormal() {
        dataBinding.btnSubmit.isEnabled = false
        dataBinding.btnSubmit.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.color_7D7D7D
            )
        )
    }

    private fun callApiToSetNewPassword() {
        val data=arguments?.getParcelable<GetSecurityCodeResponseModel?>("data")
        val requestParam=SetNewPasswordRequest(data?.code, data?.referenceId, dataBinding.edtNewPassword.text.toString())
        viewModel.setNewPasswordApi(requestParam)
        viewModel.setNewPasswordVal.observe(requireActivity()) {
            dataBinding.btnSubmit.isEnabled=true
            dataBinding.progressBar.gone()

            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        Navigation.findNavController(dataBinding.root).navigate(R.id.action_forgotPasswordFourthFragment_to_forgotPasswordFifthFragment)
                        Toast.makeText(requireActivity(), "Password has been changed successfully.", Toast.LENGTH_SHORT).show()
                    }
                    Status.ERROR -> { Toast.makeText(requireActivity(), resource.message, Toast.LENGTH_LONG).show() }
                    Status.LOADING -> { // show/hide loader }
                }
        }
    }}}

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun validate(): Boolean {
        pwd = dataBinding.edtNewPassword.text!!.toString().trim()
        c_pwd = dataBinding.edtConformPassword.text!!.toString().trim()
        return if (TextUtils.isEmpty(pwd) || pwd.length < 8) {
            Toast.makeText(requireActivity(), getString(R.string.err_pwd_empty), Toast.LENGTH_SHORT)
                .show()
            false
        } else if (TextUtils.isEmpty(c_pwd) || c_pwd.length < 8) {
            Toast.makeText(
                requireActivity(),
                getString(R.string.err_c_pwd_empty),
                Toast.LENGTH_SHORT
            ).show()
            false

        } else if (pwd != c_pwd) {
            Toast.makeText(
                requireActivity(),
                getString(R.string.err_pwd_and_c_pwd_not_same),
                Toast.LENGTH_SHORT
            )
                .show()
            false

        } else {
            true
        }

    }





}