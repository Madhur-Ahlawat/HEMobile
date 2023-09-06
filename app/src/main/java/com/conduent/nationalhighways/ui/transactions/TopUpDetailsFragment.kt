package com.conduent.nationalhighways.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.conduent.nationalhighways.databinding.FragmentTopupDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.checkedCrossing
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import javax.inject.Inject

@AndroidEntryPoint
class TopUpDetailsFragment : BaseFragment<FragmentTopupDetailsBinding>() {

    private var dateRangeModel: PaymentDateRangeModel? = null
    private var topup: String? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var data: CrossingDetailsModelsResponse? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTopupDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
    }

    override fun onResume() {
        super.onResume()
        if (crossing == null) {
            binding?.apply {
                tvPaymentReference.gone()
                tvPaymentReferenceValue.gone()
                tvPaymentMethod.gone()
                tvPaymentMethodValue.gone()
                tvLastFourDigitsOfTheCard.gone()
                tvFourDigitsOfTheCardValue.gone()
                crossingAmount.text = checkedCrossing?.amount
                tvPaymentDateValue.text = checkedCrossing?.entryDate
                tvPaymentTimeValue.text = checkedCrossing?.exitTime
                tvTypeOfPaymentValue.text = checkedCrossing?.activity
                tvChannelValue.text = Constants.CHANNEL_WEB

            }

        } else {
            binding.apply {
                tvPaymentReference.visible()
                tvPaymentReferenceValue.visible()
                tvPaymentMethod.visible()
                tvPaymentMethodValue.visible()
                tvLastFourDigitsOfTheCard.visible()
                tvFourDigitsOfTheCardValue.visible()
                crossingAmount.text = crossing?.balance
                tvPaymentDateValue.text = crossing?.transactionDate
                tvPaymentTimeValue.text = crossing?.exitTime
                tvPaymentReferenceValue.text = crossing?.transactionNumber
                tvTypeOfPaymentValue.text = crossing?.activity
                tvPaymentMethodValue.text = crossing?.rebillPaymentType?.substring(
                    0,
                    crossing?.rebillPaymentType?.indexOf("-")!!
                )
                tvChannelValue.text = Constants.CHANNEL_WEB
                tvFourDigitsOfTheCardValue.text = crossing?.rebillPaymentType?.substring(
                    crossing?.rebillPaymentType?.indexOf("-")!! + 1,
                    crossing?.rebillPaymentType?.length!!
                )
            }

        }

    }

    override fun initCtrl() {
        binding.buttonEmailReciept.setOnClickListener {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
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
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
//        emailSuccessResponse=resource?.data
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    findNavController().navigate(R.id.emailRecieptSuccessFragment)
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {}
        }

    }
}