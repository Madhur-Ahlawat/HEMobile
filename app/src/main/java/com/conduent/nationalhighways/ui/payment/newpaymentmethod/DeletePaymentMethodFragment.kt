package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentDeletePaymentMethodBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.SHOW_BACK_BUTTON
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletePaymentMethodFragment : BaseFragment<FragmentDeletePaymentMethodBinding>(),
    View.OnClickListener {

    private val viewModel: PaymentMethodViewModel by viewModels()
    private var data: CrossingDetailsModelsResponse? = null
    private var accountNumber: String = ""
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var paymentArrayList: MutableList<CardListResponseModel?>? = ArrayList()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletePaymentMethodBinding =
        FragmentDeletePaymentMethodBinding.inflate(inflater, container, false)

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
    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        accountNumber = arguments?.getString(Constants.ACCOUNT_NUMBER) ?: ""

        if (arguments?.containsKey(Constants.PAYMENT_LIST_DATA) == true && arguments?.getParcelableArrayList<CardListResponseModel>(
                Constants.PAYMENT_LIST_DATA
            ) != null
        ) {
            paymentArrayList =
                arguments?.getParcelableArrayList(Constants.PAYMENT_LIST_DATA)
        }

        when (navFlowCall) {

            Constants.PAY_FOR_CROSSINGS -> {
                val data = navData as CrossingDetailsModelsResponse?
                binding.maximumVehicleAdded.text =
                    getString(R.string.your_type_of_vehicle_does_not_match_what_we_have_on_record)
                binding.textMaximumVehicle.text = getString(
                    R.string.str_your_balance_will_no_longer_available,
                    data?.plateNo, data?.dvlaclass?.let {
                        Utils.getVehicleType(
                            requireActivity(),
                            it
                        )
                    },
                    data?.customerClass?.let { Utils.getVehicleType(requireActivity(), it) },
                    String.format("%.2f", data?.customerClassRate?.toDouble())
                )
                binding.btnContinue.text = getString(R.string.pay_new_amount)
            }

            Constants.PAYG -> {
                binding.textMaximumVehicle.text = getString(R.string.payg_delete_description)
            }

            Constants.PRE_PAY_ACCOUNT -> {
                binding.textMaximumVehicle.text =
                    getString(R.string.str_your_balance_will_no_longer_available)
            }
        }
        if(navFlowFrom== Constants.DELETE_CARD){
          binding.cancelBtn.setBorderColor(resources.getColor(R.color.redButtonColor))
          binding.cancelBtn.setTextColor(resources.getColor(R.color.redButtonColor))
        }else{
            binding.cancelBtn.setBorderColor(resources.getColor(R.color.new_btn_color))
            binding.cancelBtn.setTextColor(resources.getColor(R.color.new_btn_color))
        }
    }

    override fun observer() {
        observe(viewModel.deletePrimaryCard, ::handleDeleteCardResponse)

    }

    private fun handleDeleteCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("500") == true || status.data?.statusCode?.equals(
                        "1209"
                    ) == true
                ) {
                    ErrorUtil.showError(binding.root, status.data.message)
                    return
                }
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.DELETE_CARD)
                bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)
                bundle.putBoolean(SHOW_BACK_BUTTON, false)
                bundle.putParcelable(Constants.PERSONALDATA,personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION,accountInformation)
                bundle.putParcelableArrayList(Constants.PAYMENT_LIST_DATA,paymentArrayList as ArrayList)

                findNavController().navigate(
                    R.id.action_deletePaymentMethodFragment_to_deletePaymentMethodSuccessFragment,
                    bundle
                )


            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                when (navFlowCall) {

                    Constants.PAY_FOR_CROSSINGS -> {
                        handleFlow(true)
                    }

                    else -> {
                        viewModel.deletePrimaryCard()
                        showLoaderDialog()
                    }
                }


            }

            R.id.cancel_btn -> {
                when (navFlowCall) {

                    Constants.PAY_FOR_CROSSINGS -> {
                        handleFlow(false)

                    }

                    else -> {
                        findNavController().popBackStack()
                    }
                }

            }

        }
    }

    private fun handleFlow(goWithNewAmount: Boolean) {
        data = navData as CrossingDetailsModelsResponse?
        val unSettledTrips = data?.unSettledTrips
        if (goWithNewAmount) {
            data?.chargingRate = data?.customerClassRate
        }
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putParcelable(Constants.NAV_DATA_KEY, data)

        if (unSettledTrips != null && unSettledTrips > 0) {

            findNavController().navigate(
                R.id.action_deletePaymentMethodFragment_to_pay_for_crossingFragment,
                bundle
            )

        } else {
            findNavController().navigate(
                R.id.action_deletePaymentMethodFragment_to_additional_crossingFragment,
                bundle
            )
        }
    }


}