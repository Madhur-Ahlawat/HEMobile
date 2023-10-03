package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.databinding.FragmentGuidanceAndDocumentsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GuidanceAndDocumentsFragment : BaseFragment<FragmentGuidanceAndDocumentsBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGuidanceAndDocumentsBinding {
        binding = FragmentGuidanceAndDocumentsBinding.inflate(inflater, container, false)

        return binding
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

    }

    override fun initCtrl() {
        binding.btnFeedbackToImproveService.setOnClickListener {
        }

        binding.layoutAboutThisService.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_aboutthisserviceFragment)
        }

        binding.layoutContactDartCharge.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceDocumentsFragment_to_contactDartChargeFragment)
        }

        binding.layoutUnderstandingChargesAndFinesFines.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_viewChargesFragment)
        }

        binding.layoutTermsAndConditions.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_termsandconditions)
        }

        binding.layoutThirdPartySoftware.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_thirdPartySoftwareFragment)
        }
    }

    override fun observer() {
    }
}