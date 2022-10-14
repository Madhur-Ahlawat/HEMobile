package com.conduent.nationalhighways.ui.vehicle.crossinghistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryItem
import com.conduent.nationalhighways.databinding.FragmentCrossingHistoryMakePaymentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils.getDirection
import com.conduent.nationalhighways.utils.extn.changeBackgroundColor
import com.conduent.nationalhighways.utils.extn.changeTextColor

class CrossingHistoryMakePaymentFragment :
    BaseFragment<FragmentCrossingHistoryMakePaymentBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCrossingHistoryMakePaymentBinding.inflate(inflater, container, false)

    override fun observer() {

    }

    override fun init() {
        binding.apply {
            arguments?.getParcelable<CrossingHistoryItem?>(Constants.DATA)?.run {

                crossingDate.text = DateUtils.convertDateFormat(transactionDate, 0)
                crossingTime.text = DateUtils.convertTimeFormat(exitTime!!, 0)
                direction.text = getDirection(exitDirection)
                vehicle.text = plateNumber
                transactionId.text = transactionNumber
//                loadStatus(prepaid, status)
                status.text = tranSettleStatus
                status.changeTextColor(R.color.color_10403C)
                status.changeBackgroundColor(R.color.color_CCE2D8)

                loadMakePaymentStatus(prepaid!!)
            }
        }
    }

    override fun initCtrl() {
        binding.apply {
            makePaymentBtn.setOnClickListener(this@CrossingHistoryMakePaymentFragment)
            backBtn.setOnClickListener(this@CrossingHistoryMakePaymentFragment)
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.make_payment_btn -> {
            }
            R.id.back_btn -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun loadMakePaymentStatus(status: String) {
        when (status) {
            "Y" -> {
                binding.makePaymentBtn.text =
                    requireContext().getString(R.string.str_download_payment_receipt)
            }
            "N" -> {
                binding.makePaymentBtn.text = requireContext().getString(R.string.str_make_payment)
            }

            else -> {
                binding.makePaymentBtn.text = requireContext().getString(R.string.str_make_payment)
            }
        }
    }
}