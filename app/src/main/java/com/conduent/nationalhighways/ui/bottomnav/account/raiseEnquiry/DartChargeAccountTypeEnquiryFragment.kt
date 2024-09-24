package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentDartChargeAccountTypeEnquiryBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.openActivityWithDataBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DartChargeAccountTypeEnquiryFragment :
    BaseFragment<FragmentDartChargeAccountTypeEnquiryBinding>() {

    private var dartChargeSelectStatus = ""
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDartChargeAccountTypeEnquiryBinding =
        FragmentDartChargeAccountTypeEnquiryBinding.inflate(inflater, container, false)

    override fun init() {
        binding.radioButtonYes.setOnClickListener {
            dartChargeSelectStatus = if (binding.radioButtonYes.isChecked) {
                Constants.YES
            } else {
                ""
            }
            checkContinue()
        }
        binding.radioButtonNo.setOnClickListener {
            dartChargeSelectStatus = if (binding.radioButtonNo.isChecked) {
                Constants.NO
            } else {
                ""
            }
            checkContinue()
        }
        binding.btnNext.setOnClickListener {
            if (dartChargeSelectStatus == Constants.NO) {
                findNavController().navigate(R.id.action_dartChargeAccountTypeEnquiryFragment_to_enquiryCategoryFragment)
            } else {
                requireActivity().openActivityWithDataBack(LoginActivity::class.java) {
                    putString(
                        Constants.NAV_FLOW_FROM,
                        Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS
                    )
                }
            }
        }


        if (dartChargeSelectStatus == Constants.YES || dartChargeSelectStatus == Constants.NO) {
            binding.btnNext.enable()
        } else {
            binding.btnNext.disable()
        }
//        if(requireActivity() is RaiseEnquiryActivity){
//            (requireActivity() as RaiseEnquiryActivity).focusToolBarRaiseEnquiry()
//        }
    }

    private fun checkContinue() {
        if (dartChargeSelectStatus.isEmpty()) {
            binding.btnNext.disable()
        } else {
            binding.btnNext.enable()
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
    }

}