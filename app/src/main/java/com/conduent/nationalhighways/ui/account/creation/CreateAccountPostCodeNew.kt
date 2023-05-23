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
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPostCodeNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPostCodeNew : BaseFragment<FragmentCreateAccountPostCodeNewBinding>(),
    View.OnClickListener, OnRetryClickListener {
    var requiredPostCode = false
    var accountRequestModel : AccountCreateRequestModel.RequestModel? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPostCodeNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputPostCode.editText
            .addTextChangedListener(GenericTextWatcher(binding.inputPostCode.editText))
        binding.btnFindAddress.setOnClickListener(this)
        binding.btnEnterAddressManually.setOnClickListener(this)
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
            if (binding.inputPostCode.getText()?.isNotEmpty()==true){
                requiredPostCode=true
            }


        }

        override fun afterTextChanged(editable: Editable?) {
            binding.btnFindAddress.isEnabled = requiredPostCode
        }
    }
}