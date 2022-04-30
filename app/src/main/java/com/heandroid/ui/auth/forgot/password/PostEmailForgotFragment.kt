package com.heandroid.ui.auth.forgot.password

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentForgotPasswordPostalEmailBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostEmailForgotFragment : BaseFragment<FragmentForgotPasswordPostalEmailBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotPasswordPostalEmailBinding = FragmentForgotPasswordPostalEmailBinding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}