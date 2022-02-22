package com.heandroid.ui.auth.forgot.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotCreateNewPasswordBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateNewPasswordFragment : BaseFragment<FragmentForgotCreateNewPasswordBinding>(), View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotCreateNewPasswordBinding = FragmentForgotCreateNewPasswordBinding.inflate(inflater,container,false)

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
                findNavController().navigate(R.id.action_createPasswordFragment_to_resetFragment)
            }
        }
    }
}