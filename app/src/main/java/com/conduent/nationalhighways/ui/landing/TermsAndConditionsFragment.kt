package com.conduent.nationalhighways.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentTermsAndConditionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TermsAndConditionsFragment : BaseFragment<FragmentTermsAndConditionsBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTermsAndConditionsBinding {
        return FragmentTermsAndConditionsBinding.inflate(inflater, container, false)


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
        binding.layoutDartChargeGeneral.setOnClickListener {
            findNavController().navigate(R.id.action_termsAndConditionsFragment_to_generalTermsAndConditions)
        }
        binding.privacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_termsAndConditionsFragment_to_privacyPolicyFragment)
        }
        binding.termsandconditions.setOnClickListener {
            findNavController().navigate(R.id.action_termsAndConditionsFragment_to_paygtermsandconditions)
        }
    }

    override fun observer() {
    }
}