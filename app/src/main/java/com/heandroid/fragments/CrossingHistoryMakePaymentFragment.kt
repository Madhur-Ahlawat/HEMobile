package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentCrossingHistoryMakePaymentBinding
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.utils.Utils
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
                 crossingDate.text=transactionDate
                 crossingTime.text=exitTime
                 direction.text=getDirection(entryDirection)
                 vehicle.text=plateNumber
                 transactionId.text=transactionNumber
                 loadStatus(prepaid,status)
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