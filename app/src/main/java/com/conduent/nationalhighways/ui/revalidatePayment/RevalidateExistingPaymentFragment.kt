package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidateExistingPaymentBinding
import com.conduent.nationalhighways.ui.auth.suspended.ManualTopUpViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RevalidateExistingPaymentFragment : BaseFragment<FragmentRevalidateExistingPaymentBinding>() {
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var paymentList: ArrayList<CardListResponseModel> = ArrayList()
    private var position: Int = 0
    private val manualTopUpViewModel: ManualTopUpViewModel by viewModels()
    private var responseModel: CardListResponseModel? = null
    private var cardValidationFirstTime: Boolean = true
    private var cardValidationSecondTime: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidateExistingPaymentBinding =
        FragmentRevalidateExistingPaymentBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }
        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.PAYMENT_LIST_DATA) != null) {
            paymentList =
                arguments?.getParcelableArrayList(Constants.PAYMENT_LIST_DATA) ?: ArrayList()
        }
        if (arguments?.containsKey(Constants.CARD_VALIDATION_FIRST_TIME) == true) {
            cardValidationFirstTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_FIRST_TIME) ?: true
        }
        if (arguments?.containsKey(Constants.CARD_VALIDATION_SECOND_TIME) == true) {
            cardValidationSecondTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_SECOND_TIME) ?: false
        }
        position = arguments?.getInt(Constants.POSITION, 0) ?: 0
        Log.e("TAG", "init: position " + position)
        Log.e("TAG", "init: paymentList " + paymentList)
        responseModel = paymentList[position]

        if (responseModel?.cardType.equals("visa", true)) {
            binding.ivCardType.setImageResource(R.drawable.visablue)
        } else if (responseModel?.cardType.equals("maestro", true)) {
            binding.ivCardType.setImageResource(R.drawable.maestro)

        } else {
            binding.ivCardType.setImageResource(R.drawable.mastercard)

        }
        val htmlText =
            Html.fromHtml(responseModel?.cardType?.uppercase() + "<br>" + responseModel?.cardNumber?.let {
                Utils.maskCardNumber(
                    it
                )
            }, Html.FROM_HTML_MODE_COMPACT)

        binding.tvSelectPaymentMethod.text = htmlText

        binding.cardView.contentDescription =
            responseModel?.cardType?.uppercase() + " " + Utils.accessibilityForNumbers(
                responseModel?.cardNumber?.let {
                    Utils.maskCardNumber(
                        it
                    )
                }.toString()
            )

    }

    override fun initCtrl() {
        binding.nextBtn.setOnClickListener {

            val bundle = Bundle()
            bundle.putDouble(Constants.PAYMENT_TOP_UP, 0.0)
            bundle.putInt(Constants.POSITION, 0)
            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
            bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            bundle.putParcelableArrayList(Constants.DATA, paymentList)
            bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.size)
            bundle.putBoolean(Constants.CARD_VALIDATION_FIRST_TIME, cardValidationFirstTime)
            bundle.putBoolean(Constants.CARD_VALIDATION_SECOND_TIME, cardValidationSecondTime)
            bundle.putBoolean(Constants.CARD_VALIDATION_PAYMENT_FAIL, true)
            bundle.putBoolean(Constants.CARD_VALIDATION_EXISTING_CARD, true)
            findNavController().navigate(
                R.id.action_reValidateExistingPaymentFragment_to_threeDSWebiewFragment,
                bundle
            )
//
//            val bundle = Bundle()
//            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
//
//            bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.size)
//            bundle.putInt(Constants.POSITION,position)
//            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
//            bundle.putParcelableArrayList(Constants.PAYMENT_LIST_DATA, paymentList)
//            findNavController().navigate(
//                R.id.action_reValidateExistingPaymentFragment_to_reValidateInfoFragment,
//                bundle
//            )
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(
                manualTopUpViewModel.paymentWithExistingCard,
                ::handlePaymentWithExistingCardResponse
            )
        }
    }


    private fun handlePaymentWithExistingCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    if (status.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.NAV_FLOW_FROM,
                            navFlowFrom
                        )
                        bundle.putBoolean(Constants.CARD_VALIDATION_FIRST_TIME, true)
                        bundle.putBoolean(Constants.CARD_VALIDATION_SECOND_TIME, false)
                        bundle.putBoolean(Constants.CARD_VALIDATION_PAYMENT_FAIL, true)
                        bundle.putBoolean(Constants.CARD_VALIDATION_EXISTING_CARD, true)
                        bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.size)
                        bundle.putParcelableArrayList(Constants.PAYMENT_LIST_DATA, paymentList)
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                        findNavController().navigate(
                            R.id.action_reValidateExistingPaymentFragment_to_reValidateInfoFragment,
                            bundle
                        )

                    }
                }
            }

            else -> {
            }
        }
    }


    private fun payWithExistingCard() {
//        val model = PaymentWithExistingCardModel(
//            addressline1 = personalInformation?.addressLine1.toString().replace(" ", ""),
//            addressline2 = personalInformation?.addressLine2.toString().replace(" ", ""),
//            transactionAmount = "",
//            cardType = "",
//            cardNumber = "",
//            cvv = "",
//            rowId = paymentList[position].rowId,
//            saveCard = "",
//            useAddressCheck = "N",
//            firstName = paymentList[position].firstName,
//            middleName = paymentList[position].middleName,
//            lastName = paymentList[position].lastName,
//            paymentType = "",
//            primaryCard = "",
//            maskedCardNumber = "",
//            easyPay = "",
//            cavv = paymentSuccessResponse?.cavv,
//            xid = paymentSuccessResponse?.xid,
//            threeDsVersion = paymentSuccessResponse?.threeDsVersion,
//            directoryServerId = paymentSuccessResponse?.directoryServerId,
//            cardHolderAuth = paymentSuccessResponse?.cardHolderAuth,
//            eci = paymentSuccessResponse?.eci
//
//
//        )
//        Log.d("paymentRequest", Gson().toJson(model))
//        manualTopUpViewModel.paymentWithExistingCard(model)
    }


}