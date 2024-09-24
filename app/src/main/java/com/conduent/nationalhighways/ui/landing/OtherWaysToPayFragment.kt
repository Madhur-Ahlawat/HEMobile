package com.conduent.nationalhighways.ui.landing

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentOtherWaysToPayBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtherWaysToPayFragment : BaseFragment<FragmentOtherWaysToPayBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOtherWaysToPayBinding {
        return FragmentOtherWaysToPayBinding.inflate(inflater, container, false)

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
        binding?.tvGuideline?.setMovementMethod(LinkMovementMethod.getInstance())
        binding?.txtPayThroughWebsite?.setMovementMethod(LinkMovementMethod.getInstance())
    }

    override fun observer() {
    }
}