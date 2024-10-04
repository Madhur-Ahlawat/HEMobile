package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentDeletePaymentMethodSuccessBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants

class DeletePaymentMethodSuccessFragment :
    BaseFragment<FragmentDeletePaymentMethodSuccessBinding>(), View.OnClickListener {
    private var navFlow: String = ""
    private var lowBalance: String = ""
    private var topUpBalance: String = ""
    private var accountNumber: String = ""
    private var directDebitPaymentList: ArrayList<CardListResponseModel?>? = ArrayList()
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletePaymentMethodSuccessBinding =
        FragmentDeletePaymentMethodSuccessBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }

        if (arguments?.containsKey(Constants.PERSONALDATA) == true) {
            if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
                personalInformation =
                    arguments?.getParcelable(Constants.PERSONALDATA)
            }
        }
        if (arguments?.containsKey(Constants.ACCOUNTINFORMATION) == true) {
            if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
                accountInformation =
                    arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
            }
        }

        if (arguments?.containsKey(Constants.PAYMENT_LIST_DATA) == true && arguments?.getParcelableArrayList<CardListResponseModel>(Constants.PAYMENT_LIST_DATA) != null) {
            paymentList =
                arguments?.getParcelableArrayList(Constants.PAYMENT_LIST_DATA)
        }

        directDebitPaymentList = (paymentList?.filter { it?.bankAccount == true }
            ?: ArrayList()) as ArrayList<CardListResponseModel?>?

        binding.feedbackBt.movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("SetTextI18n")
    override fun initCtrl() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        lowBalance = arguments?.getString(Constants.THRESHOLD_AMOUNT) ?: ""
        topUpBalance = arguments?.getString(Constants.TOP_UP_AMOUNT) ?: ""
        accountNumber = arguments?.getString(Constants.ACCOUNT_NUMBER) ?: ""
        var lBalance = 0.0
        var tBalance = 0.0
        if (lowBalance.isNotEmpty()) {
            lBalance = lowBalance.replace("£", "").replace(",", "").toDouble()
        }
        if (topUpBalance.isNotEmpty()) {
            tBalance = topUpBalance.replace("£", "").replace(",", "").toDouble()
        }

        if (navFlow == Constants.THRESHOLD) {
            binding.btnContinue.text = requireActivity().resources.getString(R.string.str_continue)
            binding.maximumVehicleAdded.text = getString(R.string.str_threshold_limit)
            if (lowBalance.isEmpty()) {
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_top_amount_limit,
                    (String.format("%.2f", tBalance))
                )
            } else if (topUpBalance.isEmpty()) {
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_low_balance_limit,
                    (String.format("%.2f", lBalance))
                )
            } else {
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_low_balance_limit,
                    (String.format("%.2f", lBalance))
                ) + "\n" + getString(
                    R.string.str_your_top_amount_limit,
                    (String.format("%.2f", tBalance))
                )
            }
            binding.cancelBtn.visibility = View.GONE
        } else if (navFlow == Constants.DELETE_CARD) {
            binding.btnContinue.text = resources.getString(R.string.str_continue)
            binding.maximumVehicleAdded.text = getString(R.string.payment_method_deleted)
        }

        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                val bundle = Bundle()
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(R.id.deletePaymentMethodSuccessFragment_to_paymentMethodFragment)
            }

            R.id.cancel_btn -> {

                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.ADD_PAYMENT_METHOD)
                if (navFlowCall == Constants.SUSPENDED) {
                    bundle.putString(Constants.NAV_FLOW_FROM, Constants.PAYG_SUSPENDED)
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                }
                bundle.putDouble(Constants.DATA, 0.0)
                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size.minus(1))
                if (accountInformation?.accSubType.equals(Constants.PAYG)) {

                    if (directDebitPaymentList.orEmpty().size == 1) {
                        bundle.putBoolean(Constants.IS_DIRECT_DEBIT, true)
                    } else {
                        bundle.putBoolean(Constants.IS_DIRECT_DEBIT, false)
                    }

                    findNavController().navigate(
                        R.id.deletePaymentMethodSuccessFragment_to_nmiPaymentFragment,
                        bundle
                    )

                } else {
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)

                    if (directDebitPaymentList.orEmpty().size == 1) {
                        bundle.putBoolean(Constants.IS_DIRECT_DEBIT, true)
                        findNavController().navigate(
                            R.id.deletePaymentMethodSuccessFragment_to_nmiPaymentFragment,
                            bundle
                        )
                    } else {
                        bundle.putBoolean(Constants.IS_DIRECT_DEBIT, false)
                        findNavController().navigate(
                            R.id.deletePaymentMethodSuccessFragment_to_selectPaymentMethodFragment,
                            bundle
                        )
                    }


                }


//                findNavController().navigate(R.id.deletePaymentMethodSuccessFragment_to_paymentMethodFragment)

            }
        }

    }

}