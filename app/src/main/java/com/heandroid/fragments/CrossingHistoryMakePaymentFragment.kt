package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentCrossingHistoryMakePaymentBinding

class CrossingHistoryMakePaymentFragment : BaseFragment(), View.OnClickListener {

    private lateinit var dataBinding: FragmentCrossingHistoryMakePaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentCrossingHistoryMakePaymentBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init() {
        dataBinding.apply {
            //if status is unpaid
            status.text = requireContext().getString(R.string.unpaid)
            status.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_10403C))
            status.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.FCD6C3))

            //if status is refund
            status.text = requireContext().getString(R.string.refund)
            status.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_594D00))
            status.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.FFF7BF))

            //if status is paid
            status.text = requireContext().getString(R.string.paid)
            status.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_10403C))
            status.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.color_CCE2D8))

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
            R.id.make_payment_btn -> {
            }
            R.id.back_btn -> {
                findNavController().popBackStack()
            }
        }
    }
}