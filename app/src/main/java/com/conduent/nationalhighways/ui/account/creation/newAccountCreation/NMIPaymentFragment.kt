package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.conduent.nationalhighways.databinding.NmiPaymentFragmentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class NMIPaymentFragment : BaseFragment<NmiPaymentFragmentBinding>(),View.OnClickListener {



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NmiPaymentFragmentBinding = NmiPaymentFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(JsObject(), "Android")

//        binding.webView.setInitialScale(1)

        binding.webView.settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        binding.webView.loadUrl("file:///android_asset/NMIPayments.html")
    }

    override fun initCtrl() {
        setupWebView()
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {


    }
    internal class JsObject {
        @JavascriptInterface
        fun receiveMessage(data: String) {
            Log.i("WebView", "postMessage data=$data")

        }
    }

    private fun setupWebView() {

        val webViewClient: WebViewClient = object: WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progress.visibility = View.VISIBLE
                view?.loadUrl("javascript:(function() {" +
                        "function receiveMessage(event) {\n" +
                        "Android.receiveMessage(JSON.stringify(event.data));\n" +
                        "}" +
                        "window.addEventListener(\"message\", receiveMessage, false);"+
                        "})()"
                )
                Log.i("Custom Web View", "onPageStarted $url")
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progress.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
        binding.webView.webViewClient = webViewClient

        binding.webView.settings.defaultTextEncodingName = "utf-8"
    }




}