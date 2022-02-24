package com.heandroid.ui.startNow

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentAboutServiceBinding
import com.heandroid.databinding.FragmentCrosssingServiceUpdateBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.toolbar

class CrossingServiceUpdatesFragment : BaseFragment<FragmentCrosssingServiceUpdateBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCrosssingServiceUpdateBinding {
        return FragmentCrosssingServiceUpdateBinding.inflate(inflater, container, false)

    }

    override fun init() {
        requireActivity().toolbar(getString(R.string.str_crossing_service_update))
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}