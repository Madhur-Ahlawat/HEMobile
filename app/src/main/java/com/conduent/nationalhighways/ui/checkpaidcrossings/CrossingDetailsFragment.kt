package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentCrossingDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@AndroidEntryPoint
class CrossingDetailsFragment : BaseFragment<FragmentCrossingDetailsBinding>(), View.OnClickListener {

    private var data : CheckPaidCrossingsResponse? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCrossingDetailsBinding = FragmentCrossingDetailsBinding.inflate(inflater, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {

        data = navData as CheckPaidCrossingsResponse

        data.let {
            val crossings = it?.unusedTrip?.toInt()
            binding.fullName.text = it?.referenceNumber
            binding.companyName.text = it?.plateNumber
            binding.address.text = crossings.toString()+ " crossings"
            binding.emailAddress.text = it?.expirationDate?.let { it1 ->
                DateUtils.convertDateFormatToDateFormat(
                    it1
                )
            }
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm:ss", Locale.ENGLISH)
            val date = LocalDateTime.parse(it?.expirationDate, formatter)
            if(date.isBefore(current)){
                binding.errorTxt.visible()
                binding.errorTxt.text = getString(R.string.your_credit_expired_on_s_you_must_pay_for_any_further_crossings_you_intend_to_make
                , it?.expirationDate?.let { it1 -> DateUtils.convertDateFormatToDateFormat(it1) })
            }
            if(crossings==0){
                binding.transferBtn.gone()
                binding.errorTxt.visible()
                binding.errorTxt.text = getString(R.string.you_have_no_credit_left_you_must_pay_for_any_further_crossings_you_intend_to_make)
            }
        }


    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.transferBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.transfer_btn -> {
                val bundle = Bundle().apply {
                    putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    putParcelable(Constants.NAV_DATA_KEY, data)
                }
                findNavController().navigate(
                    R.id.action_crossing_details_to_find_vehicles,
                    bundle
                )
            }
            R.id.cancel_btn -> {
                findNavController().popBackStack()
            }
            R.id.nextBtn -> {
                findNavController().navigate(
                    R.id.action_crossing_details_to_create_account
                )
            }

        }



    }


}