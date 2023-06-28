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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountResponseModel
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.databinding.NmiPaymentFragmentBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CreateAccountViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NMIPaymentFragment : BaseFragment<NmiPaymentFragmentBinding>(),View.OnClickListener{


    private val viewModel: CreateAccountViewModel by viewModels()
    private var loader: LoaderDialog? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NmiPaymentFragmentBinding = NmiPaymentFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(JsObject(), "appInterface")


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
        observe(viewModel.account, ::handleAccountResponse)
    }
    private fun handleAccountResponse(response: Resource<CreateAccountResponseModel?>?) {
        showLoader()
//        when (response) {
//            is Resource.Success -> {
//                countriesCodeList.clear()
//                response.data?.forEach {
//                    it?.value?.let { it1 -> countriesCodeList.add(it1) }
//                }
//                countriesCodeList.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
//
//
//                if (countriesCodeList.contains(Constants.UK_CODE)) {
//                    countriesCodeList.remove(Constants.UK_CODE)
//                    countriesCodeList.add(0, Constants.UK_CODE)
//                }
//
//                binding.apply {
//                    inputCountry.dataSet.addAll(countriesCodeList)
//                    inputCountry.setSelectedValue(Constants.UK_CODE)
//                }
//
//            }
//
//            is Resource.DataError -> {
//                ErrorUtil.showError(binding.root, response.errorMsg)
//            }
//
//            else -> {
//            }
//
//        }
    }

    override fun onClick(v: View?) {


    }
    inner class JsObject {
        @JavascriptInterface
        fun postMessage(data: String) {
            Log.i("WebView", "postMessage data=$data")
            if(data.isNotEmpty() ){
                MainScope().launch {
                    when(data){
                        "NMILoaded" ->{
                            hideLoader()
                        }
                        "3DStarted" ->{
                            showLoader()
                        }
                        "3DSLoaded" ->{
                            hideLoader()
                        }
                    }
                    if(data.contains("cardHolderAuth")){
                        val gson = Gson()
                        val paymentSuccessResponse = gson.fromJson(data, PaymentSuccessResponse::class.java)
                        if(paymentSuccessResponse.cardHolderAuth.equals("verified",true)){
                            hideLoader()

//                    callAccountCreationApi()
                            NewCreateAccountRequestModel.isBackButtonVisible = false
                            findNavController().navigate(R.id.action_nmiPaymentFragment_to_accountCreatedSuccessfullyFragment)

                        }
                    }
                }
            }

        }


    }

    private fun showLoader() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
    }

    private fun hideLoader() {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
    }

    private fun callAccountCreationApi() {
        val data = NewCreateAccountRequestModel
        val model = AccountCreationRequest()
        model.stateType = "HE"
        model.creditCExpYear = "2025"
        viewModel.createAccountNew(model)

    }
    private fun setupWebView() {

        val webViewClient: WebViewClient = object: WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showLoader()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val amount = arguments?.getInt(Constants.DATA)
                view?.loadUrl("javascript:(function(){document.getElementById('amount').value = '"+amount+"';})()");
                super.onPageFinished(view, url)
            }
        }
        binding.webView.webViewClient = webViewClient
    }
}