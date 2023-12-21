package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentTryPaymentAgainBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TryPaymentAgainFragment : BaseFragment<FragmentTryPaymentAgainBinding>(),
    View.OnClickListener {

    private var paymentListSize: Int = 0
    private var personalInformation: PersonalInformation? = null
    private var isDrectDebit: Boolean = false

    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var accountInformation: AccountInformation? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTryPaymentAgainBinding =
        FragmentTryPaymentAgainBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.containsKey(Constants.PAYMENT_METHOD_SIZE) == true) {
            paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0
        }
        if (arguments?.containsKey(Constants.IS_DIRECT_DEBIT) == true) {
            isDrectDebit = arguments?.getBoolean(Constants.IS_DIRECT_DEBIT, false) ?: false
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)

        }

    }

    override fun initCtrl() {
        binding.tryPaymentAgain.setOnClickListener(this)
        binding.cancelBt.setOnClickListener(this)

        if (navFlowCall == Constants.PAYMENT_TOP_UP || navFlowCall == Constants.SUSPENDED) {
            binding.cancelBt.visible()
        } else {
            binding.cancelBt.gone()
        }
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.cancel_bt -> {
                if (navFlowCall == Constants.PAYMENT_TOP_UP) {
                    findNavController().navigate(
                        R.id.action_tryPaymentAgainFragment_to_paymentMethodFragment
                    )
                } else if (navFlowCall == Constants.SUSPENDED) {
                    requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                        putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                    }
                } else {

                }

            }

            R.id.tryPaymentAgain -> {
                if (navFlowCall == Constants.PAYMENT_TOP_UP || navFlowCall == Constants.SUSPENDED) {

                    if(paymentListSize==0){
                        findNavController().popBackStack()
                    }else{
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYMENT_TOP_UP)
                        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                        bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                        findNavController().navigate(
                            R.id.action_tryPaymentAgainFragment_to_accountSuspendedPaymentFragment,
                            bundle
                        )
                    }


                } else if (navFlowCall == Constants.ADD_PAYMENT_METHOD) {
                    accountInformation = dashboardViewModel.accountInformationData.value
                    if (accountInformation?.accSubType.equals(Constants.PAYG)) {
                        findNavController().popBackStack()
                    } else {
                        if (isDrectDebit) {
                            findNavController().popBackStack()
                        } else {
                            val bundle = Bundle()
                            bundle.putString(Constants.NAV_FLOW_KEY, Constants.ADD_PAYMENT_METHOD)
                            bundle.putDouble(Constants.DATA, 0.0)
                            bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                            bundle.putBoolean(Constants.IS_DIRECT_DEBIT, false)
                            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                            findNavController().navigate(
                                R.id.action_tryPaymentAgainFragment_to_selectPaymentMethodFragment,
                                bundle
                            )
                        }
                    }
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

}