package com.conduent.nationalhighways.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentTollDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TollDetailsFragment : BaseFragment<FragmentTollDetailsBinding>() {

    private var data: CrossingDetailsModelsResponse? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTollDetailsBinding.inflate(inflater, container, false)


    override fun init() {
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).setTitle(getString(R.string.crossing_details))
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            crossingAmount.text = crossing?.amount
            tvCrossingDateValue.text = crossing?.transactionDate
            tvTimeValue.text = crossing?.exitTime
            tvVrnValue.text = crossing?.plateNumber
            if (crossing?.exitDirection.equals("N")) {
                tvLocationValue.text = getString(R.string.northbound)
            } else {
                tvLocationValue.text = getString(R.string.southbound)
            }
            if (crossing?.tranSettleStatus?.isEmpty() == true) {
                binding.tvPaymentStatusValue.gone()
                binding.tvPaymentStatus.gone()
            } else {
                binding.tvPaymentStatusValue.visible()
                binding.tvPaymentStatus.visible()
                if (crossing?.tranSettleStatus.toString().lowercase() == "unsettled") {
                    tvPaymentStatusValue.text = Utils.capitalizeString("Pending")
                } else if (crossing?.tranSettleStatus.toString().lowercase() == "pcr") {
                    tvPaymentStatusValue.text = Utils.capitalizeString("Unpaid")
                } else {
                    tvPaymentStatusValue.text = Utils.capitalizeString(crossing?.tranSettleStatus)
                }
            }
        }
        (requireActivity() as HomeActivityMain).setTitle(resources.getString(R.string.crossing_details))
        setAccessibilityText()
    }

    private fun setAccessibilityText() {
        binding.paymentAmountCl.contentDescription =
            binding.crossingAmount.text.toString() + "\n " + binding.labelCrossingAmount.text.toString()
        binding.crossingDateCl.contentDescription =
            binding.tvCrossingDate.text.toString() + "\n " + binding.tvCrossingDateValue.text.toString()
        binding.tvVrnCl.contentDescription =
            binding.tvVrn.text.toString() + "\n " + binding.tvVrnValue.text.toString()
        binding.timeCl.contentDescription =
            binding.tvTime.text.toString() + "\n " + binding.tvTimeValue.text.toString()
        binding.directionCl.contentDescription =
            binding.tvLocation.text.toString() + "\n " + binding.tvLocationValue.text.toString()
        binding.paymentMethodCl.contentDescription =
            binding.tvPaymentStatus.text.toString() + "\n " + binding.tvPaymentStatusValue.text.toString()
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}