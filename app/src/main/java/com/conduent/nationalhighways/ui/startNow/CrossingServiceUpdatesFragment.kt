package com.conduent.nationalhighways.ui.startNow

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCrosssingServiceUpdateBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.toolbar
import javax.inject.Inject

class CrossingServiceUpdatesFragment : BaseFragment<FragmentCrosssingServiceUpdateBinding>() {
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCrosssingServiceUpdateBinding {
        return FragmentCrosssingServiceUpdateBinding.inflate(inflater, container, false)

    }

    override fun init() {
        if (requireActivity() is StartNowBaseActivity) {
            requireActivity().toolbar(getString(R.string.str_crossing_service_update))
        }

        AdobeAnalytics.setScreenTrack(
            "crossing service update",
            "crossing service update",
            "english",
            "crossing service update",
            "dart charge",
            "crossing service update",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}