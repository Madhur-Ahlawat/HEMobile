package com.conduent.nationalhighways.ui.account.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentOptForSmsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class OptForSmsFragment : BaseFragment<FragmentOptForSmsBinding>(), View.OnClickListener {
    private lateinit var  navFlow:String // create account , forgot password

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOptForSmsBinding = FragmentOptForSmsBinding.inflate(inflater, container, false)

    override fun init() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()
        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId -> // checkedId is the RadioButton selected
            when(checkedId){
                R.id.radioButtonYes -> {
                    binding.checkBoxTerms.visibility = View.VISIBLE
                    binding.btnNext.disable()
                    binding.checkBoxTerms.isChecked = false
                }
                R.id.radioButtonNo -> {
                    binding.checkBoxTerms.visibility = View.GONE
                    binding.btnNext.enable()
                }
            }
        }


        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.btnNext.enable()
            }else{
                binding.btnNext.disable()
            }
        }
        binding.btnNext.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext ->{
                val bundle=Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.ACCOUNT_CREATION_FLOW)
                findNavController().navigate(R.id.action_optForSmsFragment_to_twoStepVerificationFragment,bundle)
            }
        }
    }

}