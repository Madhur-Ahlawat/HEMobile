package com.heandroid.ui.account.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentNominatedProfilePasswordBinding
import com.heandroid.databinding.FragmentProfilePasswordBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NominatedProfilePasswordFragment: BaseFragment<FragmentNominatedProfilePasswordBinding>(), View.OnClickListener {


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentNominatedProfilePasswordBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enable = true
        binding.data=arguments?.getParcelable(Constants.DATA)
        binding.data?.personalInformation?.confirmPassword=binding.data?.accountInformation?.password
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@NominatedProfilePasswordFragment)
            btnChangePassword.setOnClickListener(this@NominatedProfilePasswordFragment)
            tiePassword.onTextChanged { checkButton() }
            tieConfirmPassword.onTextChanged { checkButton() }
        }
    }

    override fun observer() {

    }
    override fun onClick(v: View?) {
        hideKeyboard()
        val bundle = Bundle()
        bundle.putParcelable(Constants.DATA, binding.data)
        when (v?.id) {
            R.id.btnChangePassword -> { findNavController().navigate(R.id.action_passwordFragment_to_updatePasswordFragment,bundle) }
            R.id.btnAction -> { findNavController().navigate(R.id.action_nominatedPasswordFragment_to_nominatedPinFragment, bundle) }
        }
    }

    private fun checkButton() {
        binding.enable = (binding.tiePassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString()
            .trim() == binding.tiePassword.text.toString().trim())

    }
}
