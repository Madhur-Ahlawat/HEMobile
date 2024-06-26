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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.revalidate.RevalidateCardModel
import com.conduent.nationalhighways.databinding.FragmentThreeDSWebviewBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.auth.suspended.ManualTopUpViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.revalidatePayment.RevalidateViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ThreeDsWebViewFragment : BaseFragment<FragmentThreeDSWebviewBinding>(), View.OnClickListener {


    private var topUpAmount: Double = 0.0
    private var currentBalance: String = ""
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var paymentListSize: Int = 0
    private var flow: String = ""
    private var cardSecurityCode: String = ""
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var position: Int = 0
    private var cardValidationFirstTime: Boolean = true
    private var cardValidationSecondTime: Boolean = false
    private val revalidateViewModel: RevalidateViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentThreeDSWebviewBinding =
        FragmentThreeDSWebviewBinding.inflate(inflater, container, false)

    override fun init() {

        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.DATA) != null) {
            paymentList = arguments?.getParcelableArrayList(Constants.DATA)
        }

        if (arguments?.containsKey(Constants.CARD_SECURITY_CODE) == true) {
            cardSecurityCode = arguments?.getString(Constants.CARD_SECURITY_CODE) ?: ""
        }

        position = arguments?.getInt(Constants.POSITION, 0) ?: 0
        topUpAmount = arguments?.getDouble(Constants.PAYMENT_TOP_UP) ?: 0.0

        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }

        if (arguments?.containsKey(Constants.CARD_VALIDATION_FIRST_TIME) == true) {
            cardValidationFirstTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_FIRST_TIME) ?: true
        }
        if (arguments?.containsKey(Constants.CARD_VALIDATION_SECOND_TIME) == true) {
            cardValidationSecondTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_SECOND_TIME) ?: false
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.defaultTextEncodingName = "utf-8"


        binding.webView.addJavascriptInterface(JsObject(), "appInterface")

        binding.webView.setOnLongClickListener { true }
        binding.webView.isLongClickable = false

        setupWebView()


        binding.webView.loadUrl("file:///android_asset/ThreeDSGateway.html")


    }


    //    val a11yDelegate = object : AccessibilityDelegateCompat() {
