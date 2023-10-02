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

    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isChecked = false
    private var isPushNotificationChecked = true
    private var count = 1
    var apiState = Constants.UNAVAILABLE
    var apiEndTime: String = ""

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
        LandingActivity.showToolBar(true)
        LandingActivity.setToolBarTitle("Terms & Conditions")
        val webSetting: WebSettings = binding.webView.getSettings()
        webSetting.builtInZoomControls = false
        val mAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireActivity()))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(requireActivity()))
            .build()
        binding.webView.webViewClient = LocalContentWebViewClient(mAssetLoader)
//        webView.loadUrl("https://appassets.androidplatform.net/assets/termsandconditions.html")
        binding.webView.loadUrl("file:///android_res/raw/termsandconditionspage.html")

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
    private fun getBundleData(state: String?,endTime:String?=null): Bundle? {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.SERVICE_TYPE, state)
        if(endTime!=null && endTime.replace("null","").isNotEmpty()){
            bundle.putString(Constants.END_TIME, endTime)
        }
        return bundle
    }

    private fun openUrlInWebBrowser() {
        val url = Constants.PCN_RESOLVE_URL
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            startActivity(Intent.createChooser(this, "Browse with"))
        }
    }


}