package com.conduent.nationalhighways.ui.account.creation

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountCreateRequestModel
import com.conduent.nationalhighways.databinding.FragmentMobileNumberCaptureVcBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.extn.hideKeyboard


/**
 * Created by Mohammed Sameer Ahmad .
 */
class HWMobileNumberCaptureVC : BaseFragment<FragmentMobileNumberCaptureVcBinding>(),
    View.OnClickListener, OnRetryClickListener {

    var requiredFirstName = false
    var requiredLastName = false

    var requestModel = AccountCreateRequestModel.RequestModel()
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMobileNumberCaptureVcBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputCountry.getEditText().addTextChangedListener(GenericTextWatcher(binding.inputCountry.getEditText()))
        binding.inputMobileNumber.getEditText().addTextChangedListener(GenericTextWatcher(binding.inputMobileNumber.getEditText()))
        binding.inputMobileNumber.getEditText().inputType = InputType.TYPE_CLASS_NUMBER;


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
                requestModel.userInfoModel.firstName = binding.inputCountry.getText().toString()
                requestModel.userInfoModel.lastName = binding.inputMobileNumber.getText().toString()

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
                binding.inputCountry.getEditText() -> {
                    requiredFirstName = binding.inputCountry.getText()?.isNotEmpty() == true
                }
                binding.inputMobileNumber.getEditText() -> {
                    requiredLastName = binding.inputMobileNumber.getText()?.isNotEmpty() == true
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