package com.heandroid.ui.account.creation

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.EmailValidationModel
import com.heandroid.databinding.FragmentEmailVerificationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible

class EmailVerificationFragment : BaseFragment<FragmentEmailVerificationBinding>(),
    View.OnClickListener {
    private var email: String = ""
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEmailVerificationBinding {

        return FragmentEmailVerificationBinding.inflate(inflater, container, false)
    }

    override fun init() {
        requireActivity().toolbar(getString(R.string.str_create_an_account))
    }

    override fun initCtrl() {
        binding.apply {
            model = EmailValidationModel(false, " ", "")
            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    isEnable()
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
            btnAction.setOnClickListener(this@EmailVerificationFragment)
            tvResend.setOnClickListener(this@EmailVerificationFragment)
            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 5)
        }
    }

    override fun observer() {

    }

    override fun onClick(v: View?) {
        v.let {
            when (v?.id) {
                R.id.btn_action -> {
                    when (binding.btnAction.text.toString()) {
                        requireActivity().getString(R.string.str_send_security_code) -> {
                            // call api to send security code on user entered email
                            callApiToVerifyEmail()
                            setUpUiToEnterSecurityCode()
                        }

                        requireActivity().getString(R.string.str_verify_security_code) -> {
                            // call api to verify security code
                            callApiForCodeVerification()
                        }
                    }
                }
                R.id.tv_resend -> {
                    // call api to resend OTP
                    callApiToResendOTP()
                }
            }
        }
    }

    private fun callApiToResendOTP() {
        requireActivity().showToast("Call api to resend OTP")
        binding.edtCode.setText("");
    }

    private fun callApiForCodeVerification() {
        findNavController().navigate(R.id.actionEmailVerification_to_AccountTypeSelection)
    }

    private fun setUpUiToEnterSecurityCode() {

        binding.apply {
            btnAction.text = requireActivity().getString(R.string.str_verify_security_code)
            tfEmail.gone()
            tfCode.visible()
            tvResend.visible()
            tvMsg.text = requireActivity().getString(R.string.send_security_code_msg, email)
        }

    }

    private fun callApiToVerifyEmail() {
        email = binding.edtEmail.text.toString()
    }

    private fun isEnable() {
        if (binding.edtEmail.length() > 1) binding.model = EmailValidationModel(
            enable = true,
            email = binding.edtEmail.text.toString(),
            code = binding.edtCode.text.toString()
        )
        else binding.model = EmailValidationModel(
            enable = false,
            email = binding.edtEmail.text.toString(),
            code = binding.edtCode.text.toString()
        )

    }
}