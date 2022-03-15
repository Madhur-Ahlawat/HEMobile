package com.heandroid.ui.vehicle.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.heandroid.R
import com.heandroid.data.model.payment.CardModel
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.databinding.FragmentMakeOffPaymentCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.CardNumberFormatterTextWatcher
import com.heandroid.utils.extn.addExpriryListner
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible


class MakeOffPaymentCardFragment : BaseFragment<FragmentMakeOffPaymentCardBinding>(),View.OnClickListener {


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentCardBinding = FragmentMakeOffPaymentCardBinding.inflate(inflater,container,false)
    override fun init() {
        binding.webview.setSettings()

        binding.webview.loadUrl("file:///android_asset/NMI.html")
        binding.webview.webViewClient = object : WebViewClient() {}

        binding.webview.webChromeClient = object : WebChromeClient(){

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("PaymentLog", " console log  ${consoleMessage.message()}")

                val url : String = consoleMessage.message()
                val check : Boolean = "tokenType" in url

                Log.d("PaymentLog","url  $url" )

                if (check){
                    Toast.makeText(context,url, Toast.LENGTH_LONG).show()
                    binding.webview.gone()
                    binding.mcvContainer.visible()
                    val responseModel : CardResponseModel = Gson().fromJson(consoleMessage.message(),CardResponseModel::class.java)
                    binding.model= CardModel(cardNo = responseModel.card.number,
                                             name = responseModel.wallet.billingInfo.firstName +" "+responseModel.wallet.billingInfo.firstName,
                                             expiry = responseModel.card.exp,cvv = "***")
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

 fun WebView.setSettings(){
    val webSettings = this.settings;
    webSettings.javaScriptEnabled = true
    webSettings.domStorageEnabled = true
    webSettings.loadWithOverviewMode = true
    webSettings.useWideViewPort = true
    webSettings.builtInZoomControls = true
    webSettings.displayZoomControls = false
    webSettings.setSupportZoom(true)

    webSettings.defaultTextEncodingName = "utf-8";
}
