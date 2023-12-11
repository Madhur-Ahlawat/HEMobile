package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.webkit.WebViewAssetLoader
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.databinding.FragmentGeneralTermsAndConditionsBinding
import com.conduent.nationalhighways.databinding.FragmentTermsAndConditionsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.checkpaidcrossings.CheckPaidCrossingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.LocalContentWebViewClient
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GeneralTermsAndConditionsFragment : BaseFragment<FragmentGeneralTermsAndConditionsBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGeneralTermsAndConditionsBinding {
        binding = FragmentGeneralTermsAndConditionsBinding.inflate(inflater, container, false)

        return binding
    }

    override fun init() {
        val mAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireActivity()))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(requireActivity()))
            .build()
        binding.webView.webViewClient = LocalContentWebViewClient(mAssetLoader)
//        webView.loadUrl("https://appassets.androidplatform.net/assets/termsandconditions.html")
        if (NewCreateAccountRequestModel.prePay) {
            binding.webView.loadUrl("file:///android_res/raw/termsandconditionspage.html")

//            "https://pay-dartford-crossing-charge.service.gov.uk/dart-charge-terms-conditions"

        } else {
            binding.webView.loadUrl("file:///android_res/raw/paygtermsandcondition.html")

//            "https://pay-dartford-crossing-charge.service.gov.uk/payg-terms-condtions"

        }

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
    }

    override fun observer() {
    }
}