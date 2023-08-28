package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentCrossingDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CrossingDetailsFragment : BaseFragment<FragmentCrossingDetailsBinding>(), View.OnClickListener {

    private var data : CheckPaidCrossingsResponse? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCrossingDetailsBinding = FragmentCrossingDetailsBinding.inflate(inflater, container, false)

    override fun init() {

        data = navData as CheckPaidCrossingsResponse

        data.let {
            binding.fullName.text = it?.referenceNumber
            binding.companyName.text = it?.plateNumber
            binding.address.text = it?.unusedTrip + "crossings"
            binding.emailAddress.text = it?.expirationDate
        }


    }

    override fun initCtrl() {
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

        }



    }


}