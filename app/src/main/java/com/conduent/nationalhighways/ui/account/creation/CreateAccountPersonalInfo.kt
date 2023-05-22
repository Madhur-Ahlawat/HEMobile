package com.conduent.nationalhighways.ui.account.creation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountCreateRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPersonalInfoNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

const val accountRequestModelKey = "accountRequestModel"

@AndroidEntryPoint
class CreateAccountPersonalInfo : BaseFragment<FragmentCreateAccountPersonalInfoNewBinding>(),
    View.OnClickListener, OnRetryClickListener {

    var requiredFirstName = false
    var requiredLastName = false

    var requestModel = AccountCreateRequestModel.RequestModel()
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPersonalInfoNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputFirstName.editText.addTextChangedListener(GenericTextWatcher(binding.inputFirstName.editText))
        binding.inputLastName.editText.addTextChangedListener(GenericTextWatcher(binding.inputLastName.editText))

        binding.btnNext.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {
                requestModel.userInfoModel.firstName = binding.inputFirstName.getText().toString()
                requestModel.userInfoModel.lastName = binding.inputLastName.getText().toString()

                val bundle = Bundle()
                bundle.putParcelable(accountRequestModelKey, requestModel)
                findNavController().navigate(
                    R.id.action_createAccountPersonalInfo_to_createAccountPostCodeNew,
                    bundle
                )
            }
        }
    }

    override fun onRetryClick() {

    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            when (view) {
                binding.inputFirstName.getEditText() -> {
                    requiredFirstName = binding.inputFirstName.getText()?.isNotEmpty() == true
                }
                binding.inputLastName.getEditText() -> {
                    requiredLastName = binding.inputLastName.getText()?.isNotEmpty() == true
                }
            }

        }

        override fun afterTextChanged(editable: Editable?) {
                if (requiredFirstName && requiredLastName){
                    binding.btnNext.enable()
                }else{
                    binding.btnNext.disable()
                }
        }
    }
}