package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountCreateRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPersonalInfoNewBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
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
    var requiredCompanyName = false
    var requestModel = AccountCreateRequestModel.RequestModel()
    var firstNameErrorMsg = ""
    var lastNameErrorMsg = ""
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPersonalInfoNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputFirstName.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.inputLastName.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.inputCompanyName.editText.addTextChangedListener(GenericTextWatcher(2))

        binding.btnNext.setOnClickListener(this)
        firstNameErrorMsg = getString(R.string.enter_contact_first_name)
        lastNameErrorMsg = getString(R.string.enter_contact_last_name)
        if(NewCreateAccountRequestModel.personalAccount){
            binding.txtCompanyName.visibility = View.GONE
            binding.inputCompanyName.visibility = View.GONE
            binding.inputFirstName.setLabel(getString(R.string.primary_account_holder_first_name))
            binding.inputLastName.setLabel(getString(R.string.primary_account_holder_last_name))
            firstNameErrorMsg = getString(R.string.enter_the_primary_account_holder_s_first_name)
            lastNameErrorMsg = getString(R.string.enter_the_primary_account_holder_s_last_name)
        }

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetterOrDigit(source[i]) &&
                    source[i].toString() != "_" &&
                    source[i].toString() != "-"
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        binding.inputFirstName.editText.filters = arrayOf(filter)
        binding.inputLastName.editText.filters = arrayOf(filter)
        binding.inputCompanyName.editText.filters = arrayOf(filter)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {
                if (NewCreateAccountRequestModel.personalAccount){
                    NewCreateAccountRequestModel.firstName = binding.inputFirstName.getText().toString()
                    NewCreateAccountRequestModel.lastName = binding.inputLastName.getText().toString()

                }else{
                    NewCreateAccountRequestModel.companyName=binding.inputCompanyName.getText().toString()
                }


                findNavController().navigate(
                    R.id.action_createAccountPersonalInfo_to_createAccountPostCodeNew)
            }
        }
    }

    override fun onRetryClick() {

    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
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
            count: Int) {
            if (binding.inputFirstName.getText()?.isNotEmpty() == true) {
                requiredFirstName = true
                binding.inputFirstName.error = ""
            }else{
                requiredFirstName = false
                if(index == 0)
                    binding.inputFirstName.error = firstNameErrorMsg
            }
            if (binding.inputLastName.getText()?.isNotEmpty() == true) {
                requiredLastName = true
                binding.inputLastName.error = ""
            }else{
                requiredLastName = false
                if(index == 1)
                    binding.inputLastName.error = lastNameErrorMsg
            }
            if(!NewCreateAccountRequestModel.personalAccount){
                if (binding.inputCompanyName.getText()?.isNotEmpty() == true) {
                    requiredCompanyName = true
                    binding.inputCompanyName.error = ""
                }else{
                    requiredCompanyName = false
                    if(index == 2)
                        binding.inputCompanyName.error = "Enter the company name"
                }
            }


        }

        override fun afterTextChanged(editable: Editable?) {
            if (requiredFirstName && requiredLastName) {
                if(!NewCreateAccountRequestModel.personalAccount){
                    if(requiredCompanyName){
                        binding.btnNext.enable()
                    }else{
                        binding.btnNext.disable()
                    }
                }else{
                    binding.btnNext.enable()
                }
            } else {
                binding.btnNext.disable()
            }
        }
    }
}