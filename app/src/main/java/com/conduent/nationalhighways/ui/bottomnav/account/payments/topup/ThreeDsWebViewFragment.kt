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
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentThreeDSWebviewBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ThreeDsWebViewFragment : BaseFragment<FragmentThreeDSWebviewBinding>(), View.OnClickListener {


    private var topUpAmount: Double = 0.0
    private var currentBalance: String = ""
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var paymentListSize: Int = 0
    private var flow: String = ""
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var position: Int = 0


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentThreeDSWebviewBinding =
        FragmentThreeDSWebviewBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
        
        binding.webView.addJavascriptInterface(JsObject(), "appInterface")

        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.DATA) != null) {
            paymentList = arguments?.getParcelableArrayList(Constants.DATA)
        }

        position = arguments?.getInt(Constants.POSITION, 0) ?: 0
        topUpAmount = arguments?.getDouble(Constants.PAYMENT_TOP_UP) ?: 0.0
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.loadUrl("file:///android_asset/ThreeDSGateway.html")


    }

    inner class JsObject {
        @JavascriptInterface
        fun postMessage(data: String) {
            Log.i("WebView", "postMessage data=$data")
            if (data.isNotEmpty()) {
                MainScope().launch {
                    when (data) {
                        "NMILoaded", "ValidationFailed", "3DSLoaded", "timedOUt" -> hideLoader()
                        "threeDSStarted" -> showLoader()
                        "cancelClicked" -> {
                            hideLoader()
                            findNavController().popBackStack()
                        }

                        else -> {
                            val gson = Gson()
                            val paymentSuccessResponse =
                                gson.fromJson(data, PaymentSuccessResponse::class.java)

                            if (paymentSuccessResponse.cardHolderAuth.equals("verified", true)) {
                                val bundle = Bundle()
                                bundle.putParcelable(Constants.NEW_CARD, paymentSuccessResponse)
                                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                                bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount)
                                bundle.putInt(Constants.POSITION, position)
                                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                                bundle.putParcelable(
                                    Constants.ACCOUNTINFORMATION,
                                    accountInformation
                                )
                                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                                bundle.putString(Constants.NAV_FLOW_KEY, flow)
                                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                                bundle.putParcelableArrayList(
                                    Constants.DATA,
                                    paymentList as ArrayList
                                )
                                findNavController().navigate(
                                    R.id.action_threeDSWebViewFragment_to_accountSuspendedFinalPayFragment,
                                    bundle
                                )
                            }
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


    override fun initCtrl() {
//        topUpAmount = arguments?.getDouble(Constants.DATA).toString()
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
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
                val amount: Double = topUpAmount
                val doubleAmount = String.format("%.2f", amount)

                hideLoader()
                view?.loadUrl(
                    "javascript:(function(){document.getElementById('customerVaultId').innerText = '${
                        paymentList?.get(
                            position
                        )?.customerVaultId
                    }';})()"
                )
                view?.loadUrl("javascript:(function(){document.getElementById('amount').innerText = '$doubleAmount';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('email').innerText = '${personalInformation?.emailAddress}';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('phone').innerText = '${personalInformation?.phoneNumber}';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('city').innerText = '${personalInformation?.city}';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('address1').innerText = '${personalInformation?.addressLine1}';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('firstName').innerText = '${personalInformation?.firstName}';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('lastName').innerText = '${personalInformation?.lastName}';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('postalCode').innerText = '${personalInformation?.zipCode}';})()")


                binding.webView.loadUrl("javascript:loaded()")


                super.onPageFinished(view, url)

            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

            }
        }
        binding.webView.webViewClient = webViewClient


    }


    override fun observer() {

    }

    override fun onClick(p0: View?) {
    }


}