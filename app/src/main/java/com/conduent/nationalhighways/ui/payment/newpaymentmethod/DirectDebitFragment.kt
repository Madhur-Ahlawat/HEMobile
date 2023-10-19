package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.payment.SaveNewCardRequest
import com.conduent.nationalhighways.databinding.FragmentDirectDebitBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DirectDebitFragment : BaseFragment<FragmentDirectDebitBinding>() {

    private val paymentMethodViewModel: PaymentMethodViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDirectDebitBinding = FragmentDirectDebitBinding.inflate(inflater, container, false)


    override fun initCtrl() {


        setupWebView()

    }

    override fun observer() {
        lifecycleScope.launch() {
            observe(paymentMethodViewModel.saveDirectDebitNewCard, ::handleSaveNewCardResponse)

        }
    }

    private fun handleSaveNewCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        hideLoader()
        when (status) {
            is Resource.Success -> {

                if (status.data?.statusCode?.equals("0") == true) {
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.CARD_IS_ALREADY_REGISTERED,
                        Constants.DIRECT_DEBIT
                    )


                    findNavController().navigate(
                        R.id.directDebitFragment_to_paymentSuccessFragment2,
                        bundle
                    )
                } else if (status.data?.statusCode?.equals("1337") == true) {
                    displayCustomMessage(
                        getString(R.string.str_warning),
                        getString(R.string.the_card_you_are_trying_to_add_is_already),
                        getString(R.string.str_add_another_card_small),  getString(R.string.cancel),
                        object : DialogPositiveBtnListener {
                            override fun positiveBtnClick(dialog: DialogInterface) {
                                val fragmentId = findNavController().currentDestination?.id
                                findNavController().popBackStack(fragmentId!!,true)
                                findNavController().navigate(fragmentId,arguments)
                            }
                        },
                        object : DialogNegativeBtnListener {
                            override fun negativeBtnClick(dialog: DialogInterface) {
                                findNavController().navigate(R.id.action_directDebitFragment_to_paymentMethodFragment)
                            }
                        })

                    /* val bundle = Bundle()

                     bundle.putString(
                         Constants.CARD_IS_ALREADY_REGISTERED,
                         Constants.CARD_IS_ALREADY_REGISTERED
                     )
                     findNavController().navigate(
                         R.id.directDebitFragment_to_paymentSuccessFragment2,
                         bundle
                     )*/
                } else {
                    val bundle = Bundle()

                    bundle.putString(
                        Constants.CARD_IS_ALREADY_REGISTERED,
                        Constants.DIRECT_DEBIT_NOT_SET_UP
                    )
                    findNavController().navigate(
                        R.id.directDebitFragment_to_paymentSuccessFragment2,
                        bundle
                    )

                }
            }

            is Resource.DataError -> {
                if (status.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    val bundle = Bundle()

                    bundle.putString(
                        Constants.CARD_IS_ALREADY_REGISTERED,
                        Constants.DIRECT_DEBIT_NOT_SET_UP
                    )
                    findNavController().navigate(
                        R.id.directDebitFragment_to_paymentSuccessFragment2,
                        bundle
                    )
                }
            }

            else -> {
            }
        }


    }

    override fun init() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl("https://customer.nuapaytest.com/en/signup/sign-up-to-dart-charge/")

        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.saveFormData = true
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.allowFileAccessFromFileURLs = true
        binding.webView.settings.allowUniversalAccessFromFileURLs = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.isClickable = true
        binding.webView.webChromeClient = WebChromeClient()

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
                if (url?.contains("https://web-highwaystest.services.conduent.com/Dashboard/account-management/payment/eDD/response/success?token") == true) {
                    val uri = Uri.parse(binding.webView.url)
                    val encodedschemeid = uri.getQueryParameter("encodedschemeid")
                    val mandateid = uri.getQueryParameter("mandateid")


                    if (encodedschemeid != null && mandateid != null) {
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.CARD_IS_ALREADY_REGISTERED,
                            Constants.DIRECT_DEBIT
                        )



                        addNewCardApi(encodedschemeid, mandateid)

                    }
                }

                super.onPageStarted(view, url, favicon)
                return

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideLoader()

                super.onPageFinished(view, url)
            }
        }
        binding.webView.webViewClient = webViewClient
    }

    private fun addNewCardApi(encodedschemeid: String, mandateid: String) {
        showLoader()
        val saveDirectDebitNewCard = SaveNewCardRequest(encodedschemeid, mandateid)

        paymentMethodViewModel.saveDirectDebitNewCard(saveDirectDebitNewCard)

    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE

    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE

    }

}