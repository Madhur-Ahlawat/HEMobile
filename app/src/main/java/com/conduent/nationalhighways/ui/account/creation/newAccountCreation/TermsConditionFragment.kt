package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentTermsConditionBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsConditionFragment : BaseFragment<FragmentTermsConditionBinding>() {
    private var url: String = ""
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTermsConditionBinding =
        FragmentTermsConditionBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader = LoaderDialog()


        setupWebView()

        url = arguments?.getString(Constants.TERMSCONDITIONURL).toString()
    }

    override fun init() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl("file:///android_asset/termsandconditionspage.html")

    }

    override fun observer() {
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
                hideLoader()



                super.onPageFinished(view, url)
            }
        }
        binding.webView.webViewClient = webViewClient
    }

    private fun showLoader() {
        binding.progressBar.visibility=View.VISIBLE
    }

    private fun hideLoader() {
       binding.progressBar.visibility= View.GONE
    }


}