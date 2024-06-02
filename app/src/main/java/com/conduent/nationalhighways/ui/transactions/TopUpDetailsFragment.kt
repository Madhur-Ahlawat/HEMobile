package com.conduent.nationalhighways.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.conduent.nationalhighways.databinding.FragmentTopupDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import javax.inject.Inject

@AndroidEntryPoint
class TopUpDetailsFragment : BaseFragment<FragmentTopupDetailsBinding>() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var data: CrossingDetailsModelsResponse? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTopupDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        HomeActivityMain.setTitle(getString(R.string.payment_details))
        (requireActivity() as HomeActivityMain).showHideToolbar(true)
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            crossingAmount.text = crossing?.amount
            tvPaymentDateValue.text = crossing?.transactionDate
            tvPaymentTimeValue.text = crossing?.exitTime
            tvPaymentReferenceValue.text = crossing?.transactionNumber
            tvTypeOfPaymentValue.text = Utils.capitalizeString(crossing?.activity)

            var reBillPaymentType = crossing?.rebillPaymentType
            if (crossing?.rebillPaymentType?.contains("-") == true) {
                reBillPaymentType = crossing?.rebillPaymentType?.substring(
                    0,
                    crossing?.rebillPaymentType?.indexOf("-") ?: 0
                )
            }

            if (reBillPaymentType?.lowercase().equals("current") ||
                reBillPaymentType?.lowercase().equals("savings")
            ) {
                tvPaymentMethodValue.text = resources.getString(R.string.direct_debit)
            } else {
                tvPaymentMethodValue.text = Utils.capitalizeString(reBillPaymentType)
            }

            val paymentSource = crossing?.paymentSource?.lowercase()
            if (paymentSource.equals("vrs") || paymentSource.equals("ivr") || paymentSource.equals("phone-in")) {
                tvChannelValue.text =
                    Utils.capitalizeString(resources.getString(R.string.str_phone))
            } else if (paymentSource.equals("mail-in")) {
                tvChannelValue.text = Utils.capitalizeString(resources.getString(R.string.str_post))
            } else if (paymentSource.equals("mobileapp")) {
                tvChannelValue.text =
                    Utils.capitalizeString(resources.getString(R.string.mobile_app))
            } else {
                tvChannelValue.text = Utils.capitalizeString(crossing?.paymentSource)
            }

            val reBillPayment = crossing?.rebillPaymentType?.substring(
                (crossing?.rebillPaymentType?.indexOf("-") ?: 0) + 1,
                crossing?.rebillPaymentType?.length ?: 0
            )
            if (reBillPayment?.lowercase()?.all { it.isDigit() } == true) {
                tvFourDigitsOfTheCardValue.text = reBillPayment
            } else {
                tvFourDigitsOfTheCardValue.text = "N/A"
            }
        }
        HomeActivityMain.setTitle(resources.getString(R.string.payment_details))

        setAccessibilityText()
    }

    private fun setAccessibilityText() {
        binding.paymentAmountCl.contentDescription =
            binding.crossingAmount.text.toString() + "\n " + binding.labelCrossingAmount.text.toString()
        binding.paymentDateCl.contentDescription =
            binding.tvPaymentDateHeading.text.toString() + "\n " + binding.tvPaymentDateValue.text.toString()
        binding.paymentTimeCl.contentDescription =
            binding.tvPaymentTimeHeading.text.toString() + "\n " + binding.tvPaymentTimeValue.text.toString()
        binding.paymentReferenceCl.contentDescription =
            binding.tvPaymentReference.text.toString() + "\n " + Utils.accessibilityForNumbers(
                binding.tvPaymentReferenceValue.text.toString()
            )
        binding.typeOfPaymentCl.contentDescription =
            binding.tvTypeOfPayment.text.toString() + "\n " + binding.tvTypeOfPaymentValue.text.toString()
        binding.paymentMethodCl.contentDescription =
            binding.tvPaymentMethod.text.toString() + "\n " + binding.tvPaymentMethodValue.text.toString()
        binding.channelCl.contentDescription =
            binding.tvChannel.text.toString() + "\n " + binding.tvChannelValue.text.toString()
        binding.lastFourDigitsCl.contentDescription =
            binding.tvLastFourDigitsOfTheCard.text.toString() + "\n " + Utils.accessibilityForNumbers(
                binding.tvFourDigitsOfTheCardValue.text.toString()
            )
    }

    override fun initCtrl() {
        binding.buttonEmailReciept.setOnClickListener {
            showLoaderDialog()
            dashboardViewModel.whereToReceivePaymentReceipt(
                PaymentReceiptDeliveryTypeSelectionRequest(
                    crossing?.transactionNumber,
                    Constants.Email
                )
            )
        }
    }

    override fun observer() {
        observe(dashboardViewModel.whereToReceivePaymentReceipt, ::receipt)
    }

    private fun receipt(resource: Resource<ResponseBody?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    val bundle = Bundle()
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    findNavController().navigate(R.id.emailRecieptSuccessFragment, bundle)
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {}
        }

    }
}