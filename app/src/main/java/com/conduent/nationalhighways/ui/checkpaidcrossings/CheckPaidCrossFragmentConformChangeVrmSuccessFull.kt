package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentEnterVrmCheckChangeConformSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossFragmentConformChangeVrmSuccessFull :
    BaseFragment<FragmentEnterVrmCheckChangeConformSuccessBinding>(), View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by activityViewModels()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEnterVrmCheckChangeConformSuccessBinding.inflate(inflater, container, false)

    override fun init() {

        AdobeAnalytics.setScreenTrack(
            "check crossings:vrm changed successfully",
            "vehicle",
            "english",
            "check crossings",
            "home",
            "check crossings:vrm changed successfully",
            sessionManager.getLoggedInUser()
        )

        val mVrmDetailsDvla =
            arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)

        binding.topTxt.text = getString(
            R.string.str_your_change_to_ref_no_made,
            viewModel.paidCrossingOption.value?.ref
        )
        binding.regNum.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.plateNumber
        binding.tvDate.text = DateUtils.convertDateFormat(DateUtils.currentDate(), 0)
    }

    override fun initCtrl() {
        binding.makeAnotherChange.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.makeAnotherChange -> {

                AdobeAnalytics.setActionTrack(
                    "another change",
                    "check crossings:vrm changed successfully",
                    "vehicle",
                    "english",
                    "check crossings",
                    "home",
                    sessionManager.getLoggedInUser()
                )

                findNavController().navigate(R.id.action_checkPaidCrossingChangeVrmConformSuccess_to_crossingCheck)
            }
        }
    }
}