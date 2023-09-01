package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentDartChargeAccountTypeEnquiryBinding
import com.conduent.nationalhighways.databinding.FragmentDashboardBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DartChargeAccountTypeEnquiryFragment : BaseFragment<FragmentDartChargeAccountTypeEnquiryBinding>() {

    var dartChargeSelectStatus=""
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDartChargeAccountTypeEnquiryBinding  = FragmentDartChargeAccountTypeEnquiryBinding.inflate(inflater, container, false)

    override fun init() {
        binding.radioButtonYes.setOnClickListener {
            if(binding.radioButtonYes.isChecked){
                dartChargeSelectStatus = Constants.YES
            }else{
                dartChargeSelectStatus = ""
            }
            checkContinue()
        }
        binding.radioButtonNo.setOnClickListener {
            if(binding.radioButtonNo.isChecked){
                dartChargeSelectStatus = Constants.NO
            }else{
                dartChargeSelectStatus = ""
            }
            checkContinue()
        }
        binding.btnNext.setOnClickListener {

        }
    }

    private fun checkContinue() {
        if(dartChargeSelectStatus.isEmpty()){
            binding.btnNext.isEnabled=false
        }else{
            binding.btnNext.isEnabled=true
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
    }

}