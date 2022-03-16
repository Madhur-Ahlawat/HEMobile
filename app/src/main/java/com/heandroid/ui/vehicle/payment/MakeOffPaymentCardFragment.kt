package com.heandroid.ui.vehicle.payment

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
import com.heandroid.utils.extn.loadSetting
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakeOffPaymentCardFragment : BaseFragment<FragmentMakeOffPaymentCardBinding>(),View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMakeOffPaymentCardBinding = FragmentMakeOffPaymentCardBinding.inflate(inflater,container,false)
    override fun init() {
        binding.webview.loadSetting("file:///android_asset/NMI.html")
    }
    override fun initCtrl() {
        binding.apply {
            tieCardNo.addTextChangedListener(CardNumberFormatterTextWatcher())
            tieExpiryDate.addExpriryListner()
            btnContinue.setOnClickListener(this@MakeOffPaymentCardFragment)
            webview.webChromeClient=consoleListener
        }
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue -> {
                findNavController().navigate(R.id.action_makeOffPaymentCardFragment_to_makeOffPaymentConfirmationFragment)
            }
        }
    }

    private val consoleListener = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            val url : String = consoleMessage.message()
            val check : Boolean = "tokenType" in url
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
