package com.conduent.nationalhighways.ui.auth.suspended

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithNewCardModel
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendPayBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Locale

@AndroidEntryPoint
class AccountSuspendPayFragment : BaseFragment<FragmentAccountSuspendPayBinding>(),
    View.OnClickListener {
    private var responseModel: CardResponseModel? = null

    private var paymentList: ArrayList<CardListResponseModel> = ArrayList()
    private var position: Int = 0
    private val manualTopUpViewModel: ManualTopUpViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private var currentBalance: String = ""
    private var paymentSuccessResponse: PaymentSuccessResponse? = null
    private val formatter = DecimalFormat("#,###.00")
    private var cardModel: PaymentWithNewCardModel? = null

    private var topUpAmount = 0.0
    private var paymentListSize: Int = 0
    private var lowBalance: Boolean = true


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendPayBinding =
        FragmentAccountSuspendPayBinding.inflate(inflater, container, false)


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initCtrl() {
        val receivedList = arguments?.getParcelableArrayList<CardListResponseModel>(Constants.DATA)


        if (receivedList != null) {
            paymentList = receivedList
        }

        if (arguments?.containsKey(Constants.PAYMENT_METHOD_SIZE) == true) {
            paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0
        }
        position = arguments?.getInt(Constants.POSITION, 0) ?: 0
        topUpAmount = arguments?.getDouble(Constants.PAYMENT_TOP_UP) ?: 0.0

        if (arguments?.getParcelable<PaymentSuccessResponse>(Constants.NEW_CARD) != null) {
            paymentSuccessResponse = arguments?.getParcelable(Constants.NEW_CARD)
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""

        if (arguments?.getParcelable<CardResponseModel>(Constants.DATA) != null) {
            responseModel = arguments?.getParcelable(Constants.DATA)

            if (responseModel?.card?.type.equals("visa", true)) {
                binding.ivCardType.setImageResource(R.drawable.visablue)
            } else if (responseModel?.card?.type.equals("maestro", true)) {
                binding.ivCardType.setImageResource(R.drawable.maestro)

            } else {
                binding.ivCardType.setImageResource(R.drawable.mastercard)

            }
            val htmlText =
                Html.fromHtml(responseModel?.card?.type?.uppercase() + "<br>" + responseModel?.card?.number?.let {
                    Utils.maskCardNumber(
                        it
                    )
                }, Html.FROM_HTML_MODE_COMPACT)

            binding.tvSelectPaymentMethod.text = htmlText

            binding.cardView.contentDescription =
                responseModel?.card?.type?.uppercase() + " " + Utils.accessibilityForNumbers(
                    responseModel?.card?.number?.let {
                        Utils.maskCardNumber(
                            it
                        )
                    }.toString()
                )
        }
        if (navFlowCall == Constants.PAYMENT_TOP_UP) {
            if (requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain).setTitle(resources.getString(R.string.str_top_up))
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun init() {
        binding.lowBalance.editText.addTextChangedListener(GenericTextWatcher())
        binding.lowBalance.editText.setOnFocusChangeListener { _, b -> topBalanceDecimal(b) }
        binding.lowBalance.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                topBalanceDecimal(false)
                true // Return true to consume the action
            } else {
                false // Return false if you want the event to propagate further
            }
        }

        binding.btnPay.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.lowBalance.setText(
            "£" + formatter.format(topUpAmount)
        )

        if (paymentList.isNotEmpty()) {
            binding.ivCardType.setImageResource(
                Utils.setCardImage(
                    paymentList[position].cardType
                )
            )

            val htmlText = Html.fromHtml(
                paymentList[position].cardType + "<br>" + Utils.maskCardNumber(
                    paymentList[position].cardNumber
                ),
                Html.FROM_HTML_MODE_COMPACT
            )

            binding.tvSelectPaymentMethod.text = htmlText
            if (requireActivity() is MakeOffPaymentActivity) {
                (requireActivity() as MakeOffPaymentActivity).focusMakeOffToolBar()
            }

            binding.cardView.contentDescription =
                paymentList[position].cardType + " " + Utils.accessibilityForNumbers(
                    Utils.maskCardNumber(
                        paymentList[position].cardNumber
                    )
                )


        }
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome(Constants.AccountSuspendPay)
        } else if (requireActivity() is AuthActivity) {
//            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
    }

    override fun observer() {

        lifecycleScope.launch {
            observe(
                manualTopUpViewModel.paymentWithExistingCard,
                ::handlePaymentWithExistingCardResponse
            )
            observe(manualTopUpViewModel.paymentWithNewCard, ::handlePaymentWithNewCardResponse)

        }
    }

    private fun topBalanceDecimal(b: Boolean) {
        if (!b) {
            var mText =
                binding.lowBalance.getText().toString().trim().replace("$", "").replace("£", "")
                    .replace("£.", "").replace(",", "")
                    .replace(" ", "")
            if (mText.isEmpty()) {
                mText = "£0.0"
            }

            mText = mText.replace("$", "").replace("£", "").replace("£.", "").replace(",", "")
                .replace(" ", "")
            if (mText.length == 1 && mText == ".") {
                mText = "0.0"
            }
            var formattedAmount = formatter.format(mText.toDouble())
            if (!formattedAmount.isNullOrEmpty() && formattedAmount.equals(".00")) {
                formattedAmount = "0.00"
            }
            binding.lowBalance.setText(resources.getString(R.string.price, "" + formattedAmount))
            // Assuming editText is your EditText view
            binding.lowBalance.setSelection(binding.lowBalance.editText.text?.length ?: 0)

        }

    }

    inner class GenericTextWatcher : TextWatcher {

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
            lowBalance = Utils.validateAmount(binding.lowBalance, 10.00, true)
            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {
        binding.btnPay.isEnabled = lowBalance
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnPay -> {
                if (Utils.validateAmount(binding.lowBalance, 10.00, true)) {
                    if (responseModel != null) {
                        if (responseModel?.checkCheckBox == true) {
                            newPaymentMethod("Y")
                        } else {
                            newPaymentMethod("N")
                        }
                    } else {
                        payWithExistingCard()
                    }
                }
                showLoaderDialog()
            }

            R.id.btnCancel -> {
                if (navFlowCall == Constants.PAYMENT_TOP_UP) {
                    findNavController().popBackStack(R.id.accountSuspendedPaymentFragment, false)
                } else {
                    requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                        putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                    }

                }

            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun newPaymentMethod(easyPay: String) {
        var primaryCard = "N"

        if (paymentListSize == 0) {
            primaryCard = "Y"

        }
        cardModel = PaymentWithNewCardModel(
            addressLine1 = personalInformation?.addressLine1.toString(),
            addressLine2 = personalInformation?.addressLine1.toString(),
            bankRoutingNumber = "",
            cardNumber = responseModel?.token,
            cardType = responseModel?.card?.type?.uppercase(Locale.ROOT),
            city = personalInformation?.city,
            country = "UK",
            cvv = "",
            easyPay = easyPay,
            expMonth = responseModel?.card?.exp?.substring(0, 2),
            expYear = "20${responseModel?.card?.exp?.substring(2, 4)}",
            firstName = responseModel?.check?.name ?: "",
            middleName = "",
            lastName = "",
            maskedNumber = Utils.maskCardNumber(responseModel?.card?.number.toString()),
            paymentType = "card",
            primaryCard = primaryCard,
            saveCard = easyPay,
            state = "HE",
            transactionAmount = binding.lowBalance.getText().toString().trim().replace("£", "")
                .replace(",", "").replace(" ", ""),
            useAddressCheck = "N",
            personalInformation?.zipcode.toString().replace(" ", ""),
            "",
            directoryServerID = paymentSuccessResponse?.directoryServerId.toString(),
            cavv = paymentSuccessResponse?.cavv.toString(),
            threeDsVer = paymentSuccessResponse?.threeDsVersion.toString(),
            cardholderAuth = paymentSuccessResponse?.cardHolderAuth.toString(),
            eci = paymentSuccessResponse?.eci.toString()


        )
        Log.d("paymentRequest", Gson().toJson(cardModel))

        manualTopUpViewModel.paymentWithNewCard(cardModel)
    }

    private fun payWithExistingCard() {
        val model = PaymentWithExistingCardModel(
            addressline1 = personalInformation?.addressLine1.toString().replace(" ", ""),
            addressline2 = personalInformation?.addressLine2.toString().replace(" ", ""),
            transactionAmount = topUpAmount.toString(),
            cardType = "",
            cardNumber = "",
            cvv = "",
            rowId = paymentList[position].rowId,
            saveCard = "",
            useAddressCheck = "N",
            firstName = paymentList[position].firstName,
            middleName = paymentList[position].middleName,
            lastName = paymentList[position].lastName,
            paymentType = "",
            primaryCard = "",
            maskedCardNumber = "",
            easyPay = "",
            cavv = paymentSuccessResponse?.cavv,
            xid = paymentSuccessResponse?.xid,
            threeDsVersion = paymentSuccessResponse?.threeDsVersion,
            directoryServerId = paymentSuccessResponse?.directoryServerId,
            cardHolderAuth = paymentSuccessResponse?.cardHolderAuth,
            eci = paymentSuccessResponse?.eci


        )
        Log.d("paymentRequest", Gson().toJson(model))
        manualTopUpViewModel.paymentWithExistingCard(model)
    }

    private fun handlePaymentWithExistingCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {

                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA, responseModel)
                bundle.putString(Constants.TOP_UP_AMOUNT, topUpAmount.toString())
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                bundle.putString(Constants.TRANSACTIONID, status.data?.transactionId)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

                findNavController().navigate(
                    R.id.action_accountSuspendedFinalPayFragment_to_accountSuspendReOpenFragment,
                    bundle
                )

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    if (status.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
                        redirectToTryAgainPage()
                    }
                }
            }

            else -> {
            }
        }
    }

    private fun handlePaymentWithNewCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("0") == true) {
                    val bundle = Bundle()
                    bundle.putString(Constants.TOP_UP_AMOUNT, topUpAmount.toString())
                    bundle.putParcelable(Constants.DATA, responseModel)
                    bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putString(Constants.TRANSACTIONID, status.data.transactionId)
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                    bundle.putBoolean(Constants.NEW_CARD, true)


                    findNavController().navigate(
                        R.id.action_accountSuspendedFinalPayFragment_to_accountSuspendReOpenFragment,
                        bundle
                    )
                } else if (status.data?.statusCode?.equals("1337") == true && status.data.transactionId != null) {
                    if (navFlowCall == Constants.PAYMENT_TOP_UP || navFlowCall == Constants.SUSPENDED) {
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.CARD_IS_ALREADY_REGISTERED,
                            Constants.CARD_IS_ALREADY_REGISTERED
                        )
                        bundle.putString(Constants.TOP_UP_AMOUNT, topUpAmount.toString())
                        bundle.putParcelable(Constants.DATA, responseModel)
                        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        bundle.putBoolean(Constants.NEW_CARD, true)
                        bundle.putString(Constants.TRANSACTIONID, status.data.transactionId)
                        bundle.putString(
                            Constants.CARD_IS_ALREADY_REGISTERED,
                            Constants.CARD_IS_ALREADY_REGISTERED
                        )
//                    bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
//                    bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)

                        findNavController().navigate(
                            R.id.action_accountSuspendPayFragment_to_accountSuspendReOpenFragment,
                            bundle
                        )
                    } else {
                        displayCustomMessage(
                            getString(R.string.str_warning),
                            getString(R.string.the_card_you_are_trying_to_add_is_already),
                            getString(R.string.str_add_another_card_small),
                            getString(R.string.cancel),
                            object : DialogPositiveBtnListener {
                                override fun positiveBtnClick(dialog: DialogInterface) {
                                    val fragmentId = findNavController().currentDestination?.id
                                    findNavController().popBackStack(fragmentId!!, true)
                                }
                            },
                            object : DialogNegativeBtnListener {
                                override fun negativeBtnClick(dialog: DialogInterface) {
                                    val bundle = Bundle()
                                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                                    bundle.putParcelable(
                                        Constants.PERSONALDATA,
                                        personalInformation
                                    )
                                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                                    bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                                    bundle.putString(Constants.CURRENTBALANCE, currentBalance)

                                    findNavController().navigate(
                                        R.id.action_accountSuspendedFinalPayFragment_to_accountSuspendedPaymentFragment,
                                        bundle
                                    )
                                }
                            })
                    }
                } else {
                    redirectToTryAgainPage()
                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    if (status.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
                        redirectToTryAgainPage()
                    }
                }
            }

            else -> {
            }
        }
    }

    private fun redirectToTryAgainPage() {
        val bundle = Bundle()
        bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
        findNavController().navigate(
            R.id.action_accountSuspendedFinalPayFragment_to_tryPaymentAgainFragment, bundle
        )
    }
}

