package com.heandroid.ui.account.creation.step2

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.AccountTypeSelectionModel
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentCreateAccountTypeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.BUSINESS_ACCOUNT
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.Constants.PERSONAL_ACCOUNT
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountTypeFragment : BaseFragment<FragmentCreateAccountTypeBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private var requestModel : CreateAccountRequestModel? = null
    private var isEditAccountType : Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountTypeBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel=arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 2, 5)
        binding.enable= false
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountTypeFragment)
            rgOptions.setOnCheckedChangeListener(this@CreateAccountTypeFragment)
        }
    }

    override fun observer() {}

    override fun onCheckedChanged(rg: RadioGroup?, checkedId: Int) {
        binding.enable = true
        when(rg?.checkedRadioButtonId) {

            R.id.mrbPersonalAccount -> {
                requestModel?.accountType = PERSONAL_ACCOUNT
                binding.tvPersonalDesc.visible()
            }
            R.id.mrbBusinessAccount -> {
                requestModel?.accountType = BUSINESS_ACCOUNT
                binding.tvBusinessDesc.visible()
            }
        }
    }

    override fun onClick(view: View?) {
      when (view?.id) {

        R.id.btnAction -> {
            val bundle = Bundle()
            bundle.putParcelable(CREATE_ACCOUNT_DATA,requestModel)
            isEditAccountType?.let {
                bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
            }
            when(requestModel?.accountType) {
                PERSONAL_ACCOUNT -> {
                    findNavController().navigate(R.id.action_accountTypeSelectionFragment_to_personalTypeFragment,bundle)
                }
                BUSINESS_ACCOUNT -> {
                    findNavController().navigate(R.id.action_businessPrepayInfoFragment_to_businssInfoFragment,bundle)
                }
                else ->{ showError(binding.root,getString(R.string.in_progress)) }
            }
        }

     }
    }
}