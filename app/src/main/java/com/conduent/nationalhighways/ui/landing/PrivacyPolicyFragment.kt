package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import com.conduent.nationalhighways.databinding.FragmentGeneralTermsAndConditionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.utils.LocalContentWebViewClient
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PrivacyPolicyFragment : BaseFragment<FragmentGeneralTermsAndConditionsBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGeneralTermsAndConditionsBinding {
        return FragmentGeneralTermsAndConditionsBinding.inflate(inflater, container, false)

    }

    override fun init() {
        val mAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireActivity()))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(requireActivity()))
            .build()
        binding.webView.webViewClient = LocalContentWebViewClient(mAssetLoader)
        binding.webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.startsWith("tel:")) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

        })
        binding.webView.loadUrl("file:///android_res/raw/privacypolicy.html")

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
    }

    override fun observer() {
    }
}