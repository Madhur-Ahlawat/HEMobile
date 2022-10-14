package com.conduent.nationalhighways.ui.account.creation.step3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountSetupPrepayBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountSetupPrePayFragment : BaseFragment<FragmentCreateAccountSetupPrepayBinding>(), View.OnClickListener {

    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountSetupPrepayBinding.inflate(inflater, container, false)

    override fun init() {
        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        model?.transactionAmount ="10.00"
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 6)
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountSetupPrePayFragment)
        }
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                val bundle = Bundle().apply {
                    putParcelable(Constants.CREATE_ACCOUNT_DATA,model)
                }
                findNavController().navigate(R.id.action_createAccoutInfoFragment_to_createAccoutInfoConfirmationFragment, bundle)
            }
        }
    }

}