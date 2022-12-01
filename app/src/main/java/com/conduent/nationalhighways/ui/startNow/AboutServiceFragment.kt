package com.conduent.nationalhighways.ui.startNow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAboutServiceBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutServiceFragment : BaseFragment<FragmentAboutServiceBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAboutServiceBinding {

        return FragmentAboutServiceBinding.inflate(inflater, container, false)
    }

    override fun init() {
        // not need to set any toolbar

        AdobeAnalytics.setScreenTrack(
            "about service",
            "about service",
            "english",
            "about service",
            "dart charge",
            "about service",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
        binding.apply {
            tvLink.setOnClickListener(this@AboutServiceFragment)
        }
    }

    override fun observer() {

        // nothing here to observe
    }

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar(getString(R.string.str_about_this_service))
    }
    override fun onClick(v: View?) {
        v?.let {
            when(v.id)
            {
                R.id.tv_link->{

                }
            }
        }

    }
}