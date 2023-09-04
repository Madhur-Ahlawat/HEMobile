package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.conduent.nationalhighways.data.model.checkpaidcrossings.UnUsedChargesModel
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentUnusedChargesBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.checkpaidcrossings.adapter.OnChangeClickListener
import com.conduent.nationalhighways.ui.checkpaidcrossings.adapter.UnUsedChargesAdapter
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UnUsedChargesFragment : BaseFragment<FragmentUnusedChargesBinding>(), OnChangeClickListener {

    private val mList = mutableListOf<UnUsedChargesModel?>()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUnusedChargesBinding.inflate(inflater, container, false)

    override fun init() {

        AdobeAnalytics.setScreenTrack(
            "check crossings:unused crossings",
            "unused crossings",
            "english",
            "check crossings",
            "home",
            "check crossings:unused crossings",
            sessionManager.getLoggedInUser()
        )

        val mData =
            arguments?.getParcelable<CrossingDetailsModelsResponse?>(Constants.CHECK_PAID_CHARGE_DATA_KEY)
        val mDataVrmRef =
            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)

        mList.clear()
        mData?.unusedTrip?.toInt()?.let {
            for (value in 1..mData.unusedTrip.toInt()) {
                mList.add(UnUsedChargesModel(value, mDataVrmRef?.vrm))
            }
        }
        setView()

    }

    override fun initCtrl() {}

    override fun observer() {}

    override fun clickChange(index: Int) {
        AdobeAnalytics.setActionTrack(
            "click vehicle",
            "check crossings:unused crossings",
            "unused crossings",
            "english",
            "check crossings",
            "home",
            sessionManager.getLoggedInUser()
        )

        findNavController().navigate(
            R.id.action_unUsedCharges_to_enterVrmFragment
        )
    }

    private fun setView() {
        if (mList.isEmpty()) {
            binding.apply {
                rvHistory.gone()
                tvNoCrossing.visible()
            }
        } else {
            binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
            binding.rvHistory.adapter = UnUsedChargesAdapter(mList, this)
        }
    }
}


