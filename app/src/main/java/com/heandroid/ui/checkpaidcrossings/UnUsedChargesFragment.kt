package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.heandroid.data.model.checkpaidcrossings.UnUsedChargesModel
import com.heandroid.databinding.FragmentUnusedChargesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnUsedChargesFragment : BaseFragment<FragmentUnusedChargesBinding>(),
    View.OnClickListener, OnChangeClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUnusedChargesBinding.inflate(inflater, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun init() {
        val mData =
            arguments?.getParcelable<CheckPaidCrossingsResponse?>(Constants.CHECK_PAID_CHARGE_DATA_KEY)!!
        val mDataVrmRef =
            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)!!
        val mList = mutableListOf<UnUsedChargesModel?>()
        mList.clear()
        for (value in 1..mData.unusedTrip!!.toInt()) {
            mList.add(UnUsedChargesModel(value, mDataVrmRef.vrm!!))
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter = UnUsedChargesAdapter(mList, this)


    }

    override fun initCtrl() {
    }

    override fun observer() {
    }


    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    override fun clickChange(index: Int) {

        arguments?.putInt("Index",index)
        findNavController().navigate(
            R.id.action_unUsedCharges_to_enterVrmFragment,
            arguments
        )

    }
}


