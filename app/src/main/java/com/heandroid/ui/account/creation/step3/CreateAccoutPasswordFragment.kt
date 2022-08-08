package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.address.DataAddress
import com.heandroid.databinding.FragmentCreateAccountPosswordBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.PAYG
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.Utils.isValidPassword
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.visible
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccoutPasswordFragment : BaseFragment<FragmentCreateAccountPosswordBinding>(), View.OnClickListener {

    private var model: CreateAccountRequestModel? = null
    private var isEditAccountType : Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPosswordBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enable = false
        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 6)

        when(model?.planType) {
            PAYG ->{  binding.tvLabel.text=getString(R.string.pay_as_you_go)  }
            Constants.BUSINESS_ACCOUNT -> {
                binding.tvLabel.text=getString(R.string.business_prepay_account)
            }
            else ->{ binding.tvLabel.text=getString(R.string.personal_pre_pay_account) }
        }
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccoutPasswordFragment)
            tiePassword.onTextChanged {
                checkButton()
            }
            tieConfirmPassword.onTextChanged {
                checkButton()
            }
        }
    }

    private fun checkButton() {
        binding.enable = (binding.tiePassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString()
            .trim() == binding.tiePassword.text.toString().trim())

    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {

                if(isValidPassword(binding.tiePassword.text.toString().trim())){
                model?.apply {
                    password = binding.tiePassword.text.toString().trim()
                }
                val bundle = Bundle().apply {
                    putParcelable(Constants.CREATE_ACCOUNT_DATA,model)
                    isEditAccountType?.let {
                        putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
                    }
                }
                findNavController().navigate(R.id.action_createAccoutPasswordFragment_to_createAccoutPinFragment, bundle)
            }
            else {
                showError(binding.root,"•Must begin with a letter\n" +
                        "•Be at least 8 characters long\n" +
                        "•1 uppercase letter\n" +
                        "•1 lowercase letter\n" +
                        "•1 number\n" +
                        "•1 special character: ! @ # \$ % * ( ) - _ + = ~ ; , .\n")
            }}

        }
    }

}