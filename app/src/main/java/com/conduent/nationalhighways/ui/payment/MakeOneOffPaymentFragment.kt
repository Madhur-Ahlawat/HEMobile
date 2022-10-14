package com.conduent.nationalhighways.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentMakeOffPaymentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.SpannableString
import android.text.SpannableStringBuilder
import com.conduent.nationalhighways.utils.common.Constants

class MakeOneOffPaymentFragment : BaseFragment<FragmentMakeOffPaymentBinding>(),
    View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMakeOffPaymentBinding.inflate(inflater, container, false)

    override fun init() {
        val arr = arrayListOf(
            requireContext().getString(R.string.str_payment_des1),
            requireContext().getString(R.string.str_payment_des2)
            //requireContext().getString(R.string.str_payment_des3)
        )

        val bulletGap = dp(10).toInt()
        val ssb = SpannableStringBuilder()
        for (i in arr.indices) {
            val line = arr[i]
            val ss = SpannableString(line)
            ss.setSpan(BulletSpan(bulletGap), 0, line.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.append(ss)
            if (i + 1 < arr.size) ssb.append("\n")
        }
        binding.tvLabel.text = ssb
    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {
                val bundle = Bundle()
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY,Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT)
                findNavController().navigate(R.id.action_makeOneOffPaymentFragment_to_makePaymentAddVehicleFragment, bundle)
//              findNavController().navigate(R.id.action_makeOneOffPaymentFragment_to_makeOneOffPaymentCrossingFragment)
            }
        }
    }

    private fun dp(dp: Int): Float {
        return resources.displayMetrics.density * dp
    }
}