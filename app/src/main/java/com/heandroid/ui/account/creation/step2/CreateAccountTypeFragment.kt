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
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.Constants.PERSONAL_ACCOUNT
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountTypeFragment : BaseFragment<FragmentCreateAccountTypeBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private var requestModel : CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountTypeBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel=arguments?.getParcelable(DATA)
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 2, 5)
        binding.model = AccountTypeSelectionModel()
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountTypeFragment)
            rgMainAccount.setOnCheckedChangeListener(this@CreateAccountTypeFragment)
        }
    }

    override fun observer() {}

    override fun onCheckedChanged(rg: RadioGroup?, checkedId: Int) {
        binding.model= AccountTypeSelectionModel(enable = true)
        when(rg?.checkedRadioButtonId) {

            R.id.rb_personal_act -> {
                requestModel?.accountType = PERSONAL_ACCOUNT
                binding.tvPersonalDesc.visible()
            }
            R.id.rb_business_act -> {
                requestModel?.accountType = BUSINESS_ACCOUNT
                binding.tvBusinessDesc.visible()
            }
        }
    }


    override fun onClick(view: View?) {
     when (view?.id) {
        R.id.btn_action -> {
            val bundle = Bundle()
            bundle.putParcelable(DATA,requestModel)
            findNavController().navigate(R.id.action_accountTypeSelectionFragment_to_personalTypeFragment,bundle)
        }
     }
    }
}