package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentCreateAccountSetupPrepayBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountSetupPrePayFragment : BaseFragment<FragmentCreateAccountSetupPrepayBinding>(), View.OnClickListener {

    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountSetupPrepayBinding.inflate(inflater, container, false)

    override fun init() {
        model = arguments?.getParcelable(Constants.DATA)
        model?.transactionAmount =10.0
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 5)
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
                    putParcelable(Constants.DATA,model)
                }
                findNavController().navigate(R.id.action_createAccoutInfoFragment_to_createAccoutInfoConfirmationFragment, bundle)
            }
        }
    }

}