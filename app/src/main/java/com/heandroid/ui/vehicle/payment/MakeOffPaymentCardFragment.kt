package com.heandroid.ui.vehicle.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.heandroid.databinding.FragmentMakeOffPaymentCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.CardNumberFormatterTextWatcher
import com.heandroid.utils.extn.addExpriryListner
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MakeOffPaymentCardFragment : BaseFragment<FragmentMakeOffPaymentCardBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentCardBinding = FragmentMakeOffPaymentCardBinding.inflate(inflater,container,false)
    override fun init() {
        var webSettings = binding.webview.settings;
        webSettings.javaScriptEnabled = true;
        webSettings.domStorageEnabled = true;
        webSettings.loadWithOverviewMode = true;
        webSettings.useWideViewPort = true;
        webSettings.builtInZoomControls = true;
        webSettings.displayZoomControls = false;
        webSettings.setSupportZoom(true);
        binding.webview.getSettings().setPluginState(WebSettings.PluginState.ON);

        webSettings.defaultTextEncodingName = "utf-8";
        binding.webview.loadUrl("file:///android_asset/NMI.html");
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e("url","--"+url)
            }
        }


    }
    override fun initCtrl() {
        binding.tieCardNo.addTextChangedListener(CardNumberFormatterTextWatcher())
        binding.tieExpiryDate.addExpriryListner()
    }
    override fun observer() {}
}