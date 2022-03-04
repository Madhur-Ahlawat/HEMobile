package com.heandroid.ui.account.creation

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.data.model.account.EmailValidationModel
import com.heandroid.databinding.FragmentEmailVerificationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.toolbar

class FragmentEmailVerification : BaseFragment<FragmentEmailVerificationBinding>(),
    View.OnClickListener {

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
            btnAction.setOnClickListener(this@FragmentEmailVerification)
        }
    }

    override fun observer() {

    }

    override fun onClick(v: View?) {
        v.let {


        }
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