package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentCreateAccountPersonalInfoBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.Constants.PAYG
import com.heandroid.utils.common.Constants.PERSONAL_TYPE
import com.heandroid.utils.common.Constants.PERSONAL_TYPE_PAY_AS_U_GO
import com.heandroid.utils.common.Constants.PERSONAL_TYPE_PREPAY
import com.heandroid.utils.common.Constants.PRE_PAY_ACCOUNT
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountPersonalInfoFragment : BaseFragment<FragmentCreateAccountPersonalInfoBinding>(), View.OnClickListener {

    private var model : CreateAccountRequestModel ? =null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPersonalInfoBinding.inflate(inflater,container,false)
    override fun init() {
        model=arguments?.getParcelable(DATA)
        model?.firstName=""
        model?.lastName=""
        model?.cellPhone=""
        model?.eveningPhone=""
        model?.enable=false
        binding.model=model
        binding.tvStep.text= getString(R.string.str_step_f_of_l,3,5)
        when(model?.planType) {
            PAYG ->{
                binding.tilMobileNo.gone()
                binding.tvLabel.text=getString(R.string.pay_as_you_go)  }

            else -> { binding.tvLabel.text=getString(R.string.personal_pre_pay_account) }
        }
    }
    override fun initCtrl() {
        binding.btnAction.setOnClickListener(this)
        binding.tieFullName.doAfterTextChanged {
            model?.enable  = (it?.length?:0) > 0
            binding.model = model
        }
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAction -> {
                if(binding.tieFullName.text.toString().contains(" ")){
                   binding.model?.firstName= binding.tieFullName.text.toString().split(" ")[0]
                   binding.model?.lastName= binding.tieFullName.text.toString().split(" ")[1]
                }else {
                    binding.model?.firstName = binding.tieFullName.text.toString()
                    binding.model?.lastName = ""
                }
                val bundle = Bundle()
                bundle.putParcelable(DATA,binding.model)
                findNavController().navigate(R.id.action_personalDetailsEntryFragment_to_postcodeFragment,bundle)
            }
        }
    }
}