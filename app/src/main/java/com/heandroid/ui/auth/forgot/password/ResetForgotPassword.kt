package com.heandroid.ui.auth.forgot.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.ui.base.BaseFragment

import com.heandroid.databinding.FragmentForgotResetBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.utils.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResetForgotPassword : BaseFragment<FragmentForgotResetBinding>(), View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotResetBinding = FragmentForgotResetBinding.inflate(inflater,container,false)

    override fun init() {
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_submit -> {
                requireActivity().startNormalActivity(AuthActivity::class.java)
                requireActivity().finish()
            }
        }
    }
}