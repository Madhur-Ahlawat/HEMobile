package com.conduent.nationalhighways.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAboutThisServiceBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutThisServiceFragment : BaseFragment<FragmentAboutThisServiceBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAboutThisServiceBinding {
        return FragmentAboutThisServiceBinding.inflate(inflater, container, false)
    }

    override fun init() {
        AdobeAnalytics.setScreenTrack(
            "home",
            "home",
            "english",
            "home",
            "splash",
            "home",
            sessionManager.getLoggedInUser()
        )
        if (requireActivity() is RaiseEnquiryActivity) {
            (requireActivity() as RaiseEnquiryActivity).focusToolBarRaiseEnquiry()
        }
    }

    override fun initCtrl() {
//        binding?.btnLearnMore?.setMovementMethod(LinkMovementMethod.getInstance())
        binding?.btnLearnMore?.setOnClickListener { findNavController().navigate(R.id.action_aboutthisserviceFragment_to_viewChargesFragment) }
    }

    override fun observer() {
    }
}