package com.conduent.nationalhighways.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentReminderStatusBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderStatusFragment : BaseFragment<FragmentReminderStatusBinding>() {

    var geofenceNotification: Boolean = false
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReminderStatusBinding =
        FragmentReminderStatusBinding.inflate(inflater, container, false)

    override fun init() {
        geofenceNotification = arguments?.getBoolean(Constants.GEO_FENCE_NOTIFICATION) ?: false
        if (geofenceNotification) {
            binding.titleTv.text = resources.getString(R.string.str_notifications_enabled)
        } else {
            binding.titleTv.text = resources.getString(R.string.str_notifications_disabled)
        }

        binding.btnContinue.setOnClickListener {
            requireActivity().startNormalActivityWithFinish(LandingActivity::class.java)
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

}