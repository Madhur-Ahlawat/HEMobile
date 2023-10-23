package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.databinding.FragmentDeletePaymentMethodBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
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
    private var loader: LoaderDialog? = null
    private var paymentList: CardListResponseModel? = null
    private var data : CrossingDetailsModelsResponse? = null
    private var accountNumber:String=""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletePaymentMethodBinding =
        FragmentDeletePaymentMethodBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)


        accountNumber=arguments?.getString(Constants.ACCOUNT_NUMBER)?:""


        if (arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA) != null) {
            paymentList = arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA)

        }

        when(navFlowCall) {

            Constants.PAY_FOR_CROSSINGS -> {
                val data = navData as CrossingDetailsModelsResponse?
                binding.maximumVehicleAdded.text = getString(R.string.your_type_of_vehicle_does_not_match_what_we_have_on_record)
                binding.textMaximumVehicle.text = getString(R.string.our_records_show_the_numberplate,
                    data?.plateNo, data?.dvlaclass?.let { Utils.getVehicleType(
                        requireActivity(),
                        it
                    ) },
                    data?.customerClass?.let { Utils.getVehicleType(requireActivity(), it) },
                    String.format("%.2f", data?.customerClassRate?.toDouble()))
                binding.btnContinue.text = getString(R.string.pay_new_amount)
            }
            Constants.PAYG -> {
                binding.textMaximumVehicle.text=getString(R.string.payg_delete_description)
            }
            Constants.PRE_PAY_ACCOUNT -> {
                binding.textMaximumVehicle.text=getString(R.string.str_your_balance_will_no_longer_available)
            }
        }

    }

    override fun observer() {
        observe(viewModel.deletePrimaryCard, ::handleDeleteCardResponse)

    }

    private fun handleDeleteCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
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
                bundle.putString(Constants.ACCOUNT_NUMBER,accountNumber)
                bundle.putBoolean(SHOW_BACK_BUTTON,false)

                findNavController().navigate(
                    R.id.action_deletePaymentMethodFragment_to_deletePaymentMethodSuccessFragment,
                    bundle
                )


            }

            is Resource.DataError -> {
                if (status.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                }else {
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
                when(navFlowCall) {

                    Constants.PAY_FOR_CROSSINGS -> {
                            handleFlow(true)
                    }

                    else ->{
                        viewModel.deletePrimaryCard()
                        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    }
                }


            }

            R.id.cancel_btn -> {
                when(navFlowCall) {

                    Constants.PAY_FOR_CROSSINGS -> {
                        handleFlow(false)

                    }
                    else ->{
                        findNavController().popBackStack()
                    }
                }

            }

        }
    }

    private fun handleFlow(goWithNewAmount: Boolean) {
        data = navData as CrossingDetailsModelsResponse?
        val unSettledTrips = data?.unSettledTrips?.toInt()
        if(goWithNewAmount){
            data?.chargingRate = data?.customerClassRate
        }
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
        bundle.putParcelable(Constants.NAV_DATA_KEY,data)

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