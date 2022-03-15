package com.heandroid.ui.vehicle.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.CardNumberFormatterTextWatcher
import com.heandroid.utils.extn.addExpriryListner


class MakeOffPaymentCardFragment : BaseFragment<FragmentMakeOffPaymentCardBinding>(),View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentCardBinding = FragmentMakeOffPaymentCardBinding.inflate(inflater,container,false)
    override fun init() {

        val webSettings = binding.webview.settings;
        webSettings.javaScriptEnabled = true;
        webSettings.domStorageEnabled = true;
        webSettings.loadWithOverviewMode = true;
        webSettings.useWideViewPort = true;
        webSettings.builtInZoomControls = true;
        webSettings.displayZoomControls = false;
        webSettings.setSupportZoom(true);

        webSettings.defaultTextEncodingName = "utf-8";
        binding.webview.loadUrl("file:///android_asset/NMI.html");
        binding.webview.webViewClient = object : WebViewClient() {
        }

        binding.webview.webChromeClient = object : WebChromeClient(){

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("PaymentLog", " console log  ${consoleMessage.message()}")
                val url : String = consoleMessage.message()
                val check : Boolean = "tokenType" in url
                Log.d("PaymentLog","url  $url" )

                if (check){
                    Toast.makeText(context,url, Toast.LENGTH_LONG).show()
                }
                return true
            }


        }

    }
    override fun initCtrl() {
        binding.tieCardNo.addTextChangedListener(CardNumberFormatterTextWatcher())
        binding.tieExpiryDate.addExpriryListner()
        binding.btnContinue.setOnClickListener(this)
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue -> {
                findNavController().navigate(R.id.action_makeOffPaymentCardFragment_to_makeOffPaymentConfirmationFragment)
            }
        }
    }


}