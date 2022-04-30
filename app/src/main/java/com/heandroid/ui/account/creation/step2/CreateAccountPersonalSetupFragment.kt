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
import com.heandroid.databinding.FragmentCreateAccountPersonalSetupBinding
import com.heandroid.databinding.FragmentCreateAccountTypeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPersonalSetupFragment : BaseFragment<FragmentCreateAccountPersonalSetupBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private var model : CreateAccountRequestModel?=null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPersonalSetupBinding.inflate(inflater, container, false)

    override fun init() {
        model=arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 2, 5)
        binding.enable = false
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountPersonalSetupFragment)
            rgOptions.setOnCheckedChangeListener(this@CreateAccountPersonalSetupFragment)
        }
    }

    override fun observer() {}
    override fun onCheckedChanged(rg: RadioGroup?, checkedId: Int) {
        binding.enable = true
        when (rg?.checkedRadioButtonId) {
            R.id.mrbPrePay -> {
                model?.planType=null
                binding.tvPrepayDesc.visible() }
            R.id.mrbPayG -> {
                model?.planType=Constants.PAYG
                binding.tvPayGDesc.visible() }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnAction -> {
                if(binding.mrbPrePay.isChecked || binding.mrbPayG.isChecked){
                     val bundle = Bundle()
                     bundle.putParcelable(CREATE_ACCOUNT_DATA, model)
                     findNavController().navigate(R.id.action_personalTypeFragment_to_personalDetailsEntryFragment, bundle)
                }
                else { showError(binding.root,getString(R.string.select_account_type)) }
            }
        }
    }
}