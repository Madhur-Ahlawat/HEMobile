package com.conduent.nationalhighways.ui.account.creation.step2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPersonalSetupBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPersonalSetupFragment :
    BaseFragment<FragmentCreateAccountPersonalSetupBinding>(), View.OnClickListener {

    private var model: CreateAccountRequestModel? = null
    private var isEditAccountType: Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPersonalSetupBinding.inflate(inflater, container, false)

    override fun init() {
        model = arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType =
                arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        binding.enable = false
    }

    override fun initCtrl() {
/*
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountPersonalSetupFragment)
            mrbPrePay.setOnClickListener(this@CreateAccountPersonalSetupFragment)
            mrbPayG.setOnClickListener(this@CreateAccountPersonalSetupFragment)
        }
*/
    }

    override fun observer() {}

    override fun onClick(view: View?) {
        when (view?.id) {
/*
            R.id.mrbPrePay -> {
                binding.enable = true
                binding.mrbPayG.isChecked = false
                model?.planType = null
                binding.tvPrepayDesc.visible()
                binding.tvPayGDesc.gone()
            }

            R.id.mrbPayG -> {
                binding.enable = true
                binding.mrbPrePay.isChecked = false
                model?.planType = Constants.PAYG
                binding.tvPayGDesc.visible()
                binding.tvPrepayDesc.gone()
            }

            R.id.btnAction -> {
                when {
                    binding.mrbPayG.isChecked -> {
                        val bundle = Bundle()
                        bundle.putParcelable(CREATE_ACCOUNT_DATA, model)
                        isEditAccountType?.let {
                            bundle.putInt(
                                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                            )
                        }

                        findNavController().navigate(
                            R.id.action_personalTypeFragment_to_personalDetailsEntryFragment,
                            bundle
                        )

                    }
                    binding.mrbPrePay.isChecked -> {
                        val bundle = Bundle()
                        bundle.putParcelable(CREATE_ACCOUNT_DATA, model)
                        isEditAccountType?.let {
                            bundle.putInt(
                                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                            )
                        }

                        findNavController().navigate(
                            R.id.action_personalTypeFragment_to_businssInfoFragment,
                            bundle
                        )

                    }
                    else -> {
                        showError(binding.root, getString(R.string.select_account_type))
                    }
                }

*/
/*
                if(binding.mrbPrePay.isChecked || binding.mrbPayG.isChecked){
                     val bundle = Bundle()
                     bundle.putParcelable(CREATE_ACCOUNT_DATA, model)
                    isEditAccountType?.let {
                        bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
                    }
                     findNavController().navigate(R.id.action_personalTypeFragment_to_personalDetailsEntryFragment, bundle)
                }
*//*

            }
*/
        }
    }
}