package com.heandroid.ui.auth.forgot.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotOtpBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OTPForgotPassword: BaseFragment<FragmentForgotOtpBinding>(), View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotOtpBinding = FragmentForgotOtpBinding.inflate(inflater,container,false)

    override fun init() {
        binding.btnVerify.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_verify -> {
                findNavController().navigate(R.id.action_otpFragment_to_createPasswordFragment)
            }
        }
    }
}