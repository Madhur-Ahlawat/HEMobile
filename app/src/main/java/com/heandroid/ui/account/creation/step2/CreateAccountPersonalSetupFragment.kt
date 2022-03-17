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
import com.heandroid.databinding.FragmentCreateAccountPersonalSetupBinding
import com.heandroid.databinding.FragmentCreateAccountTypeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPersonalSetupFragment : BaseFragment<FragmentCreateAccountPersonalSetupBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPersonalSetupBinding.inflate(inflater, container, false)

    override fun init() {
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 2, 5)
        binding.enable = false
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountPersonalSetupFragment)
            rgPersonalSubAccount.setOnCheckedChangeListener(this@CreateAccountPersonalSetupFragment)
        }
    }

    override fun observer() {}
    override fun onCheckedChanged(rg: RadioGroup?, checkedId: Int) {
        binding.enable = true
        when(rg?.checkedRadioButtonId){
            R.id.rb_prepay -> { binding.tvPrepayDesc.visible() }
            R.id.rb_payg_act -> { binding.tvPaygDesc.visible() }
        }
    }

    override fun onClick(view: View?) {
     when (view?.id) {
        R.id.btn_action -> {
            val bundle = Bundle()
            bundle.putParcelable(DATA,arguments?.getParcelable(DATA))
            findNavController().navigate(R.id.action_personalTypeFragment_to_personalDetailsEntryFragment,bundle)
        }
     }
    }
}