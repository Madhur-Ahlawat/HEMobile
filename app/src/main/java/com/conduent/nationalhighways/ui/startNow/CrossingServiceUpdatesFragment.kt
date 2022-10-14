package com.conduent.nationalhighways.ui.startNow

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCrosssingServiceUpdateBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.extn.toolbar

class CrossingServiceUpdatesFragment : BaseFragment<FragmentCrosssingServiceUpdateBinding>() {

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
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}