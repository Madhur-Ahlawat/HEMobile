package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPostCodeNewBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.ErrorUtil

import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPostCodeNew : BaseFragment<FragmentCreateAccountPostCodeNewBinding>(),
    View.OnClickListener, OnRetryClickListener {
    private var requiredPostCode = false
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPostCodeNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputPostCode.editText
            .addTextChangedListener(GenericTextWatcher(binding.inputPostCode.editText))
        binding.btnFindAddress.setOnClickListener(this)
        binding.btnEnterAddressManually.setOnClickListener(this)
        if (NewCreateAccountRequestModel.personalAccount) {
            binding.txtHeading.text = getString(R.string.personal_address)
            binding.txtThisShouldBeVehicle.text =
                getString(R.string.this_should_be_the_address_were_the_vehicle_is_registered)
        }

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetterOrDigit(source[i]) &&
                    source[i].toString() != " " &&
                    source[i].toString() != "-"
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        binding.inputPostCode.editText.filters = arrayOf(filter)
        binding.inputPostCode.setMaxLength(10)
        if(NewCreateAccountRequestModel.isEditCall){
            binding.inputPostCode.setText(NewCreateAccountRequestModel.zipCode)
        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnFindAddress -> {
                validation()
            }

            R.id.btnEnterAddressManually -> {
                val bundle = Bundle()
                findNavController().navigate(
                    R.id.action_createAccountPostCodeNew_to_ManualAddress, bundle
                )
            }

        }
    }

    private fun validation() {
        if (binding.inputPostCode.getText().toString().isNotEmpty()) {
            NewCreateAccountRequestModel.zipCode = binding.inputPostCode.getText().toString()
            findNavController().navigate(
                R.id.action_createAccountPostCodeNew_to_selectaddressfragment
            )
        } else {
            ErrorUtil.showError(binding.root, getString(R.string.please_enter_postcode))
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

            requiredPostCode = if (binding.inputPostCode.getText().toString().trim().isEmpty()) {
                //binding.inputPostCode.setErrorText(getString(R.string.str_post_code_error_message))
                false
            } else {
                val string = binding.inputPostCode.getText().toString().trim()
                val finalString = string.replace(" ", "")
                if (finalString.length < 4 || finalString.length > 11) {
                    binding.inputPostCode.setErrorText(getString(R.string.postcode_must_be_between_4_and_10_characters))
                    false

                } else {
                    binding.inputPostCode.removeError()
                    true
                }
            }


        }

        override fun afterTextChanged(editable: Editable?) {
            binding.btnFindAddress.isEnabled = requiredPostCode
        }
    }
}