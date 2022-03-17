package com.heandroid.ui.account.creation.step5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentCreateAccountChoosePaymentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountChoosePaymentFragment: BaseFragment<FragmentCreateAccountChoosePaymentBinding>(), View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountChoosePaymentBinding.inflate(inflater,container,false)
    override fun init() {
        binding.tvStep.text=getString(R.string.str_step_f_of_l,5,5)
        binding.enable=false

    }
    override fun initCtrl() {
        binding.btnContine.setOnClickListener(this)
        binding.rgPaymentOptions.setOnCheckedChangeListener(this)
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue -> {
                val bundle = Bundle()
                bundle.putParcelable(DATA,arguments?.getParcelable(DATA))
                findNavController().navigate(R.id.action_choosePaymentFragment_to_cardFragment,bundle)
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(group?.checkedRadioButtonId){
            R.id.rgPaymentOptions -> { binding.enable=true }
        }
    }
}