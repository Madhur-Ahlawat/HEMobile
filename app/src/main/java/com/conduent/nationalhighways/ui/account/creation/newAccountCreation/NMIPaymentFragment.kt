package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.AddCardModel
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.databinding.NmiPaymentFragmentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.google.gson.Gson
import java.util.Locale


class NMIPaymentFragment : BaseFragment<NmiPaymentFragmentBinding>(),View.OnClickListener {



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NmiPaymentFragmentBinding = NmiPaymentFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.addJavascriptInterface(JsObject(this), "Android")

//        binding.webView.setInitialScale(1)

        binding.webView.settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        binding.webView.webChromeClient = consoleListener



        binding.webView.loadUrl("file:///android_asset/NMIPayments.html")
    }

    fun callJavaScript(view: View?) {
        Log.v(null, "Calling java Script")
        binding.webView.loadUrl("javascript:callback()")
    }

    override fun initCtrl() {
        setupWebView()
    }

    override fun observer() {
    }
    private val consoleListener = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            val url: String = consoleMessage.message()
            Toast.makeText(requireContext(),url,Toast.LENGTH_SHORT).show()
            val check: Boolean = "tokenType" in url
           /* if (check) {
                binding.webview.gone()
                binding.mcvContainer.visible()
                val responseModel: CardResponseModel = Gson().fromJson(consoleMessage.message(), CardResponseModel::class.java)

                cardModel = AddCardModel(addressLine1 = "", addressLine2 = "", bankRoutingNumber = "",
                    cardNumber = responseModel.token, cardType =  responseModel.card?.type?.uppercase(
                        Locale.ROOT), city = "",
                    country = "", customerVaultId = null, easyPay = "Y",
                    expMonth = responseModel.card?.exp?.substring(0, 2), expYear = responseModel.card?.exp?.substring(2, 4), firstName = "",
                    middleName = "",lastName = "", maskedCardNumber = responseModel.card?.number,
                    paymentType = "card", primaryCard = "N", state = "",
                    zipcode1 = "", zipcode2 = "",cvv=null)

                binding.apply {
                    tieCardNo.setText( cardModel?.maskedCardNumber?:"")
                    tieExpiryDate.setText("${cardModel?.expMonth}/${cardModel?.expYear}")
                    tieName.setText( responseModel.check?.name?:"")
                    tieCVV.setText("***")
                }

                val fullName: List<String?>? = responseModel.check?.name?.split(" ")
                when (fullName?.size) {
                    1 -> { cardModel?.run {
                        firstName = fullName[0]
                        middleName = ""
                        lastName = ""
                    } }
                    2 -> { cardModel?.run {
                        firstName=fullName[0]
                        middleName=""
                        lastName=fullName[1]
                    } }
                    3 -> { cardModel?.run {
                        firstName=fullName[0]
                        middleName=fullName[1]
                        lastName=fullName[2]
                    } }
                }

            }else {
                findNavController().navigate(R.id.action_paymentMethodCardFragment_to_paymentMethodErrorFragment)
            }*/
            return true
        }
    }


    override fun onClick(v: View?) {


    }
    internal class JsObject(nmiPaymentFragment: NMIPaymentFragment) {
        @JavascriptInterface
        fun receiveMessage(data: String) {
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