//        override fun onInitializeAccessibilityNodeInfo(
//            host: View,
//            info: AccessibilityNodeInfoCompat
//        ) {
//            super.onInitializeAccessibilityNodeInfo(host, info)
//        }
//    }

    inner class JsObject {

        /*   @JavascriptInterface
           fun showKeyboard() {
               // Code to show the keyboard
               Log.e("appInterface", "showKeyboard called")
               requireActivity().runOnUiThread {
                   val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                   imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
               }
           }*/
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

                            if (navFlowCall == Constants.CARD_VALIDATION_REQUIRED) {
                                if (paymentSuccessResponse.cardHolderAuth.equals(
                                        "verified",
                                        true
                                    )
                                ) {
                                    payWithExistingCard(paymentSuccessResponse)
                                }
                            } else {
                                if (paymentSuccessResponse.cardHolderAuth.equals(
                                        "verified",
                                        true
                                    )
                                ) {
                                    val bundle = Bundle()
                                    bundle.putParcelable(Constants.NEW_CARD, paymentSuccessResponse)
                                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                                    bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount)
                                    bundle.putInt(Constants.POSITION, position)
                                    bundle.putParcelable(
                                        Constants.PERSONALDATA,
                                        personalInformation
                                    )
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


    }

    private fun showLoader() {
        try {
            binding.progressBar.visibility = View.VISIBLE
        } catch (_: Exception) {
            Log.e("TAG", "showLoader: exception")
        }
    }

    private fun hideLoader() {
        try {
            binding.progressBar.visibility = View.GONE
        } catch (_: Exception) {
            Log.e("TAG", "showLoader:- exception")
        }
    }


    override fun initCtrl() {
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

    }

    // JavaScript interface to show keyboard


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
                if (navFlowCall == Constants.CARD_VALIDATION_REQUIRED) {
                    view?.loadUrl("javascript:(function(){document.getElementById('currency').innerText = '${"GBP"}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('amount').innerText = '${"0"}';})()")
                    view?.loadUrl(
                        "javascript:(function(){document.getElementById('customerVaultId').innerText = '${
                            paymentList?.get(
                                position
                            )?.customerVaultId
                        }';})()"
                    )
                    view?.loadUrl("javascript:(function(){document.getElementById('email').innerText = '${personalInformation?.emailAddress}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('city').innerText = '${personalInformation?.city}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('address1').innerText = '${personalInformation?.addressLine1}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('firstName').innerText = '${personalInformation?.firstName}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('lastName').innerText = '${personalInformation?.lastName}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('postalCode').innerText = '${personalInformation?.zipCode}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('country').innerText = '${personalInformation?.country}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('challengeIndicator').innerText = '${"04"}';})()")

                } else {
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
                }

                binding.webView.loadUrl("javascript:loaded()")



                super.onPageFinished(view, url)

            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e("TAG", "onReceivedError: ", )
            }
        }
        binding.webView.webViewClient = webViewClient


    }


    override fun observer() {

        lifecycleScope.launch {
            observe(
                revalidateViewModel.paymentWithExistingCard,
                ::handlePaymentWithExistingCardResponse
            )
        }
    }

    override fun onClick(p0: View?) {
    }


    private fun payWithExistingCard(paymentSuccessResponse: PaymentSuccessResponse) {
        showLoader()
        val paymentModel = paymentList?.get(
            position
        )
        val model = RevalidateCardModel(
            cardType = paymentList?.get(
                position
            )?.cardType ?: "",
            cvv = cardSecurityCode,
            rowId = paymentModel?.rowId ?: "",
            cardVerify = "Y",
            source = "MOBILEAPP",
            saveCard = "Y",
            firstName = paymentModel?.firstName ?: "",
            lastName = paymentModel?.lastName ?: "",
            addressline1 = personalInformation?.addressLine1.toString().replace(" ", "").replace("null",""),
            city = personalInformation?.city?:"",
            state = personalInformation?.state?:"",
            country = personalInformation?.country?:"",
            zipcode1 = personalInformation?.zipcode?:"",
            customerVaultId = paymentModel?.customerVaultId ?: "",
            paymentType = "card",
            primaryCard = "Y",
            easyPay = "Y",
            cardCVV = cardSecurityCode,
            cavv = paymentSuccessResponse.cavv ?: "",
            xid = paymentSuccessResponse.xid ?: "",
            directoryServerId = paymentSuccessResponse.directoryServerId ?: "",
            eci = paymentSuccessResponse.eci ?: "",
            cardholderAuth = paymentSuccessResponse.cardHolderAuth ?: "",
            threeDsVer = paymentSuccessResponse.threeDsVersion ?: "",


            )
        Log.d("paymentRequest", Gson().toJson(model))
        revalidateViewModel.paymentWithExistingCard(model)
    }

    private fun handlePaymentWithExistingCardResponse(resource: Resource<PaymentMethodDeleteResponseModel?>?) {
        hideLoader()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.statusCode == "0") {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)
                    bundle.putInt(Constants.POSITION, position)
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    bundle.putParcelableArrayList(
                        Constants.PAYMENT_LIST_DATA,
                        paymentList as ArrayList
                    )
                    findNavController().navigate(
                        R.id.action_threeDSWebViewFragment_to_reValidateInfoFragment,
                        bundle
                    )
                }else{
                    redirectToErrorPage()
                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    if (resource.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
                       redirectToErrorPage()
                    }
                }
            }

            is Resource.Loading -> {

            }

            else -> {

            }
        }
    }

    private fun redirectToErrorPage() {
        val bundle = Bundle()
        bundle.putString(
            Constants.NAV_FLOW_FROM,
            navFlowFrom
        )
        bundle.putBoolean(Constants.CARD_VALIDATION_FIRST_TIME, cardValidationFirstTime)
        bundle.putBoolean(Constants.CARD_VALIDATION_SECOND_TIME, cardValidationSecondTime)
        bundle.putBoolean(Constants.CARD_VALIDATION_PAYMENT_FAIL, true)
        bundle.putBoolean(Constants.CARD_VALIDATION_EXISTING_CARD, true)
        bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)
        bundle.putParcelableArrayList(Constants.PAYMENT_LIST_DATA, paymentList as ArrayList)
        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
        findNavController().navigate(
            R.id.action_threeDSWebViewFragment_to_reValidateInfoFragment,
            bundle
        )

    }

}


