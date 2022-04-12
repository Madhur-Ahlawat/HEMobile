package com.heandroid.ui.bottomnav.account.payments.method

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.heandroid.R
import com.heandroid.data.model.payment.AddCardModel
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.databinding.FragmentPaymentMethodCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PaymentMethodCardFragment : BaseFragment<FragmentPaymentMethodCardBinding>(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var loader: LoaderDialog? = null

    private var cardModel : AddCardModel?=null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPaymentMethodCardBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager,"")
        binding.webview.loadSetting("file:///android_asset/NMI.html")
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<LinearLayout>(R.id.tabLikeButtonsLayout).gone()
    }

    override fun initCtrl() {
        binding.apply {
            webview.webViewClient = progressListener
            webview.webChromeClient = consoleListener
            btnAdd.setOnClickListener(this@PaymentMethodCardFragment)
            btnCancel.setOnClickListener(this@PaymentMethodCardFragment)
            cbDefault.setOnCheckedChangeListener(this@PaymentMethodCardFragment)
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAdd -> {
                val bundle= Bundle()
                bundle.putParcelable(Constants.DATA,cardModel)
                if(arguments?.getBoolean("edit") == true) findNavController().navigate(R.id.action_paymentMethodCardFragment_to_paymentMethodConfirmCardFragment,bundle)
                else findNavController().navigate(R.id.action_paymentMethodCardFragment_to_paymentMethodEditCardFragment,bundle)
            }

            R.id.btnCancel ->{ requireActivity().onBackPressed() }
        }
    }

    private val progressListener = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
             view?.loadUrl("file:///android_asset/NMI.html")
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            loader?.dismiss()
        }

    }

    private val consoleListener = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            val url: String = consoleMessage.message()
            val check: Boolean = "tokenType" in url
            if (check) {
                binding.webview.gone()
                binding.mcvContainer.visible()
                val responseModel: CardResponseModel = Gson().fromJson(consoleMessage.message(), CardResponseModel::class.java)

                cardModel = AddCardModel(addressLine1 = "", addressLine2 = "", bankRoutingNumber = "",
                                         cardNumber = responseModel.token, cardType =  responseModel.card.type.uppercase(Locale.ROOT), city = "",
                                         country = "", customerVaultId = null, easyPay = "Y",
                                         expMonth = responseModel.card.exp.substring(0, 2), expYear = responseModel.card.exp.substring(2, 4), firstName = "",
                                         middleName = "",lastName = "", maskedCardNumber = responseModel.card.number,
                                         paymentType = "card", primaryCard = "N", state = "",
                                         zipcode1 = "", zipcode2 = "",cvv=null)

                binding.apply {
                    tieCardNo.setText( cardModel?.maskedCardNumber?:"")
                    tieExpiryDate.setText("${cardModel?.expMonth}/${cardModel?.expYear}")
                    tieName.setText( responseModel.check.name?:"")
                    tieCVV.setText("***")
                }

                val fullName: List<String?>? = responseModel.check.name?.split(" ")
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

            }
            return true
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        cardModel?.default=isChecked
    }


}
