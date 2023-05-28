package com.conduent.nationalhighways.ui.account.creation

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountCreateRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPostCodeNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil

import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPostCodeNew : BaseFragment<FragmentCreateAccountPostCodeNewBinding>(),
    View.OnClickListener, OnRetryClickListener {
    private var requiredPostCode = false
    private var accountRequestModel : AccountCreateRequestModel.RequestModel? = null
    private var isPersonalAccount : Boolean? = true
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPostCodeNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputPostCode.editText
            .addTextChangedListener(GenericTextWatcher(binding.inputPostCode.editText))
        binding.btnFindAddress.setOnClickListener(this)
        binding.btnEnterAddressManually.setOnClickListener(this)
        isPersonalAccount = arguments?.getBoolean(Constants.IS_PERSONAL_ACCOUNT,true)
        if(isPersonalAccount == true){
            binding.txtHeading.text = getString(R.string.personal_address)
        }

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetterOrDigit(source[i])
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        binding.inputPostCode.editText.filters = arrayOf(filter)
        binding.inputPostCode.setMaxLength(10)
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
                isPersonalAccount?.let { bundle.putBoolean(Constants.IS_PERSONAL_ACCOUNT, it) }
                findNavController().navigate(
                    R.id.action_createAccountPostCodeNew_to_ManualAddress
                )
            }

        }
    }

    private fun validation() {
        if (binding.inputPostCode.getText().toString().isNotEmpty()) {
            val bundle = Bundle()
            bundle.putString("zipcode", binding.inputPostCode.getText().toString())
            isPersonalAccount?.let { bundle.putBoolean(Constants.IS_PERSONAL_ACCOUNT, it) }
            findNavController().navigate(
                R.id.action_createAccountPostCodeNew_to_selectaddressfragment,
                bundle
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
            val length = binding.inputPostCode.getText()?.length

            if (length != null) {
                if (binding.inputPostCode.getText()?.isNotEmpty() == true && length > 4) {
                    requiredPostCode = true
                    binding.inputPostCode.error = ""
                }else{
                    requiredPostCode = false
                    if(length==0){
                        binding.inputPostCode.error = getString(R.string.please_enter_postcode)
                    }else {
                        binding.inputPostCode.error = getString(R.string.postcode_must_be_between_4_and_10_characters)
                    }
                }
            }


        }

        override fun afterTextChanged(editable: Editable?) {
            binding.btnFindAddress.isEnabled = requiredPostCode
        }
    }
}