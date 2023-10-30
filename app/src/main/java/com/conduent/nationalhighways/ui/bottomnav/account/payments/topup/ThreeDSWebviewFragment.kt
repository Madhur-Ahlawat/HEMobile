package com.conduent.nationalhighways.ui.bottomnav.account.payments.topup

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.databinding.FragmentThreeDSWebviewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


class ThreeDSWebviewFragment : BaseFragment<FragmentThreeDSWebviewBinding>(), View.OnClickListener {

    private var responseModel: CardResponseModel? = null
    private var checkBox: Boolean = false
    private var topUpAmount = "100"
    private var currentBalance: String = ""
    private var personalInformation: PersonalInformation? = null
    private var paymentListSize: Int = 0
    private var flow: String = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentThreeDSWebviewBinding =
        FragmentThreeDSWebviewBinding.inflate(inflater, container, false)

    override fun init() {
        WebView.setWebContentsDebuggingEnabled(true)
//        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(JsObject(), "appInterface")



//        binding.webView.settings.apply {
//            loadWithOverviewMode = true
//            useWideViewPort = true
//        }
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.loadUrl("file:///android_asset/threeDSGateWay.html")
        val file = File("file:///android_asset/threeDSGateWay.html")
        file.setReadable(true, true)

        if (file.exists()) {
            Log.e("TAG", "init:exists " )
            // File exists, proceed with loading
        } else {
            Log.e("TAG", "init:not exists " )
            // File doesn't exist, handle the error
        }



    }

    inner class JsObject {
        @JavascriptInterface
        fun postMessage(data: String) {
            Log.i("WebView", "postMessage data=$data")
            if (data.isNotEmpty()) {
                MainScope().launch {
                    when (data) {
                        "NMILoaded", "ValidationFailed", "3DSLoaded", "timedOUt", "cancelClicked" -> hideLoader()
                        "3DStarted" -> showLoader()
                        "3DSNotIntiated" -> showErrorPopup(resources.getString(R.string.payment_failed))
                        "cardtypeerror" -> showErrorPopup(resources.getString(R.string.payment_incorrect))

                        else -> {
                            if (data == "paymentFailed") {
                                hideLoader()
                                findNavController().navigate(R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment)
                            }
                        }
                    }

                    val check: Boolean = "tokenType" in data

                    if (check) {

                        responseModel =
                            Gson().fromJson(data, CardResponseModel::class.java)
                        responseModel?.checkCheckBox = checkBox

                    }

                    if (data.contains("cardHolderAuth")) {
                        val gson = Gson()
                        val paymentSuccessResponse =
                            gson.fromJson(data, PaymentSuccessResponse::class.java)
                        if (paymentSuccessResponse.cardHolderAuth.equals("verified", true)) {
                            val gson = Gson()
                            val paymentSuccessResponse =
                                gson.fromJson(data, PaymentSuccessResponse::class.java)
                            val bundle = Bundle()
                            bundle.putParcelable(Constants.DATA, responseModel)
                            bundle.putParcelable(Constants.NEW_CARD, paymentSuccessResponse)
                            bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount.toDouble())
                            bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                            bundle.putString(Constants.NAV_FLOW_KEY, flow)
                            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                            bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                            findNavController().navigate(
                                R.id.action_nmiPaymentFragment_to_accountSuspendedFinalPayFragment,
                                bundle
                            )
                        }
                    }
                }
            }

        }


    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorPopup(errorMsg: String) {
        ErrorUtil.showError(binding.root, errorMsg)
    }


    override fun initCtrl() {
//        topUpAmount = arguments?.getDouble(Constants.DATA).toString()
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)

        }
        flow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()
        paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0
        setupWebView()

    }


    private fun setupWebView() {

        val webViewClient: WebViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showLoader()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
//                val amount: Double = arguments?.getDouble(Constants.DATA) ?: 0.00
//                val doubleAmount = String.format("%.2f", amount)
                val doubleAmount = "10"
                hideLoader()
                view?.loadUrl("javascript:(function(){document.getElementById('customerVaultId').innerText = '100316189_2f1aa171a2';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('amount').innerText = '$doubleAmount';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('email').innerText = 'threeds@test.com';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('phone').innerText = '071727127712';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('city').innerText = 'Kkd';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('address1').innerText = 'address';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('firstName').innerText = 'address';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('lastName').innerText = 'address';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('postalCode').innerText = 'ls98bn';})()")


                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e(
                    "TAG",
                    "onReceivedError() called with: view = $view, request = $request, error = $error"
                )
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            binding.webView.evaluateJavascript("loaded();", null)
        } else {
            binding.webView.loadUrl("javascript:loaded();")
        }

        binding.webView.webViewClient = webViewClient

    }


    override fun observer() {

    }

    override fun onClick(p0: View?) {
    }


}