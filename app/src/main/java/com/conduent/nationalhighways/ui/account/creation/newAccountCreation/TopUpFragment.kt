package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.CreateAccountResponseModel
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.account.payment.VehicleItem
import com.conduent.nationalhighways.data.model.accountpayment.AccountGetThresholdResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.conduent.nationalhighways.databinding.FragmentTopUpBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CreateAccountViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.topup.AccountTopUpPaymentViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.SHOW_BACK_BUTTON
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class TopUpFragment : BaseFragment<FragmentTopUpBinding>(), View.OnClickListener {

    private var lowBalance: Boolean = false
    private var topUpBalance: Boolean = false
    private var navFlow: String = ""
    private var isViewCreated: Boolean = false
    private val viewModel: AccountTopUpPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var paymentListSize: Int = 0
    private var apiLowBalanceAmount: String = "5"
    private var apiTopUpAmountBalance: String = "10"
    private var gtwLowBalance: GenericTextWatcher? = null
    private var gtwTopBalance: GenericTextWatcher? = null
    private var isClick = false
    private val createAccountViewModel: CreateAccountViewModel by viewModels()


    val formatter = DecimalFormat("#,###.00")
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTopUpBinding = FragmentTopUpBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initCtrl() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0

        if (navFlow == Constants.THRESHOLD) {
            if (!isViewCreated) {
                getThresholdAmount()
            }
        }
        else{
            binding.minimumAMountTopUp.text = (getString(
                R.string.top_up_amount_value,
                this@TopUpFragment.apiTopUpAmountBalance.toDouble().toInt().toString()
            ))
            binding.minimumAmount.text = (getString(
                R.string.str_minimum_amount,
                this@TopUpFragment.apiLowBalanceAmount.toDouble().toInt().toString()
            ))
//            binding.lowBalance.setText("£" + formatter.format(this@TopUpFragment.apiLowBalanceAmount.toDouble()))
//            binding.top.setText("£" + formatter.format(this@TopUpFragment.apiTopUpAmountBalance.toDouble()))

        }
        isViewCreated = true
        binding.topUpBtn.setOnClickListener(this)
        binding.lowBalance.editText.setOnFocusChangeListener { _, b -> lowBalanceDecimal(b) }
        binding.top.editText.setOnFocusChangeListener { _, b -> topBalanceDecimal(b) }
        gtwTopBalance = GenericTextWatcher(true)
        gtwLowBalance = GenericTextWatcher(false)
        binding.top.editText.addTextChangedListener(gtwTopBalance)
        binding.lowBalance.editText.addTextChangedListener(gtwLowBalance)
        lowBalance = true
        topUpBalance = true
        checkButton()
    }

    inner class GenericTextWatcher(var isTopUp: Boolean) : TextWatcher {

        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            if (isTopUp) {
                topUpBalance =
                    Utils.validateAmount(
                        binding.top,
                        formatter.format(apiTopUpAmountBalance.toDouble()).toDouble(),
                        true
                    )
            } else {
                lowBalance =
                    Utils.validateAmount(
                        binding.lowBalance,
                        formatter.format(apiLowBalanceAmount.toDouble()).toDouble(),
                        false
                    )
            }
            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun getThresholdAmount() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getThresholdAmount()
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.thresholdLiveData, ::getThresholdApiResponse)
            observe(viewModel.updateAmountLiveData, ::updateThresholdApiResponse)
          //  observe(createAccountViewModel.account, ::handleAccountResponse)


        }
    }


    override fun onResume() {
        isClick = false
        super.onResume()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.topUpBtn -> {
                val bundle = Bundle()

                if (navFlow == Constants.THRESHOLD) {
                    val topupAmount = binding.top.editText.text.toString().trim().replace("$", "£")
                        .replace("£", "").replace(",", "").replace(" ", "")
                    val thresholdAmount =
                        binding.lowBalance.editText.text.toString().trim().replace("$", "£")
                            .replace("£", "").replace(",", "").replace(" ", "")

                    val request = AccountTopUpUpdateThresholdRequest(
                        thresholdAmount,
                        topupAmount
                    )
                    /* loader?.show(
                         requireActivity().supportFragmentManager,
                         Constants.LOADER_DIALOG
                     )*/
                    viewModel.updateThresholdAmount(request)
                    binding.topUpBtn.isEnabled = false
                    isClick = true

                } else {
                   emailHeartBeatApi()
                   smsHeartBeatApi()
                   // callAccountCreationApi("2.1.0","AAIBBYNoEwAAACcKhAJkdQAAAAA=","e86cc862-2f96-4b6c-bd99-3feb448a1671","05")

                    val amount = binding.top.editText.text.toString().trim().replace("$", "£")
                        .replace("£", "")
                    val thresholdAmount =
                        binding.lowBalance.editText.text.toString().trim().replace("$", "£")
                            .replace("£", "")
                    bundle.putDouble(Constants.DATA, amount.toDouble())
                    bundle.putDouble(Constants.THRESHOLD_AMOUNT, thresholdAmount.toDouble())
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.NOTSUSPENDED)
                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)


                    findNavController().navigate(
                        R.id.action_topUpFragment_to_nmiPaymentFragment,
                        bundle
                    )
                }

            }

            R.id.lowBalance -> {
                /* val bundle = Bundle()

                 bundle.putString(Constants.LOW_BALANCE, Constants.LOW_BALANCE)
                 bundle.putString(Constants.TOP_UP_AMOUNT, binding.top.getText().toString())
                 bundle.putString(Constants.LOW_BALANCE_AMOUNT, binding.lowBalance.editText.getText().toString())


                 findNavController().navigate(
                     R.id.action_topUpFragment_to_amountKeyPadFragment,
                     bundle
                 )*/
            }

            R.id.top -> {
                /* val bundle = Bundle()

                 bundle.putString(Constants.LOW_BALANCE, Constants.TOP_UP_BALANCE)
                 bundle.putString(Constants.LOW_BALANCE_AMOUNT, binding.lowBalance.editText.getText().toString())
                 bundle.putString(Constants.TOP_UP_AMOUNT,binding.top.editText.getText().toString())


                 findNavController().navigate(
                     R.id.action_topUpFragment_to_amountKeyPadFragment,
                     bundle
                 )*/

            }
        }

    }

    private fun callAccountCreationApi(
        threeDsVersion: String?,
        cavv: String?,
        directoryServerId: String?,
        eci: String?
    ) {
        //showLoader()
        val data = NewCreateAccountRequestModel
        val model = AccountCreationRequest()
        model.stateType = "HE"
        model.cardStateType = "HE"
        model.tcAccepted = "Y"
        model.mailPreference = "Y"
        model.emailPreference = "Y"
        model.postCode=NewCreateAccountRequestModel.zipCode
        model.addressLine2="Small Heath"
        if (NewCreateAccountRequestModel.twoStepVerification) {
            model.mfaFlag = "Y"

        } else {
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
            model.countryType = "NON-UK"

        }
        model.referenceId = data.referenceId
        if (NewCreateAccountRequestModel.communicationTextMessage || NewCreateAccountRequestModel.twoStepVerification) {
            model.cellPhone = data.mobileNumber
            model.cellPhoneCountryCode = data.countryCode?.let { getRequiredText(it) }
            model.smsReferenceId = data.sms_referenceId

        } else {
            model.eveningPhone = data.telephoneNumber
            model.eveningPhoneCountryCode = data.telephone_countryCode?.let { getRequiredText(it) }
            model.smsReferenceId = ""
        }
        model.address1 = data.addressline1
        model.billingAddressLine1 = data.addressline1
        model.emailAddress = data.emailAddress
        model.creditCExpMonth = "12"
        model.creditCExpYear = "2025"
       /* if (isTrusted) {*/
            model.cardholderAuth =
                "verified"// if istrusted is true then we need to send verified else empty
       /* } else {
            model.cardholderAuth = ""
        }
*/
        if (NewCreateAccountRequestModel.prePay){
                model.transactionAmount ="10.00"


        }

        model.thresholdAmount ="10.00"
        model.securityCode = ""
        model.securityCd = data.emailSecurityCode   // email security code
        model.cardFirstName = data.firstName   // model name
        model.cardCity = data.townCity   // address city
        model.city = data.townCity   // address city
        model.threeDsVer = threeDsVersion  // 3ds verison
        model.maskedNumber ="************1111"
        model.creditCardNumber ="3seccS6c-MvVmh6-VC3xWq-GJgd86T5Ny9T"// card number should be token number
        model.cavv = cavv // 3ds cavv
        model.password = data.password  //model password
        model.firstName = data.firstName
        model.creditCardType = "VISA" // need to send upper case
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
        model.correspDeliveryMode=""
        model.eci = eci // 3ds eci
        model.replenishmentAmount = "10.00" // top up amount
        model.directoryServerID = directoryServerId // 3ds serverId
        if (NewCreateAccountRequestModel.communicationTextMessage) {
            model.smsOption = "Y"

        } else {
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
            // item.plateTypeDesc = "STANDARD"
            item.plateCountry = "UK"
            item.vehicleYear = "2023"
            listVehicle.add(item)

        }
        model.ftvehicleList.vehicle = listVehicle
        createAccountViewModel.createAccountNew(model)


    }
    private fun handleAccountResponse(response: Resource<CreateAccountResponseModel?>?) {
        when (response) {
            is Resource.Success -> {
                if (response.data?.statusCode.equals("0")) {
                    /*clearSingletonData()
                    val bundle = Bundle()
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    bundle.putParcelable(Constants.DATA, response.data)

                    findNavController().navigate(
                        R.id.action_nmiPaymentFragment_to_accountCreatedSuccessfullyFragment,
                        bundle
                    )*/
                }


            }

            is Resource.DataError -> {
                if ((response.errorModel?.errorCode == Constants.TOKEN_FAIL && response.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || response.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(response.errorModel)
                } else {
                   // findNavController().navigate(R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment)
                }
            }

            else -> {
               // findNavController().navigate(R.id.action_nmiPaymentFragment_to_tryPaymentAgainFragment)

            }

        }
    }

    private fun getRequiredText(text: String) = text.substringAfter('(').replace(")", "")


    private fun topBalanceDecimal(b: Boolean) {
        if (!b) {
            var mText = binding.top.getText().toString().trim()
            if (mText.isNullOrEmpty()) {
                mText = "0.0"
            }

            mText = mText.replace("$", "").replace("£", "").replace("£.", "").replace(",", "")
                .replace(" ", "")
            if (mText.length == 1 && mText.equals(".")) {
                mText = "0.0"
            }
            var formatedAmount = formatter.format(mText.toDouble().toInt())
            if (!formatedAmount.isNullOrEmpty() && formatedAmount.equals(".00")) {
                formatedAmount = "0.00"
            }
            binding.top.setText("£" + formatedAmount)
        }
    }

    private fun lowBalanceDecimal(b: Boolean) {
        if (!b) {
            var mText = binding.lowBalance.getText().toString().trim()
            if (mText.isNullOrEmpty()) {
                mText = "0.0"
            }

            mText = mText.replace("$", "").replace("£", "").replace("£.", "").replace(",", "")
                .replace(" ", "")
            if (mText.length == 1 && mText.equals(".")) {
                mText = "0.0"
            }
            var formatedAmount = formatter.format(mText.toDouble().toInt())
            if (!formatedAmount.isNullOrEmpty() && formatedAmount.equals(".00")) {
                formatedAmount = "0.00"
            }
            binding.lowBalance.setText("£" + formatedAmount)
        }
    }

    private fun checkButton() {
        binding.topUpBtn.isEnabled = lowBalance && topUpBalance
    }

    private fun getThresholdApiResponse(resource: Resource<AccountGetThresholdResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.apply {
                    if (statusCode == "0") {
                        binding.apply {
                            if (thresholdAmountVo?.thresholdAmount?.isNotEmpty() == true) {
//                                binding.lowBalance.editText.removeTextChangedListener(gtwLowBalance)
//                                binding.lowBalance.editText.setText(
//                                    "£" + formatter.format(
//                                        thresholdAmountVo.thresholdAmount!!.toDouble()
//                                    )
//                                )
//
//                                binding.lowBalance.editText.addTextChangedListener(gtwLowBalance)

                                this@TopUpFragment.apiLowBalanceAmount = thresholdAmountVo.thresholdAmount.toString()
                                binding.minimumAmount.text = getString(
                                    R.string.str_minimum_amount,
                                    this@TopUpFragment.apiLowBalanceAmount
                                )

                            }

                            if (thresholdAmountVo?.customerAmount?.isNotEmpty() == true) {
//                                binding.top.editText.removeTextChangedListener(gtwTopBalance)
//                                binding.top.editText.setText(
//                                    "£" + formatter.format(
//                                        thresholdAmountVo.customerAmount!!.toDouble()
//                                    )
//                                )
//                                binding.top.editText.addTextChangedListener(gtwTopBalance)
                                this@TopUpFragment.apiTopUpAmountBalance = thresholdAmountVo.customerAmount.toString()

                                binding.minimumAMountTopUp.text = (getString(
                                    R.string.top_up_amount_value,
                                    this@TopUpFragment.apiTopUpAmountBalance
                                ))

                            }

                        }
                    }
                }
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
        checkButton()
    }

    private fun updateThresholdApiResponse(resource: Resource<AccountTopUpUpdateThresholdResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                binding.topUpBtn.isEnabled = true

                if (resource.data?.statusCode == "0") {
                    val bundle = Bundle()


                    val amount = binding.top.editText.text.toString().trim().replace("£", "")
                    val thresholdAmount =
                        binding.lowBalance.editText.text.toString().trim().replace("£", "")


                    if (navFlow != Constants.THRESHOLD && apiLowBalanceAmount.toInt()
                            .toString() == thresholdAmount.toInt().toString()
                            .trim()
                    ) {
                        bundle.putString(Constants.THRESHOLD_AMOUNT, "")

                    } else {
                        bundle.putString(
                            Constants.THRESHOLD_AMOUNT,
                            binding.lowBalance.editText.text.toString().trim()
                        )

                    }


                    if (navFlow != Constants.THRESHOLD && apiTopUpAmountBalance == amount) {
                        bundle.putString(Constants.TOP_UP_AMOUNT, "")

                    } else {
                        bundle.putString(
                            Constants.TOP_UP_AMOUNT,
                            binding.top.editText.text.toString().trim()
                        )

                    }
                    bundle.putBoolean(SHOW_BACK_BUTTON, false)
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlow)

                    if (isClick) {
                        findNavController().navigate(
                            R.id.action_topUpFragment_to_deletePaymentMethodSuccessFragment,
                            bundle
                        )

                    }
                    isClick = false

                }
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
    }


}