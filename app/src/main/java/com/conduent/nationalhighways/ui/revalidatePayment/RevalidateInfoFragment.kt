package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidateInfoBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RevalidateInfoFragment : BaseFragment<FragmentRevalidateInfoBinding>() {

    private var paymentList: CardListResponseModel? = null
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidateInfoBinding =FragmentRevalidateInfoBinding.inflate(inflater, container, false)

    override fun init() {


        if (navFlowFrom == Constants.CARD_VALIDATION_LATER_DATE) {
            binding.warningIcon.setImageResource(R.drawable.warningicon)
            binding.titleTv.text =
                resources.getString(R.string.str_important)
            binding.descTv.text =
                resources.getString(R.string.str_donot_have_valid_payment_method)
            binding.cancelBtn.visible()
            binding.btnContinue.visible()
        } else if (navFlowFrom == Constants.CARD_VALIDATION_REQUIRED) {
            binding.titleTv.text =
                resources.getString(R.string.str_payment_card_details_confirmed)
            binding.descTv.text =
                resources.getString(R.string.str_payment_card_details_confirmed_desc1)
            binding.cancelBtn.gone()
            binding.btnContinue.visible()
        }

        binding.btnContinue.setOnClickListener {
            requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            }
        }

        binding.cancelBtn.setOnClickListener {
            if (navFlowFrom == Constants.CARD_VALIDATION_LATER_DATE) {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.CARD_VALIDATION_REQUIRED)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                bundle.putDouble(Constants.DATA, 0.0)

                findNavController().navigate(
                    R.id.action_reValidateInfoFragment_to_nmiPaymentFragment,
                    bundle
                )
            }
        }

    }

    override fun initCtrl() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }
        Log.e("TAG", "initCtrl:-)-> ")

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }

        if (arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA) != null) {
            paymentList = arguments?.getParcelable(Constants.PAYMENT_DATA)

        }

    }

    override fun observer() {

    }

}