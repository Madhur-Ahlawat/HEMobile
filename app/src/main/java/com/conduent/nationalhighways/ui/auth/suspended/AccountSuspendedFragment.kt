package com.conduent.nationalhighways.ui.auth.suspended

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSuspendedFragment : BaseFragment<FragmentAccountSuspendHaltTopUpBinding>(),
    View.OnClickListener {
    private var currentBalance: String = ""
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var crossingCount: String = ""
    private var navFlow: String = ""
    private var paymentListSize: Int = 0


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltTopUpBinding =
        FragmentAccountSuspendHaltTopUpBinding.inflate(inflater, container, false)

    override fun init() {
        paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""
        crossingCount = arguments?.getString(Constants.CROSSINGCOUNT) ?: ""
        if (accountInformation?.accSubType.equals(Constants.PAYG)) {
            binding.textMaximumVehicle.text =
                getString(R.string.str_provide_payment_card_details_desc)
            binding.maximumVehicleAdded.text =
                getString(R.string.str_provide_payment_card_details)
            binding.btnTopUpNow.text = getString(R.string.str_continue)
        } else {
            if (crossingCount.isNotEmpty()) {
                if (crossingCount.toInt() > 0) {
                    binding.maximumVehicleAddedNote.text =
                        getString(R.string.str_you_crossing, "£5.00", crossingCount)
                    binding.maximumVehicleAddedNote.visibility = View.VISIBLE
                } else {
                    binding.maximumVehicleAddedNote.visibility = View.GONE
                }
            }
            val balance = currentBalance.replace("£", "").replace(",", "")
            if (balance.isNotEmpty()) {
                val doubleBalance = balance.toDouble()
                val finalCurrentBalance = 5.00 - doubleBalance
                binding.textMaximumVehicle.text = getString(
                    R.string.str_you_will_need_to_pay,
                    "£" + String.format("%.2f", finalCurrentBalance)
                )
            }

        }
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }

    }

    override fun initCtrl() {
        binding.btnTopUpNow.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        if (arguments?.getString(Constants.NAV_FLOW_KEY) != null) {
            navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""

        }

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnTopUpNow -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                if (accountInformation?.accSubType.equals(Constants.PAYG)) {
                    findNavController().navigate(
                        R.id.action_accountSuspendedFragment_to_paymentMethodFragment,
                        bundle
                    )

                } else {
                    findNavController().navigate(
                        R.id.action_accountSuspendedFragment_to_accountSuspendedPaymentFragment,
                        bundle
                    )
                }
            }

            R.id.cancel_btn -> {
                requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                    putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                    putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                }

            }

        }
    }
}