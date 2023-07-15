package com.conduent.nationalhighways.ui.bottomnav.account.close_account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.conduent.nationalhighways.databinding.FragmentTopupDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import javax.inject.Inject

@AndroidEntryPoint
class CloseAccountFragment : BaseFragment<FragmentCloseAccountBinding>() {

    private var dateRangeModel: PaymentDateRangeModel?=null
    private var topup: String?=null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCloseAccountBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun onResume() {
        super.onResume()
        binding.crossingAmount.text=crossing?.balance
        binding.tvPaymentDateValue.text=crossing?.transactionDate
        binding.tvPaymentTimeValue.text=dateRangeModel?.vehicleNumber
        binding.tvPaymentReferenceValue.text=crossing?.transactionNumber
        binding.tvTypeOfPaymentValue.text=crossing?.activity
        binding.tvPaymentMethodValue.text=crossing?.rebillPaymentType?.substring(0,
            crossing?.rebillPaymentType?.indexOf("-")!!)
        binding.tvChannelValue.text=Constants.CHANNEL_WEB
        binding.tvFourDigitsOfTheCardValue.text=crossing?.rebillPaymentType?.substring(crossing?.rebillPaymentType?.indexOf("-")!!+1,crossing?.rebillPaymentType?.length!!)
    }

    override fun initCtrl() {
        binding.buttonEmailReciept.setOnClickListener {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            dashboardViewModel.whereToReceivePaymentReceipt(
                PaymentReceiptDeliveryTypeSelectionRequest(crossing?.transactionNumber,Constants.Email)
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