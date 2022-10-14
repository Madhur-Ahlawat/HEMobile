package com.conduent.nationalhighways.ui.account.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentNominatedProfilePasswordBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NominatedProfilePasswordFragment : BaseFragment<FragmentNominatedProfilePasswordBinding>(),
    View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNominatedProfilePasswordBinding.inflate(inflater, container, false)

    override fun init() {
        checkButton()
        binding.data = arguments?.getParcelable(Constants.DATA)
        binding.data?.personalInformation?.confirmPassword =
            binding.data?.accountInformation?.password
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@NominatedProfilePasswordFragment)
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
            R.id.btnAction -> {
                findNavController().navigate(
                    R.id.action_nominatedPasswordFragment_to_nominatedPinFragment,
                    bundle
                )
            }
        }
    }

    private fun checkButton() {
        binding.enable = (binding.tiePassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString()
            .trim() == binding.tiePassword.text.toString().trim())

    }
}
