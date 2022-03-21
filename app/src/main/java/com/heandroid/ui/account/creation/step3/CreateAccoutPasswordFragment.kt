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
import com.heandroid.databinding.FragmentCreateAccountPostcodeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.visible
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccoutPasswordFragment : BaseFragment<FragmentCreateAccountPosswordBinding>(), View.OnClickListener {

    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPosswordBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enable = false
        model = arguments?.getParcelable(Constants.DATA)
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 5)

        when(arguments?.getInt(Constants.PERSONAL_TYPE,0)){
            Constants.PERSONAL_TYPE_PREPAY ->{ binding.tvLabel.text=getString(R.string.personal_pre_pay_account) }
            Constants.PERSONAL_TYPE_PAY_AS_U_GO ->{  binding.tvLabel.text=getString(R.string.pay_as_you_go)  }
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
            .trim() == binding.tieConfirmPassword.text.toString().trim())

    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                model?.apply {
                    password = binding.tiePassword.text.toString().trim()
                }
                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA,model)
                    putInt(Constants.PERSONAL_TYPE, arguments?.getInt(Constants.PERSONAL_TYPE)!!)

                }
                findNavController().navigate(R.id.action_createAccoutPasswordFragment_to_createAccoutPinFragment, bundle)
            }

        }
    }

}