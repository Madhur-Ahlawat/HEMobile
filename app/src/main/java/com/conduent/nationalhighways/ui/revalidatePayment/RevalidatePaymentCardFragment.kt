package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidatePaymentCardBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RevalidatePaymentCardFragment : BaseFragment<FragmentRevalidatePaymentCardBinding>() {

    private var accountInformation: AccountInformation? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidatePaymentCardBinding =
        FragmentRevalidatePaymentCardBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation = arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }

        if (accountInformation?.accountType.equals(
                "BUSINESS",
                true
            ) || ((accountInformation?.accSubType.equals(
                "STANDARD", true
            ) && accountInformation?.accountType.equals(
                "PRIVATE", true
            )))
        ) {
            //private account
            binding.radioGroupYesNo.visible()
            binding.desc2Tv.text = resources.getString(R.string.str_wantto_validate_card_now)
            binding.descTv.text =
                resources.getString(R.string.str_revalidate_payment_card_details_prepay)
            binding.desc3Tv.visible()
            checkContinueButton()
        } else {
            //payg account

            binding.radioGroupYesNo.gone()
            binding.desc2Tv.text =
                resources.getString(R.string.str_revalidate_payment_card_details_desc2)
            binding.descTv.text =
                resources.getString(R.string.str_revalidate_payment_card_details_payg)
            binding.desc3Tv.gone()
            binding.btnContinue.enable()
        }


    }

    private fun checkContinueButton() {
        if (binding.radioButtonYes.isChecked || binding.radioButtonNo.isChecked) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }

    override fun initCtrl() {

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            checkContinueButton()
        }

        binding.btnContinue.setOnClickListener {
            if (accountInformation?.accountType.equals(
                    "BUSINESS",
                    true
                ) || ((accountInformation?.accSubType.equals(
                    "STANDARD", true
                ) && accountInformation?.accountType.equals(
                    "PRIVATE", true
                )))
            ) {
                if(binding.radioButtonNo.isChecked){
                    requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                        putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                    }
                }else{
                    findNavController().navigate(R.id.action_reValidatePaymentCardFragment_to_reValidatePaymentCardDetailsFragment)
                }
            }else{
                findNavController().navigate(R.id.action_reValidatePaymentCardFragment_to_reValidatePaymentCardDetailsFragment)
            }
        }
    }

    override fun observer() {
    }

}