package com.heandroid.ui.vehicle.crossinghistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.CrossingHistoryItem
import com.heandroid.databinding.FragmentCrossingHistoryMakePaymentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.Utils.getDirection
import com.heandroid.utils.common.Utils.loadStatus

class CrossingHistoryMakePaymentFragment : BaseFragment<FragmentCrossingHistoryMakePaymentBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCrossingHistoryMakePaymentBinding.inflate(inflater, container, false)

    override fun observer() {

    }

    override fun init() {
        binding.apply {
            arguments?.getParcelable<CrossingHistoryItem?>("data")?.run {

                crossingDate.text = DateUtils.convertDateFormat(transactionDate, 0)
                crossingTime.text = DateUtils.convertTimeFormat(exitTime, 0)
                direction.text = getDirection(exitDirection)
                vehicle.text = plateNumber
                transactionId.text = transactionNumber
                loadStatus(prepaid, status)
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
            R.id.make_payment_btn -> {   }
            R.id.back_btn -> { findNavController().popBackStack() }
        }
    }
}