package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.content.DialogInterface
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
import com.conduent.nationalhighways.data.model.account.CreateProfileDetailModelModel
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.account.payment.VehicleItem
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.makeoneofpayment.FtVehicleList
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.conduent.nationalhighways.data.model.makeoneofpayment.PaymentTypeInfo
import com.conduent.nationalhighways.data.model.makeoneofpayment.VehicleList
import com.conduent.nationalhighways.data.model.payment.AddCardModel
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.data.model.payment.NmiErrorModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.NmiPaymentFragmentBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CreateAccountViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.payment.MakeOneOfPaymentViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.setPersonalInfoAnnouncement
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class NMIPaymentFragment : BaseFragment<NmiPaymentFragmentBinding>(), View.OnClickListener {


    private var isDrectDebit: Boolean = false
    private val viewModel: CreateAccountViewModel by viewModels()
    private val paymentMethodViewModel: PaymentMethodViewModel by viewModels()
    private val oneOfPaymentViewModel: MakeOneOfPaymentViewModel by viewModels()
    private var crossingDetailModelResponse: CrossingDetailsModelsResponse? = null


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
    private var responseModel: CardResponseModel? = null
    private var isViewCreated: Boolean = false

    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var currentBalance: String = ""
    private var checkBox: Boolean = false
    private var paymentListSize: Int = 0
    private var threeDStarted: Boolean = false
    private var cardValidationFirstTime: Boolean = false
    private var cardValidationSecondTime: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NmiPaymentFragmentBinding = NmiPaymentFragmentBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        if (arguments?.containsKey(Constants.PAYMENT_METHOD_SIZE) == true) {
            paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0
        }

        if (arguments?.containsKey(Constants.IS_DIRECT_DEBIT) == true) {
            isDrectDebit = arguments?.getBoolean(Constants.IS_DIRECT_DEBIT, false) ?: false
        }

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }

        if (arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY) != null) {
            crossingDetailModelResponse = arguments?.getParcelable(Constants.NAV_DATA_KEY)
        }

        if (arguments?.containsKey(Constants.CARD_VALIDATION_FIRST_TIME) == true) {
            cardValidationFirstTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_FIRST_TIME) ?: false
        }

        if (arguments?.containsKey(Constants.CARD_VALIDATION_SECOND_TIME) == true) {
            cardValidationSecondTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_SECOND_TIME) ?: false
        }

       if (arguments?.containsKey(Constants.CURRENTBALANCE) == true) {
           currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""
       }



        topUpAmount = arguments?.getDouble(Constants.DATA).toString()
        thresholdAmount = arguments?.getDouble(Constants.THRESHOLD_AMOUNT).toString()


        setPersonalInfoAnnouncement(binding.rootLayout, requireActivity())

        setupWebView()
        if (navFlowCall == Constants.PAYMENT_TOP_UP && paymentListSize == 0) {
            if (requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain).setTitle("Top Up New Payment Method")
            }
        }
    }

    override fun init() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.addJavascriptInterface(JsObject(), "appInterface")

        binding.webView.setOnLongClickListener { true }
        binding.webView.isLongClickable = false



        binding.webView.settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        binding.webView.settings.defaultTextEncodingName = "utf-8"

        binding.webView.loadUrl("file:///android_asset/NMIPayments.html")
    }


    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.account, ::handleProfileDetailModel)
            observe(paymentMethodViewModel.saveNewCard, ::handleSaveNewCardResponse)
            observe(oneOfPaymentViewModel.oneOfPaymentsPay, ::oneOfPaymentPay)
        }
        isViewCreated = true


    }

    private fun handleProfileDetailModel(response: Resource<CreateProfileDetailModelModel?>?) {
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
                } else {
                    redirectToTryAgainPaymentScreen()
                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(response.errorModel)) {
                    displaySessionExpireDialog(response.errorModel)
                } else {
                    if (response.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
                        redirectToTryAgainPaymentScreen()
                    }
                }
            }

            else -> {

            }

        }
    }

    private fun clearSingletonData() {
        NewCreateAccountRequestModel.referenceId = ""
        NewCreateAccountRequestModel.mobileNumber = ""
        NewCreateAccountRequestModel.telephoneNumber = ""
        NewCreateAccountRequestModel.telephone_countryCode = ""
        NewCreateAccountRequestModel.countryCode = ""
        NewCreateAccountRequestModel.communicationTextMessage = false
        NewCreateAccountRequestModel.termsCondition = false
        NewCreateAccountRequestModel.twoStepVerification = false
        NewCreateAccountRequestModel.personalAccount = false
        NewCreateAccountRequestModel.firstName = ""
        NewCreateAccountRequestModel.lastName = ""
        NewCreateAccountRequestModel.companyName = ""
        NewCreateAccountRequestModel.addressLine1 = ""
        NewCreateAccountRequestModel.addressLine2 = ""
        NewCreateAccountRequestModel.townCity = ""
        NewCreateAccountRequestModel.state = ""
        NewCreateAccountRequestModel.country = ""
        NewCreateAccountRequestModel.zipCode = ""
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
        NewCreateAccountRequestModel.isCountryNotSupportForSms = false
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
                        mBundle.putString(Constants.DATA, htmlTopUpAmount)
                        mBundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)

                        AdobeAnalytics.setActionTrackPaymentMethodOrderId(
                            "Confirm ",
                            " one of payment: payment confirm",
                            "payment ",
                            "english",
                            " one of payment",
                            "home",
                            "success",
                            "card",
                            it.referenceNumber ?: "",
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
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    if (resource.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
                        redirectToTryAgainPaymentScreen()
                    }
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
                        "NMILoaded", "ValidationFailed", "3DSLoaded", "timedOUt" -> hideLoader()
                        "3DStarted" -> showLoader()
                        "3DSNotIntiated" -> redirectToTryAgainPaymentScreen()


                        "cardtypeerror" -> redirectToTryAgainPaymentScreen()


                        "3DStarted1" -> threeDStarted = true
                        "cancelClicked" -> {
                            hideLoader()
                            findNavController().popBackStack()
                        }

                        "termsClick"->{
                            if (accountInformation?.accountType.equals(
                                    "BUSINESS",
                                    true
                                ) || ((accountInformation?.accSubType.equals(
                                    "STANDARD", true
                                ) && accountInformation?.accountType.equals(
                                    "PRIVATE", true
                                )))
                            ) {
                                findNavController().navigate(R.id.action_nmiPaymentFragment_to_generalTermsAndConditions)
                            } else {
                                findNavController().navigate(R.id.action_nmiPaymentFragment_to_paygtermsandconditions)
                            }

                        }


                        else -> {

                            if (data.startsWith("errorMessage")) {

                                val nmiErrorModel =
                                    Gson().fromJson(
                                        data.replace("errorMessage", ""),
                                        NmiErrorModel::class.java
                                    )

                                if (nmiErrorModel.type != "integrationError") {
                                    redirectToTryAgainPaymentScreen()
                                }
                            }


                            if (data == "paymentFailed" && threeDStarted) {
                                hideLoader()
                                redirectToTryAgainPaymentScreen()

                            }

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
                            Utils.maskSixteenDigitCardNumber(responseModel?.card?.number.toString())
                        cardToken = responseModel?.token.toString()
                        creditCardType = responseModel?.card?.type.toString()


                    }


                    if (data.contains("cardHolderAuth")) {
                        val gson = Gson()
                        val paymentSuccessResponse =
                            gson.fromJson(data, PaymentSuccessResponse::class.java)
                        if (paymentSuccessResponse.cardHolderAuth.equals("verified", true)) {
                            if (navFlowCall == Constants.NOTSUSPENDED) {
                                callAccountCreationApi(
                                    paymentSuccessResponse.threeDsVersion,
                                    paymentSuccessResponse.cavv,
                                    paymentSuccessResponse.directoryServerId,
                                    paymentSuccessResponse.eci
                                )
                            } else if (navFlowCall == Constants.CARD_VALIDATION_REQUIRED) {
                                saveNewCard(responseModel, paymentSuccessResponse, "Y")
                            } else if (navFlowCall == Constants.ADD_PAYMENT_METHOD) {
                                if (responseModel?.checkCheckBox == true || paymentListSize == 0) {
                                    saveNewCard(responseModel, paymentSuccessResponse, "Y")
                                } else {
                                    saveNewCard(responseModel, paymentSuccessResponse, "N")
                                }

                            } else if (navFlowCall == Constants.PAY_FOR_CROSSINGS) {

                                makeOneOffPaymentApi(responseModel, paymentSuccessResponse)
                            } else {
                                val bundle = Bundle()
                                bundle.putParcelable(Constants.DATA, responseModel)
                                bundle.putParcelable(Constants.NEW_CARD, paymentSuccessResponse)
                                bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount.toDouble())
                                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                                bundle.putParcelable(
                                    Constants.ACCOUNTINFORMATION,
                                    accountInformation
                                )
                                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
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


    private fun makeOneOffPaymentApi(
        responseModel: CardResponseModel?,
        paymentSuccessResponse: PaymentSuccessResponse
    ) {
        showLoader()
        val paymentTypeInfo = PaymentTypeInfo(
            responseModel?.card?.type?.uppercase(Locale.ROOT),
            Utils.maskSixteenDigitCardNumber(responseModel?.card?.number ?: ""),
            responseModel?.token,
            responseModel?.card?.exp?.subSequence(0, 2).toString(),
            "20${responseModel?.card?.exp?.subSequence(2, 4)}",
            htmlTopUpAmount,
            responseModel?.check?.name,
            "",
            NewCreateAccountRequestModel.emailAddress,
            NewCreateAccountRequestModel.mobileNumber,
            "", "", "", "", "", "", "",
            cardholderAuth = paymentSuccessResponse.cardHolderAuth,
            cavv = paymentSuccessResponse.cavv,
            directoryServerID = paymentSuccessResponse.directoryServerId,
            eci = paymentSuccessResponse.eci,
            threeDsVer = paymentSuccessResponse.threeDsVersion

        )
        val pendingDues = (crossingDetailModelResponse?.unsettledTripChange?.toDouble())?.times(
            (crossingDetailModelResponse?.chargingRate?.toDouble() ?: 0.00)
        )
        val futureTollPayment =
            (crossingDetailModelResponse?.additionalCrossingCount?.toDouble())?.times(
                (crossingDetailModelResponse?.chargingRate?.toDouble() ?: 0.00)
            )

        val vehicleList = VehicleList(
            crossingDetailModelResponse?.plateNo?.uppercase(),
            crossingDetailModelResponse?.vehicleMake,
            crossingDetailModelResponse?.vehicleModel,
            crossingDetailModelResponse?.dvlaclass,
            crossingDetailModelResponse?.plateCountry,
            pendingDues.toString(),
            crossingDetailModelResponse?.additionalCrossingCount.toString(),
            futureTollPayment.toString(),
            crossingDetailModelResponse?.vehicleColor,
            crossingDetailModelResponse?.chargingRate,
            crossingDetailModelResponse?.customerClass,
            crossingDetailModelResponse?.customerClassRate,
            crossingDetailModelResponse?.accountNo,
            crossingDetailModelResponse?.unsettledTripChange.toString(),
            crossingDetailModelResponse?.chargingRate
        )
        val mVehicleList = ArrayList<VehicleList>()
        mVehicleList.clear()
        mVehicleList.add(vehicleList)
        val ftVehicleList = FtVehicleList(mVehicleList)
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
            easyPay = "Y",
            expMonth = responseModel?.card?.exp?.substring(0, 2),
            expYear = "20${responseModel?.card?.exp?.substring(2, 4)}",
            firstName = responseModel?.check?.name ?: "",
            middleName = "",
            lastName = "",
            maskedCardNumber = Utils.maskSixteenDigitCardNumber(responseModel?.card?.number.toString()),
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
        try {
            binding.progressBar.visibility = View.VISIBLE
        }catch (e:Exception){

        }


    }

    private fun hideLoader() {
        try {
            binding.progressBar.visibility = View.GONE
        } catch (_: Exception) {
        }
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
        model.mailPreference = "Y"
        model.emailPreference = "Y"
        model.postCode = NewCreateAccountRequestModel.zipCode.replace(" ", "")
        model.addressLine2 = "Small Heath"


        model.smsSecurityCd = data.smsSecurityCode      // sms security code
        model.cardMiddleName = ""
        model.cardZipCode = data.zipCode.replace(" ", "")
        if (!NewCreateAccountRequestModel.prePay) {
            model.planType = "PAYG"

        }

        model.referenceId = data.referenceId
        if (NewCreateAccountRequestModel.communicationTextMessage || NewCreateAccountRequestModel.twoStepVerification) {
            model.cellPhone = data.mobileNumber
            model.cellPhoneCountryCode = data.countryCode?.let { getRequiredText(it) }
            model.smsReferenceId = data.sms_referenceId

            NewCreateAccountRequestModel.isCountryNotSupportForSms =
                Utils.isSupportedCountry(model.cellPhoneCountryCode.toString())

            if (!NewCreateAccountRequestModel.prePay) {
                if (!NewCreateAccountRequestModel.notSupportedCountrySaveDetails) {
                    model.cellPhone = ""
                    model.cellPhoneCountryCode = ""
                    model.smsReferenceId = ""

                }
            }
        } else {
            model.eveningPhone = data.telephoneNumber
            model.eveningPhoneCountryCode = data.telephone_countryCode?.let { getRequiredText(it) }
            model.smsReferenceId = ""
        }
        if (NewCreateAccountRequestModel.prePay) {
            if (data.address_country_code.isEmpty()) {
                model.countryType = "UK"
            } else {
                model.countryType = data.address_country_code
            }
            model.address1 = data.addressLine1
            model.address2 = data.addressLine2
            model.zipCode1 = data.zipCode.replace(" ", "")
            model.city = data.townCity   // address city
        } else {
            model.countryType = ""
            model.address1 = ""
            model.address2 = ""
            model.zipCode1 = ""
            model.city = ""
        }
        model.addressLine2 = data.addressLine2
        model.billingAddressLine1 = data.addressLine1
        model.billingAddressLine2 = data.addressLine2
        model.emailAddress = data.emailAddress
        model.creditCExpMonth = expMonth
        model.creditCExpYear = expYear
        if (isTrusted) {
            model.cardholderAuth =
                "verified"// if istrusted is true then we need to send verified else empty
        } else {
            model.cardholderAuth = ""
        }

        if (NewCreateAccountRequestModel.prePay) {
            if (htmlTopUpAmount.trim().isNotEmpty()) {
                model.transactionAmount =
                    String.format("%.2f", htmlTopUpAmount.toDouble()) // html Amount

            }
        }

        model.thresholdAmount =
            String.format("%.2f", thresholdAmount.toDouble())  // threshold Amount
        model.securityCode = ""
        model.securityCd = data.emailSecurityCode   // email security code
        model.cardFirstName = data.firstName   // model name
        model.cardCity = data.townCity   // address city
        model.threeDsVer = threeDsVersion  // 3ds verison
        model.maskedNumber =
            maskedCardNumber// card masked number only we need to send last four digit
        model.creditCardNumber = cardToken// card number should be token number
        model.cavv = cavv // 3ds cavv
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
        model.correspDeliveryFrequency = ""
        model.correspDeliveryMode = ""
        model.eci = eci // 3ds eci
        model.replenishmentAmount = String.format("%.2f", topUpAmount.toDouble()) // top up amount
        model.directoryServerID = directoryServerId // 3ds serverId
        if (NewCreateAccountRequestModel.communicationTextMessage || NewCreateAccountRequestModel.twoStepVerification) {
            if (!NewCreateAccountRequestModel.isCountryNotSupportForSms) {
                model.smsOption = "N"
                model.mfaFlag = "N"
            } else {
                model.smsOption = "Y"
                model.mfaFlag = "Y"
            }
        } else {
            model.smsOption = "N"
            model.mfaFlag = "N"
        }

        val listVehicle: ArrayList<VehicleItem> = ArrayList()

        for (obj in data.vehicleList) {
            val item = VehicleItem()
            item.vehicleModel = obj.vehicleModel
            item.vehicleMake = obj.vehicleMake
            item.vehicleColor = obj.vehicleColor
            item.vehiclePlate = obj.plateNumber
            item.vehicleClassDesc = Utils.getVehicleTypeNumber(obj.vehicleClass.toString())
            // item.plateTypeDesc = "STANDARD"
            if (obj.isUK == true) {
                item.plateCountry = "UK"
            } else {
                item.plateCountry = "NON-UK"
            }
            item.vehicleYear = Utils.getCurrentYear().toString()
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
                val amount: Double = arguments?.getDouble(Constants.DATA) ?: 0.00


                val doubleAmount = String.format("%.2f", amount)
                hideLoader()
                view?.loadUrl("javascript:(function(){document.getElementById('amount').value = '$doubleAmount';})()")
                view?.loadUrl("javascript:(function(){document.getElementById('currency').innerText = 'GBP';})()")


                when (navFlowCall) {
                    Constants.SUSPENDED -> {
                        view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('demoPayButton').innerText  ='CONTINUE';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${personalInformation?.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${personalInformation?.phoneNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${personalInformation?.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${personalInformation?.city}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${personalInformation?.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${personalInformation?.addressLine1}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('checkboxHint').innerText  ='Save the payment method against the account.';})()")

                        if (paymentListSize != 0 && paymentListSize != 1) {
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = 'none';})()")
                        } else {
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = '';})()")
                        }

                    }

                    Constants.CARD_VALIDATION_REQUIRED -> {
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'block'; document.getElementById('title').innerText = 'How do you want to pay?';})()")

                        view?.loadUrl("javascript:(function(){document.getElementById('headerTable').style.display ='';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').innerText  ='Enter new card details:';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('subtitle').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('terms').style.display ='';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('termsText').style.display ='';})()")

                        view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').checked = true; document.getElementById('cardChecked').style.pointerEvents = 'none';})()")

                        view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('demoPayButton').innerText  ='PROCEED WITH PAYMENT';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${personalInformation?.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${personalInformation?.phoneNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${personalInformation?.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${personalInformation?.city}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${personalInformation?.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${personalInformation?.addressLine1}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('checkboxHint').innerText  ='Make this my default payment method.';})()")

                    }

                    Constants.ADD_PAYMENT_METHOD -> {
                        view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")
                        if (paymentListSize == 1) {
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = '';})()")
                        } else {
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
                        if (isDrectDebit && paymentListSize == 1) {
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = 'none';})()")
                        }
                    }

                    Constants.PAYMENT_TOP_UP -> {
                        view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('checkboxHint').innerText  ='Save the payment method against the account.';})()")

                        if (paymentListSize >= 2) {
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = 'none';})()")
                        } else if (paymentListSize == 0) {
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('hint1').innerHTML = 'Save the payment method against the account';})()")
                        } else {
                            view?.loadUrl("javascript:(function(){document.getElementById('checkboxHint').innerHTML = 'Save the payment method against the account';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('checkboxHint').value = 'Make default payment method';})()")
                        }
                        view?.loadUrl("javascript:(function(){document.getElementById('demoPayButton').innerText  ='CONTINUE';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${personalInformation?.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${personalInformation?.phoneNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${personalInformation?.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${personalInformation?.city}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${personalInformation?.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${personalInformation?.addressLine1}';})()")

                    }

                    Constants.PAY_FOR_CROSSINGS -> {
                        val amountData = getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            topUpAmount.toDouble()
                        )

                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'block'; document.getElementById('title').innerText = 'Payment Details';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${NewCreateAccountRequestModel.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${NewCreateAccountRequestModel.mobileNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${NewCreateAccountRequestModel.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${NewCreateAccountRequestModel.townCity}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${NewCreateAccountRequestModel.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${NewCreateAccountRequestModel.addressLine1}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('amountLabel').innerText = '${amountData}';})()")


                        view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = 'none';})()")

                        view?.loadUrl("javascript:(function(){document.getElementById('breakPoint').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('headerTable').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('amountLabel').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').innerText  ='Payment Details';})()")

                        view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = '';})()")

                    }

                    else -> {
                        view?.loadUrl("javascript:(function(){document.getElementById('nameerrormesages').style.display = '';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('title').style.display = 'block'; document.getElementById('title').innerText = 'How do you want to pay?';})()")

                        if (!NewCreateAccountRequestModel.prePay) {
                            view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('amountLabel').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('breakPoint').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('headerTable').style.display ='';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display ='none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('subtitle').style.display = '';})()")



                            view?.loadUrl("javascript:(function(){document.getElementById('subtitle').innerText  ='You chose to pay as you go. We’ll collect payment from your card each time you cross.';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('title').innerText  ='How do you want to pay?';})()")
                        } else {
                            view?.loadUrl("javascript:(function(){document.getElementById('breakPoint').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('amount').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('paymentAmountTitle').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('amountLabel').style.display = 'none';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('amounInput').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('currency1').style.display = '';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('payment').style.display ='';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('headerTable').style.display ='';})()")
                            view?.loadUrl("javascript:(function(){document.getElementById('title').style.display ='';})()")
                        }
                        view?.loadUrl("javascript:(function(){document.getElementById('email').value = '${NewCreateAccountRequestModel.emailAddress}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('phone').value = '${NewCreateAccountRequestModel.mobileNumber}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('postalCode').value = '${NewCreateAccountRequestModel.zipCode}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('city').value = '${NewCreateAccountRequestModel.townCity}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('country').value = '${NewCreateAccountRequestModel.country}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('address1').value = '${NewCreateAccountRequestModel.addressLine1}';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('cardChecked').style.display = 'none';})()")
                        view?.loadUrl("javascript:(function(){document.getElementById('checkBoxhide').style.display = 'none';})()")


                    }
                }



                super.onPageFinished(view, url)
            }
        }
        binding.webView.webViewClient = webViewClient
    }

    private fun getRequiredText(text: String) = text.substringAfter('(').replace(")", "")

    private fun handleSaveNewCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        hideLoader()
        when (status) {
            is Resource.Success -> {

                if (status.data?.statusCode?.equals("0") == true) {
                    val bundle = Bundle()

                    bundle.putParcelable(Constants.DATA, responseModel)
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

                    if (navFlowFrom == Constants.PAYG_SUSPENDED) {
                        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                        bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                        findNavController().navigate(
                            R.id.action_nmiPaymentFragment_to_accountSuspendReOpenFragment,
                            bundle
                        )
                    } else if (navFlowFrom == Constants.CARD_VALIDATION_REQUIRED) {
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                        findNavController().navigate(
                            R.id.action_nmiPaymentFragment_to_reValidateInfoFragment,
                            bundle
                        )
                    } else {
                        findNavController().navigate(
                            R.id.action_nmiPaymentFragment_to_paymentSuccessFragment2,
                            bundle
                        )
                    }


                } else if (status.data?.statusCode?.equals("1337") == true) {
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.CARD_IS_ALREADY_REGISTERED,
                        Constants.CARD_IS_ALREADY_REGISTERED
                    )
                    displayCustomMessage(
                        getString(R.string.str_warning),
                        getString(R.string.the_card_you_are_trying_to_add_is_already),
                        getString(R.string.str_add_another_card_small), getString(R.string.cancel),
                        object : DialogPositiveBtnListener {
                            override fun positiveBtnClick(dialog: DialogInterface) {
                                val fragmentId = findNavController().currentDestination?.id
                                findNavController().popBackStack(fragmentId ?: 0, true)
                                findNavController().navigate(fragmentId ?: 0, arguments)
                            }
                        },
                        object : DialogNegativeBtnListener {
                            override fun negativeBtnClick(dialog: DialogInterface) {
                                findNavController().navigate(R.id.action_nmiPaymentFragment_to_paymentMethodFragment)
                            }
                        })
                } else if (status.data?.statusCode?.equals("1333") == true) {
                    redirectToTryAgainPaymentScreen()
                } else {
                    redirectToTryAgainPaymentScreen()
                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    if (status.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.NAV_FLOW_FROM,
                            navFlowFrom
                        )
                        if (navFlowFrom == Constants.CARD_VALIDATION_REQUIRED) {
                            bundle.putBoolean(
                                Constants.CARD_VALIDATION_FIRST_TIME,
                                cardValidationFirstTime
                            )
                            bundle.putBoolean(
                                Constants.CARD_VALIDATION_SECOND_TIME,
                                cardValidationSecondTime
                            )
                            bundle.putBoolean(Constants.CARD_VALIDATION_PAYMENT_FAIL, true)
                            bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                            findNavController().navigate(
                                R.id.action_nmiPaymentFragment_to_reValidateInfoFragment,
                                bundle
                            )
                        } else {
                            bundle.putString(
                                Constants.CARD_IS_ALREADY_REGISTERED,
                                Constants.CREDIT_NOT_SET_UP
                            )
                            findNavController().navigate(
                                R.id.action_nmiPaymentFragment_to_paymentSuccessFragment2,
                                bundle
                            )
                        }


                    }

                }
            }

            else -> {

            }
        }
    }

    private fun redirectToTryAgainPaymentScreen() {

        if (navFlowFrom == Constants.PAYG_SUSPENDED) {
            val bundle = Bundle()

            bundle.putString(
                Constants.NAV_FLOW_FROM,
                navFlowFrom
            )
            bundle.putString(
                Constants.CARD_IS_ALREADY_REGISTERED,
                Constants.CREDIT_NOT_SET_UP
            )
            findNavController().navigate(
                R.id.action_nmiPaymentFragment_to_paymentSuccessFragment2,
                bundle
            )
        } else {
            val bundle = Bundle()
            bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
            bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
            bundle.putBoolean(Constants.IS_DIRECT_DEBIT, isDrectDebit)

            findNavController().navigate(
                R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment,
                bundle
            )
        }
    }

}