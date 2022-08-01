package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.heandroid.data.model.checkpaidcrossings.UnUsedChargesModel
import com.heandroid.databinding.FragmentUnusedChargesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.checkpaidcrossings.adapter.OnChangeClickListener
import com.heandroid.ui.checkpaidcrossings.adapter.UnUsedChargesAdapter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnUsedChargesFragment : BaseFragment<FragmentUnusedChargesBinding>(), OnChangeClickListener {

    private val mList = mutableListOf<UnUsedChargesModel?>()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUnusedChargesBinding.inflate(inflater, container, false)

    override fun init() {
        val mData =
            arguments?.getParcelable<CheckPaidCrossingsResponse?>(Constants.CHECK_PAID_CHARGE_DATA_KEY)
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


