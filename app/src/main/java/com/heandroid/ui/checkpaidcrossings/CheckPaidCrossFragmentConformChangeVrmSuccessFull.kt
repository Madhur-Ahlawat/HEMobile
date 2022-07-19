package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.databinding.FragmentEnterVrmCheckChangeConformSuccessBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPaidCrossFragmentConformChangeVrmSuccessFull :
    BaseFragment<FragmentEnterVrmCheckChangeConformSuccessBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEnterVrmCheckChangeConformSuccessBinding.inflate(inflater, container, false)

    override fun init() {
        val mDataVrmRef =
            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)
        val mVrmDetailsDvla =
            arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)

        binding.topTxt.text = getString(R.string.str_your_change_to_ref_no_made, mDataVrmRef?.ref)
        binding.regNum.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.plateNumber
        binding.tvDate.text = DateUtils.convertDateFormat(DateUtils.currentDate(), 0)
    }

    override fun initCtrl() {}

    override fun observer() {}

    override fun onClick(v: View?) {}
}