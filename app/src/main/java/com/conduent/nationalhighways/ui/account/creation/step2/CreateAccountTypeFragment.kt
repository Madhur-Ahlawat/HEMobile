package com.conduent.nationalhighways.ui.account.creation.step2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountTypeBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.BUSINESS_ACCOUNT
import com.conduent.nationalhighways.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.conduent.nationalhighways.utils.common.Constants.PERSONAL_ACCOUNT
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountTypeFragment : BaseFragment<FragmentCreateAccountTypeBinding>(),
    View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null
    private var isEditAccountType: Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountTypeBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType =
                arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 2, 6)
        binding.enable = false
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountTypeFragment)
            mrbPersonalAccount.setOnClickListener(this@CreateAccountTypeFragment)
            mrbBusinessAccount.setOnClickListener(this@CreateAccountTypeFragment)
        }
    }

    override fun observer() {}

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.mrbPersonalAccount -> {
                binding.mrbBusinessAccount.isChecked = false
                binding.enable = true
                requestModel?.accountType = PERSONAL_ACCOUNT
                binding.tvPersonalDesc.visible()
                binding.tvBusinessDesc.gone()
            }
            R.id.mrbBusinessAccount -> {
                binding.mrbPersonalAccount.isChecked = false
                binding.enable = true
                binding.tvBusinessDesc.visible()
                binding.tvPersonalDesc.gone()
                requestModel?.accountType = BUSINESS_ACCOUNT
            }
            R.id.btnAction -> {
                val bundle = Bundle()
                bundle.putParcelable(CREATE_ACCOUNT_DATA, requestModel)
                isEditAccountType?.let {
                    bundle.putInt(
                        Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                        Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                    )
                }
                when (requestModel?.accountType) {
                    PERSONAL_ACCOUNT -> {
                        findNavController().navigate(
                            R.id.action_accountTypeSelectionFragment_to_personalTypeFragment,
                            bundle
                        )
                    }
                    BUSINESS_ACCOUNT -> {
                        findNavController().navigate(
                            R.id.action_businessPrepayInfoFragment_to_businssInfoFragment,
                            bundle
                        )
                    }
                    else -> {
                        showError(binding.root, getString(R.string.in_progress))
                    }
                }
            }

        }
    }
}