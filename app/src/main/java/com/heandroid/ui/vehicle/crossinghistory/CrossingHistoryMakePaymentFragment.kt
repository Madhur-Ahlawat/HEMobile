package com.heandroid.ui.vehicle.crossinghistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.CrossingHistoryItem
import com.heandroid.databinding.FragmentCrossingHistoryMakePaymentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.DateUtils
import com.heandroid.utils.Utils.getDirection
import com.heandroid.utils.Utils.loadStatus

class CrossingHistoryMakePaymentFragment : BaseFragment(), View.OnClickListener {

    private lateinit var dataBinding: FragmentCrossingHistoryMakePaymentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dataBinding = FragmentCrossingHistoryMakePaymentBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init() {
        dataBinding.apply {
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

    private fun initCtrl() {
        dataBinding.apply {
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