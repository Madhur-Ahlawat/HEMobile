package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.graphics.Bitmap
import android.os.Bundle
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
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountResponseModel
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.account.payment.VehicleItem
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.databinding.NmiPaymentFragmentBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CreateAccountViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.topup.ManualTopUpViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NMIPaymentFragment : BaseFragment<NmiPaymentFragmentBinding>(), View.OnClickListener {


    private val viewModel: CreateAccountViewModel by viewModels()
    private val manualTopUpViewModel: ManualTopUpViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager


    private var loader: LoaderDialog? = null
    val vehicle: ArrayList<VehicleItem> = ArrayList()
    private var expMonth: String = ""
    private var expYear: String = ""
    private var thresholdAmount: String = ""
    private var topUpAmount = ""
    private var maskedCardNumber: String = ""
    private var cardToken: String = ""
    private var isTrusted: Boolean = false
    private var creditCardType: String = ""
    private var flow: String = ""
    private var responseModel: CardResponseModel? = null

    private var personalInformation: PersonalInformation? = null
    private var currentBalance: String = ""
    private var checkBox: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NmiPaymentFragmentBinding = NmiPaymentFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(JsObject(), "appInterface")



        flow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()

        topUpAmount = arguments?.getDouble(Constants.DATA).toString()
        thresholdAmount = arguments?.getDouble(Constants.THRESHOLD_AMOUNT).toString()

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""


        binding.webView.settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        binding.webView.loadUrl("file:///android_asset/NMIPayments.html")
    }

    override fun initCtrl() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        setupWebView()
    }

    override fun observer() {
        observe(viewModel.account, ::handleAccountResponse)

    }

    private fun handleAccountResponse(response: Resource<CreateAccountResponseModel?>?) {
        hideLoader()
        when (response) {
            is Resource.Success -> {
                val bundle = Bundle()
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_nmiPaymentFragment_to_accountCreatedSuccessfullyFragment,
                    bundle
                )


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {
            }

        }
    }

    override fun onClick(v: View?) {


    }

    inner class JsObject {
        @JavascriptInterface
        fun postMessage(data: String) {
            Log.i("WebView", "postMessage data=$data")
            if (data.isNotEmpty()) {
                MainScope().launch {
                    when (data) {
                        "NMILoaded" -> {
                            hideLoader()
                        }

                        "3DStarted" -> {
                           // showLoader()
                        }

                        "3DSLoaded" -> {
                            hideLoader()
                        }

                        "true" -> {
                            checkBox = true
                        }
                    }

                    val check: Boolean = "tokenType" in data
                    if (check) {
                        responseModel =
                            Gson().fromJson(data, CardResponseModel::class.java)
                        responseModel?.checkCheckBox = checkBox

                        expMonth = responseModel?.card?.exp?.subSequence(0, 2).toString()
                        expYear = "20" + responseModel?.card?.exp?.subSequence(2, 4).toString()
                        isTrusted = responseModel?.initiatedBy?.isTrusted == true
                        maskedCardNumber =
                            Utils.maskCardNumber(responseModel?.card?.number.toString())
                        cardToken = responseModel?.token.toString()
                        creditCardType = responseModel?.card?.type.toString()


                    }


                    if (data.contains("cardHolderAuth")) {
                        val gson = Gson()
                        val paymentSuccessResponse =
                            gson.fromJson(data, PaymentSuccessResponse::class.java)
                        if (paymentSuccessResponse.cardHolderAuth.equals("verified", true)) {
                            if (flow == Constants.NOTSUSPENDED) {
                                callAccountCreationApi(
                                    paymentSuccessResponse.threeDsVersion,
                                    paymentSuccessResponse.cavv,
                                    paymentSuccessResponse.directoryServerId,
                                    paymentSuccessResponse.eci
                                )
                            } else {
                                val bundle = Bundle()
                                bundle.putParcelable(Constants.DATA, responseModel)
                                bundle.putParcelable(Constants.NEW_CARD, paymentSuccessResponse)
                                bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount.toDouble())
                                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                                bundle.putString(Constants.NAV_FLOW_KEY, flow)
                                findNavController().navigate(
                                    R.id.action_nmiPaymentFragment_to_accountSuspendedFinalPayFragment,
                                    bundle
                                )


                            }

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

    private fun callAccountCreationApi(
        threeDsVersion: String?,
        cavv: String?,
        directoryServerId: String?,
        eci: String?
    ) {
        val data = NewCreateAccountRequestModel
        val model = AccountCreationRequest()
        model.stateType = "HE"
        model.cardStateType = "HE"
        model.tcAccepted = "Y"
        model.mailPreference = "Y"
        model.emailPreference = "Y"
        model.mfaFlag = "N"
        model.smsSecurityCd = data.smsSecurityCode      // sms security code
        model.cardMiddleName = ""
        model.cardZipCode = data.zipCode
        model.zipCode1 = data.zipCode
        if (NewCreateAccountRequestModel.country.equals("UK", true)) {
            model.countryType = "UK"

        } else {
            model.countryType = "NON-UK"

        }
        model.referenceId = data.referenceId
        model.eveningPhone = data.mobileNumber
        model.address1 = data.addressline1
        model.billingAddressLine1 = data.addressline1
        model.emailAddress = data.emailAddress
        model.creditCExpMonth = expMonth
        model.eveningPhoneCountryCode = data.countryCode?.let { getRequiredText(it) }
        model.creditCExpYear = expYear
        if (isTrusted) {
            model.cardholderAuth =
                "verified"// if istrusted is true then we need to send verified else empty
        } else {
            model.cardholderAuth = ""
        }

        model.transactionAmount = "10.00" // html Amount
        model.thresholdAmount = "10.00" // threshold Amount
        model.securityCode = ""
        model.smsReferenceId = ""
        model.securityCd = data.emailSecurityCode   // email security code
        model.cardFirstName = data.firstName   // model name
        model.cardCity = data.townCity   // address city
        model.city = data.townCity   // address city
        model.threeDsVer = threeDsVersion  // 3ds verison
        model.maskedNumber =
            maskedCardNumber// card masked number only we need to send last four digit
        model.creditCardNumber = cardToken// card number should be token number
        model.cavv = cavv // 3ds cavv
        model.correspDeliveryMode = ""/*"EMAIL"*/
        model.password = data.password  //model password
        model.firstName = data.firstName
        model.creditCardType = creditCardType.uppercase() // need to send upper case
        if (NewCreateAccountRequestModel.personalAccount) {
            model.accountType = "PRIVATE"    // private or business
        } else {
            model.accountType = "BUSINESS"
        }

        model.cardLastName = data.lastName  // model name
        model.lastName = data.lastName
        model.digitPin = "2465"
        model.correspDeliveryFrequency = "" /*"MONTHLY"*/
        model.eci = eci // 3ds eci
        model.replenishmentAmount = "10" // payment amount
        model.directoryServerID = directoryServerId // 3ds serverId
        val listVehicle: ArrayList<VehicleItem> = ArrayList()

        for (obj in data.vehicleList) {
            val item = VehicleItem()

            item.vehicleModel = obj.vehicleModel
            item.vehicleMake = obj.vehicleMake
            item.vehicleColor = obj.vehicleColor
            item.vehiclePlate = obj.plateNumber
            item.vehicleClassDesc = Utils.getVehicleTypeNumber(obj.vehicleClass.toString())
            item.plateTypeDesc = obj.vehicleClass
            item.plateCountry = obj.plateCountry
            item.vehicleYear = ""
            listVehicle.add(item)

        }
        model.ftvehicleList.vehicle = listVehicle

        viewModel.createAccountNew(model)


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
                val amount = arguments?.getDouble(Constants.DATA)
                hideLoader()
                view?.loadUrl("javascript:(function(){document.getElementById('amount').value = '$amount';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('currency').innerText = 'GBP';})()")

                if (flow == Constants.SUSPENDED||flow==Constants.PAYMENT_TOP_UP) {
                    view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'none';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('demoPayButton').innerText  ='CONTINUE';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${personalInformation?.emailAddress}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${personalInformation?.phoneNumber}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${personalInformation?.zipCode}';})()")


                }  else {
                    view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${NewCreateAccountRequestModel.emailAddress}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${NewCreateAccountRequestModel.mobileNumber}';})()")
                    view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${NewCreateAccountRequestModel.zipCode}';})()")


                }



                super.onPageFinished(view, url)
            }
        }
        binding.webView.webViewClient = webViewClient
    }

    private fun getRequiredText(text: String) = text.substringAfter(' ')


}