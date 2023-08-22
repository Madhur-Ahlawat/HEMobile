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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountResponseModel
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.account.payment.VehicleItem
import com.conduent.nationalhighways.data.model.makeoneofpayment.FtVehicleList
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.conduent.nationalhighways.data.model.makeoneofpayment.PaymentTypeInfo
import com.conduent.nationalhighways.data.model.payment.AddCardModel
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.databinding.NmiPaymentFragmentBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CreateAccountViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.payment.MakeOneOfPaymentViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
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
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class NMIPaymentFragment : BaseFragment<NmiPaymentFragmentBinding>(), View.OnClickListener {


    private val viewModel: CreateAccountViewModel by viewModels()
    private val paymentMethodViewModel: PaymentMethodViewModel by viewModels()
    private val oneOfPaymentViewModel: MakeOneOfPaymentViewModel by viewModels()


    @Inject
    lateinit var sessionManager: SessionManager


    val vehicle: ArrayList<VehicleItem> = ArrayList()
    private var expMonth: String = ""
    private var expYear: String = ""
    private var thresholdAmount: String = ""
    private var topUpAmount = ""
    private var htmlTopUpAmount = ""
    private var maskedCardNumber: String = ""
    private var cardToken: String = ""
    private var isTrusted: Boolean = false
    private var creditCardType: String = ""
    private var flow: String = ""
    private var responseModel: CardResponseModel? = null
    private var isViewCreated: Boolean = false

    private var personalInformation: PersonalInformation? = null
    private var currentBalance: String = ""
    private var checkBox: Boolean = false
    private var paymentListSize: Int = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NmiPaymentFragmentBinding = NmiPaymentFragmentBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""

        flow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()

        topUpAmount = arguments?.getDouble(Constants.DATA).toString()
        thresholdAmount = arguments?.getDouble(Constants.THRESHOLD_AMOUNT).toString()



        setupWebView()
    }

    override fun init() {
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(JsObject(), "appInterface")



        binding.webView.settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        binding.webView.loadUrl("file:///android_asset/NMIPayments.html")
    }


    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.account, ::handleAccountResponse)
            observe(paymentMethodViewModel.saveNewCard, ::handleSaveNewCardResponse)
            observe(oneOfPaymentViewModel.oneOfPaymentsPay, ::oneOfPaymentPay)

        }
        isViewCreated = true


    }

    private fun handleAccountResponse(response: Resource<CreateAccountResponseModel?>?) {
        hideLoader()
        when (response) {
            is Resource.Success -> {
                if (response.data?.statusCode.equals("0")) {
                    clearSingletonData()
                    val bundle = Bundle()
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    bundle.putParcelable(Constants.DATA, response.data)

                    findNavController().navigate(
                        R.id.action_nmiPaymentFragment_to_accountCreatedSuccessfullyFragment,
                        bundle
                    )
                }


            }

            is Resource.DataError -> {
                findNavController().navigate(R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment)
            }

            else -> {
                findNavController().navigate(R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment)

            }

        }
    }

    private fun clearSingletonData() {
        NewCreateAccountRequestModel.referenceId = ""
        NewCreateAccountRequestModel.mobileNumber = ""
        NewCreateAccountRequestModel.countryCode = ""
        NewCreateAccountRequestModel.communicationTextMessage = false
        NewCreateAccountRequestModel.termsCondition = false
        NewCreateAccountRequestModel.twoStepVerification = false
        NewCreateAccountRequestModel.personalAccount = false
        NewCreateAccountRequestModel.firstName = ""
        NewCreateAccountRequestModel.lastName = ""
        NewCreateAccountRequestModel.companyName = ""
        NewCreateAccountRequestModel.addressline1 = ""
        NewCreateAccountRequestModel.addressline2 = ""
        NewCreateAccountRequestModel.townCity = ""
        NewCreateAccountRequestModel.state = ""
        NewCreateAccountRequestModel.country = ""
        NewCreateAccountRequestModel.zipCode = ""
        NewCreateAccountRequestModel.prePay = false
        NewCreateAccountRequestModel.plateCountry = ""
        NewCreateAccountRequestModel.plateNumber = ""
        NewCreateAccountRequestModel.plateNumberIsNotInDVLA = false
        NewCreateAccountRequestModel.vehicleList.clear()
        NewCreateAccountRequestModel.addedVehicleList.clear()
        NewCreateAccountRequestModel.isRucEligible = false
        NewCreateAccountRequestModel.isExempted = false
        NewCreateAccountRequestModel.isVehicleAlreadyAdded = false
        NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal = false
        NewCreateAccountRequestModel.isMaxVehicleAdded = false
        NewCreateAccountRequestModel.isManualAddress = false
        NewCreateAccountRequestModel.emailSecurityCode = ""
        NewCreateAccountRequestModel.smsSecurityCode = ""
        NewCreateAccountRequestModel.password = ""
    }

    override fun onClick(v: View?) {


    }

    private fun oneOfPaymentPay(resource: Resource<OneOfPaymentModelResponse?>?) {
        hideLoader()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    it.let {
                        val mBundle = Bundle()
                        mBundle.putParcelable(Constants.ONE_OF_PAYMENTS_PAY_RESP, it)
                        mBundle.putString(Constants.DATA,htmlTopUpAmount)

                        AdobeAnalytics.setActionTrackPaymentMethodOrderId(
                            "Confirm ",
                            " one of payment: payment confirm",
                            "payment ",
                            "english",
                            " one of payment",
                            "home",
                            "success",
                            "card",
                            it.refrenceNumber!!,
                            "1",
                            sessionManager.getLoggedInUser()
                        )

                        findNavController().navigate(
                            R.id.action_nmiPaymentFragment_to_make_one_off_payment_successfully,
                            mBundle
                        )

                    }
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)

                AdobeAnalytics.setActionTrackPaymentMethod(
                    "Confirm ",
                    " one of payment: payment confirm",
                    "payment ",
                    "english",
                    " one of payment",
                    "home",
                    resource.errorMsg, "card", sessionManager.getLoggedInUser()
                )

            }

            else -> {
            }
        }

    }


    inner class JsObject {
        @JavascriptInterface
        fun postMessage(data: String) {
            Log.i("WebView", "postMessage data=$data")
            if (data.isNotEmpty()) {
                MainScope().launch {
                    when (data) {
                        "NMILoaded", "ValidationFailed", "3DSLoaded", "timedOUt", "cancelClicked" -> hideLoader()

                        "3DStarted" -> showLoader()
                        "3DSNotIntiated" -> showErrorPopup(resources.getString(R.string.payment_failed))
                        "cardtypeerror" -> showErrorPopup(resources.getString(R.string.payment_incorrect))

                        else -> {
                            if (data.startsWith("amounttoIncrease")) {
                                htmlTopUpAmount = data.replace("amounttoIncrease", "")
                            } else if (data == "true") {
                                checkBox = true
                            }
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
                            } else if (flow == Constants.ADD_PAYMENT_METHOD) {
                                if (responseModel?.checkCheckBox == true) {
                                    saveNewCard(responseModel, paymentSuccessResponse, "Y")

                                } else {
                                    saveNewCard(responseModel, paymentSuccessResponse, "N")

                                }

                            } else if (flow == Constants.PAY_FOR_CROSSINGS) {

                                makeOneOffPaymentApi(responseModel, paymentSuccessResponse)
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

    private fun showErrorPopup(errorMsg:String) {
        ErrorUtil.showError(binding.root, errorMsg)

    }

    private fun makeOneOffPaymentApi(
        responseModel: CardResponseModel?,
        paymentSuccessResponse: PaymentSuccessResponse
    ) {
        val paymentTypeInfo = PaymentTypeInfo(
            responseModel?.card?.type?.uppercase(Locale.ROOT),
            responseModel?.card?.number,
            responseModel?.token,
            responseModel?.card?.exp?.subSequence(0, 2).toString(),
            "20${responseModel?.card?.exp?.subSequence(2, 4)}",
            htmlTopUpAmount,
            responseModel?.check?.name,
            "",
            NewCreateAccountRequestModel.emailAddress,
            NewCreateAccountRequestModel.mobileNumber, "", "", "", "", "", "", ""
        )

        val ftVehicleList =
            FtVehicleList(NewCreateAccountRequestModel.vehicleList as ArrayList<NewVehicleInfoDetails>)
        val oneOfPayModelReq = OneOfPaymentModelRequest(ftVehicleList, paymentTypeInfo)
        oneOfPaymentViewModel.oneOfPaymentsPay(oneOfPayModelReq)
    }


    private fun saveNewCard(
        responseModel: CardResponseModel?,
        paymentSuccessResponse: PaymentSuccessResponse?,
        s: String
    ) {
        showLoader()
        val addCardModel = AddCardModel(
            addressLine1 = personalInformation?.addressLine1.toString(),
            addressLine2 = personalInformation?.addressLine2.toString(),
            bankRoutingNumber = "",
            cardNumber = responseModel?.token,
            cardType = responseModel?.card?.type?.uppercase(Locale.ROOT),
            city = personalInformation?.city,
            country = "UK",
            cvv = "",
            easyPay = "N",
            expMonth = responseModel?.card?.exp?.substring(0, 2),
            expYear = "20${responseModel?.card?.exp?.substring(2, 4)}",
            firstName = responseModel?.check?.name ?: "",
            middleName = "",
            lastName = "",
            maskedCardNumber = Utils.maskCardNumber(responseModel?.card?.number.toString()),
            paymentType = "card",
            primaryCard = s,
            saveCard = "",
            state = "HE",
            useAddressCheck = "N",
            zipcode1 = personalInformation?.zipcode.toString().replace(" ", ""),
            zipcode2 = "",
            directoryServerId = paymentSuccessResponse?.directoryServerId.toString(),
            cavv = paymentSuccessResponse?.cavv.toString(),
            threeDsVersion = paymentSuccessResponse?.threeDsVersion.toString(),
            cardHolderAuth = paymentSuccessResponse?.cardHolderAuth.toString(),
            eci = paymentSuccessResponse?.eci.toString(),
            customerVaultId = null
        )

        paymentMethodViewModel.saveNewCard(addCardModel)


    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE


    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE

    }

    private fun callAccountCreationApi(
        threeDsVersion: String?,
        cavv: String?,
        directoryServerId: String?,
        eci: String?
    ) {
        showLoader()
        val data = NewCreateAccountRequestModel
        val model = AccountCreationRequest()
        model.stateType = "HE"
        model.cardStateType = "HE"
        model.tcAccepted = "Y"
        model.mailPreference = "N"
        model.emailPreference = "Y"
        if (NewCreateAccountRequestModel.twoStepVerification){
            model.mfaFlag = "Y"

        }else{
            model.mfaFlag = "N"

        }
        model.smsSecurityCd = data.smsSecurityCode      // sms security code
        model.cardMiddleName = ""
        model.cardZipCode = data.zipCode
        model.zipCode1 = data.zipCode
        if (!NewCreateAccountRequestModel.prePay) {
            model.planType = "PAYG"

        }
        if (NewCreateAccountRequestModel.country.equals(
                "UK",
                true
            ) || NewCreateAccountRequestModel.country.equals("United Kingdom", true)
        ) {
            model.countryType = "UK"

        } else {
            model.countryType = "UK"

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

        model.transactionAmount = String.format("%.2f", htmlTopUpAmount.toDouble()) // html Amount
        model.thresholdAmount =
            String.format("%.2f", thresholdAmount.toDouble())  // threshold Amount
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
        model.correspDeliveryMode = "EMAIL"
        model.password = data.password  //model password
        model.firstName = data.firstName
        model.creditCardType = creditCardType.uppercase() // need to send upper case
        if (NewCreateAccountRequestModel.personalAccount) {
            model.accountType = "PRIVATE"    // private or business
        } else {
            model.accountType = "BUSINESS"
            model.companyName = NewCreateAccountRequestModel.companyName
        }

        model.cardLastName = data.lastName  // model name
        model.lastName = data.lastName
        model.digitPin = "2465"
        //model.correspDeliveryFrequency = "MONTHLY"
        model.eci = eci // 3ds eci
        model.replenishmentAmount = String.format("%.2f", topUpAmount.toDouble()) // top up amount
        model.directoryServerId = directoryServerId // 3ds serverId
        if (NewCreateAccountRequestModel.communicationTextMessage){
            model.smsOption = "Y"

        }else{
            model.smsOption = "N"

        }
        val listVehicle: ArrayList<VehicleItem> = ArrayList()

        for (obj in data.vehicleList) {
            val item = VehicleItem()

            item.vehicleModel = obj.vehicleModel
            item.vehicleMake = obj.vehicleMake
            item.vehicleColor = obj.vehicleColor
            item.vehiclePlate = obj.plateNumber
            item.vehicleClassDesc = Utils.getVehicleTypeNumber(obj.vehicleClass.toString())
            item.plateTypeDesc = "STANDARD"
            item.plateCountry = "UK"
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
                val amount: Double = if (!NewCreateAccountRequestModel.prePay) {
                    0.00

                } else {
                    arguments?.getDouble(Constants.DATA) ?: 0.00

                }
                val doubleAmount = String.format("%.2f", amount)
                hideLoader()
                view?.loadUrl("javascript:(function(){document.getElementById('amount').value = '$doubleAmount';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('currency').innerText = 'GBP';})()")
//
                when (flow) {
                    Constants.SUSPENDED -> {
                        view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('demoPayButton').innerText  ='CONTINUE';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${personalInformation?.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${personalInformation?.phoneNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${personalInformation?.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${personalInformation?.city}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${personalInformation?.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${personalInformation?.addressLine1}';})()")


                    }

                    Constants.ADD_PAYMENT_METHOD, Constants.PAYMENT_TOP_UP -> {
                        view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")
                        if (paymentListSize == 0 || paymentListSize == 2) {
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = 'none';})()")
                        }
                        view?.loadUrl("javascript:(function(){document.getElementById('demoPayButton').innerText  ='CONTINUE';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${personalInformation?.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${personalInformation?.phoneNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${personalInformation?.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${personalInformation?.city}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${personalInformation?.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${personalInformation?.addressLine1}';})()")

                    }

                    else -> {
                        if (!NewCreateAccountRequestModel.prePay) {
                            view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")

                        }
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${NewCreateAccountRequestModel.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${NewCreateAccountRequestModel.mobileNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${NewCreateAccountRequestModel.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${NewCreateAccountRequestModel.townCity}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${NewCreateAccountRequestModel.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${NewCreateAccountRequestModel.addressline1}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = 'none';})()")


                    }
                }



                super.onPageFinished(view, url)
            }
        }
        binding.webView.webViewClient = webViewClient
    }

    private fun getRequiredText(text: String) = text.substringAfter('(').replace(")","")

    private fun handleSaveNewCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        hideLoader()
        when (status) {
            is Resource.Success -> {

                if (status.data?.statusCode?.equals("0") == true) {
                    val bundle = Bundle()

                    bundle.putParcelable(Constants.DATA, responseModel)

                    findNavController().navigate(
                        R.id.action_nmiPaymentFragment_to_paymentSuccessFragment2,
                        bundle
                    )
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)

                } else if (status.data?.statusCode?.equals("1337") == true) {
                    val bundle = Bundle()

                    bundle.putString(
                        Constants.CARD_IS_ALREADY_REGISTERED,
                        Constants.CARD_IS_ALREADY_REGISTERED
                    )
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)

                    findNavController().navigate(
                        R.id.action_nmiPaymentFragment_to_paymentSuccessFragment2,
                        bundle
                    )
                } else {


                    ErrorUtil.showError(binding.root, status.data?.message)
                }
            }

            is Resource.DataError -> {
                findNavController().navigate(R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment)
            }

            else -> {
                findNavController().navigate(R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment)

            }
        }
    }

}