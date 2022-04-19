package com.heandroid.ui.bottomnav.dashboard.topup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.heandroid.R
import com.heandroid.data.model.manualtopup.PaymentWithExistingCardModel
import com.heandroid.data.model.manualtopup.PaymentWithNewCardModel
import com.heandroid.data.model.payment.CardListResponseModel
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.data.model.payment.PaymentMethodDeleteResponseModel
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.databinding.FragmentPaymentMethodCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.loadSetting
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*


@AndroidEntryPoint
class ManualTopUpAddCardFragment : BaseFragment<FragmentPaymentMethodCardBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null

    private var cardModel : PaymentWithNewCardModel?=null
    private val viewModel : ManualTopUpViewModel by viewModels()
    private val paymentViewModel : PaymentMethodViewModel by viewModels()
    private var isAlready : Boolean =false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPaymentMethodCardBinding.inflate(inflater,container,false)


    override fun init() {
        isAlready=false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager,"")
        binding.webview.loadSetting("file:///android_asset/NMI.html")
        binding.cbDefault.gone()
    }

    override fun initCtrl() {
        binding.apply {
            webview.webViewClient = progressListener
            webview.webChromeClient = consoleListener
            btnAdd.setOnClickListener(this@ManualTopUpAddCardFragment)
            btnCancel.setOnClickListener(this@ManualTopUpAddCardFragment)
        }
    }

    override fun observer() {
        observe(viewModel.paymentWithNewCard,::handlePaymentWithNewCardResponse)
        observe(paymentViewModel.accountDetail,::handleAccountDetailResponse)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAdd -> {
                isAlready=true
                loader?.show(requireActivity().supportFragmentManager,"")
                paymentViewModel.accountDetail()
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
                Log.e("payment token ",responseModel.toString())
                cardModel= PaymentWithNewCardModel(transactionAmount = arguments?.getString("amount"),
                                                   cardType = responseModel.card.type.uppercase(Locale.ROOT), cardNumber = responseModel.token,
                                                   cvv=responseModel.card.hash, expMonth = responseModel.card.exp.substring(0, 2),
                                                   expYear = "20${responseModel.card.exp.substring(2, 4)}" , saveCard = "Y", useAddressCheck = "N",
                                                   bankRoutingNumber = "", paymentType = "card", maskedNumber =  responseModel.card.number,
                                                   firstName = "", middleName = "", lastName = "", primaryCard = "N", easyPay = "Y")


                binding.apply {
                    tieCardNo.setText( cardModel?.maskedNumber?:"")
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


    private fun handlePaymentWithNewCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?){
        try {
            isAlready=false
            loader?.dismiss()
            when(status){
                is Resource.Success ->{
                    if(status.data?.statusCode?.equals("0")==true){
                        val bundle= Bundle()
                        bundle.putParcelable(Constants.DATA,status.data)
                        bundle.putString("amount",arguments?.getString("amount"))
                        findNavController().navigate(R.id.action_manualTopUpAddCardFragment_to_manualTopUpSuccessfulFragment,bundle)
                    }else {
                        showError(binding.root,status.data?.message)
                    }
                }
                is Resource.DataError ->{ showError(binding.root,status.errorMsg) }
            }
        }catch (e: Exception){}
    }


    private fun handleAccountDetailResponse(status: Resource<ProfileDetailModel?>?){
        try {
            if(!isAlready) return
            when(status){
                is  Resource.Success -> {
                    status.data?.run {
                        if(status.equals("500")){
                            loader?.dismiss()
                            showError(binding.root,message)
                        }
                        else {
                            var data=status.data.personalInformation
                            cardModel?.run {
                                city=data?.city
                                addressline1=data?.addressLine1
                                addressline2=data?.addressLine2
                                country=data?.country
                                state=data?.state?:""
                                zipcode1=data?.zipcode?:""
                                zipcode2=""
                            }

                            viewModel.paymentWithNewCard(cardModel)
                        }
                    }
                   }

                    is  Resource.DataError ->{
                        loader?.dismiss()

                        showError(binding.root,status.errorMsg) }
                }
            }
           catch (e: Exception){

           }
    }


}