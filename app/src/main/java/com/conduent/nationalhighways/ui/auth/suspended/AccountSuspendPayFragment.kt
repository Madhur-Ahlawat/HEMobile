package com.conduent.nationalhighways.ui.auth.suspended

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Html
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
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithNewCardModel
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendPayBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.topup.ManualTopUpViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class AccountSuspendPayFragment : BaseFragment<FragmentAccountSuspendPayBinding>(),
    View.OnClickListener {
    private var responseModel: CardResponseModel? = null

    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var position: Int = 0
    private var lowBalance: Boolean = false
    private var loader: LoaderDialog? = null
    private val manualTopUpViewModel: ManualTopUpViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private var currentBalance: String = ""
    private var paymentSuccessResponse: PaymentSuccessResponse? = null
    private var navFlow: String = ""

    private var cardModel: PaymentWithNewCardModel? = null

    private var topUpAmount = 0.0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendPayBinding =
        FragmentAccountSuspendPayBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.DATA) != null) {
            paymentList = arguments?.getParcelableArrayList(Constants.DATA)
        }
        position = arguments?.getInt(Constants.POSITION, 0) ?: 0
        topUpAmount = arguments?.getDouble(Constants.PAYMENT_TOP_UP) ?: 0.0

        if (arguments?.getParcelable<PaymentSuccessResponse>(Constants.NEW_CARD) != null) {
            paymentSuccessResponse = arguments?.getParcelable(Constants.NEW_CARD)
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)



        if (arguments?.getParcelable<CardResponseModel>(Constants.DATA) != null) {
            responseModel = arguments?.getParcelable<CardResponseModel>(Constants.DATA)

            if (responseModel?.card?.type.equals("visa", true)) {
                binding.ivCardType.setImageResource(R.drawable.visablue)
            } else if (responseModel?.card?.type.equals("maestro", true)) {
                binding.ivCardType.setImageResource(R.drawable.maestro)

            } else {
                binding.ivCardType.setImageResource(R.drawable.mastercard)

            }
            val htmlText =
                Html.fromHtml(responseModel?.card?.type?.uppercase() + "<br>" + responseModel?.card?.number)

            binding.tvSelectPaymentMethod.text = htmlText


        }

        if (arguments?.getString(Constants.NAV_FLOW_KEY) != null) {
            navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""

        }
    }

    override fun init() {

        binding.btnPay.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.lowBalance.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.lowBalance.editText.setOnFocusChangeListener { _, b -> topBalanceDecimal(b) }


        binding.lowBalance.setText("£" + String.format("%.2f", topUpAmount))
        if (paymentList?.isNotEmpty() == true) {
            if (paymentList?.get(position)?.cardType.equals("visa", true)) {
                binding.ivCardType.setImageResource(R.drawable.visablue)
            } else if (paymentList?.get(position)?.cardType.equals("maestro", true)) {
                binding.ivCardType.setImageResource(R.drawable.maestro)

            } else {
                binding.ivCardType.setImageResource(R.drawable.mastercard)

            }
            val htmlText = Html.fromHtml(
                paymentList?.get(position)?.cardType + "<br>" + paymentList?.get(position)?.cardNumber
            )

            binding.tvSelectPaymentMethod.text = htmlText

        }


    }

    private fun topBalanceDecimal(b: Boolean) {
        if (b.not()) {
            val text = binding.lowBalance.getText().toString().trim()
            val updatedText = text.replace("£", "")
            if (updatedText.isNotEmpty() && updatedText.contains(".").not()) {
                binding.lowBalance.setText(String.format("%.2f", updatedText.toDouble()))
            }
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

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnPay -> {
                val bundle = Bundle()

                if (responseModel != null) {
                    if (responseModel?.checkCheckBox == true) {
                        newPaymentMethod("Y")
                    } else {
                        newPaymentMethod("N")
                    }

                    bundle.putParcelable(Constants.DATA, responseModel)
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                    bundle.putString(Constants.TRANSACTIONID, "541232435465")
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlow)


                } else {
                    payWithExistingCard()
                    //  bundle.putParcelable(Constants.DATA, status.data)
                    bundle.putString("amount", arguments?.getString("amount"))
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                    bundle.putString(Constants.TRANSACTIONID, "761234567892")
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlow)

                }
                /* findNavController().navigate(
                     R.id.action_accountSuspendedFinalPayFragment_to_accountSuspendReOpenFragment,
                     bundle
                 )*/
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            }

            R.id.btnCancel -> {
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun newPaymentMethod(s: String) {
        cardModel = PaymentWithNewCardModel(
            addressLine1 = personalInformation?.addressLine1.toString().replace(" ",""),
            addressLine2 = personalInformation?.addressLine2.toString().replace(" ",""),
            bankRoutingNumber = "",
            cardNumber = responseModel?.token,
            cardType = responseModel?.card?.type?.uppercase(Locale.ROOT),
            city = personalInformation?.city,
            country = personalInformation?.country,
            cvv = "",
            easyPay = "N",
            expMonth = responseModel?.card?.exp?.substring(0, 2),
            expYear = "20${responseModel?.card?.exp?.substring(2, 4)}",
            firstName = responseModel?.check?.name ?: "",
            middleName = "",
            lastName = "",
            maskedNumber = Utils.maskCardNumber(responseModel?.card?.number.toString()),
            paymentType = "card",
            primaryCard = "N",
            saveCard = s,
            state = "HE",
            transactionAmount = binding.lowBalance.getText().toString().trim().replace("£", ""),
            useAddressCheck = "N",
            personalInformation?.zipcode.toString().replace(" ",""),
            "",
            directoryServerId = paymentSuccessResponse?.directoryServerId.toString(),
            cavv = paymentSuccessResponse?.cavv.toString(),
            threeDsVersion = paymentSuccessResponse?.threeDsVersion.toString(),
            cardHolderAuth = paymentSuccessResponse?.cardHolderAuth.toString(),
            eci = paymentSuccessResponse?.eci.toString()


        )

        manualTopUpViewModel.paymentWithNewCard(cardModel)
    }

    private fun payWithExistingCard() {
        val model = PaymentWithExistingCardModel(
            addressline1 = personalInformation?.addressLine1.toString().replace(" ",""),
            addressline2=personalInformation?.addressLine2.toString().replace(" ",""),
            transactionAmount = topUpAmount.toString(),
            cardType = "",
            cardNumber = "",
            cvv = "",
            rowId = paymentList?.get(position)?.rowId,
            saveCard = "",
            useAddressCheck = "N",
            firstName = paymentList?.get(position)?.firstName,
            middleName = paymentList?.get(position)?.middleName,
            lastName = paymentList?.get(position)?.lastName,
            paymentType = "",
            primaryCard = "",
            maskedCardNumber = "",
            easyPay = ""
        )
        Log.d("paymentRequest", Gson().toJson(model))
        manualTopUpViewModel.paymentWithExistingCard(model)
    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {

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

            if (index == 0) {

                val text = binding.lowBalance.getText().toString().trim()
                val updatedText = text.replace("£", "")

                if (updatedText.isNotEmpty()) {
                    val str: String = updatedText.substringBeforeLast(".")
                    lowBalance = if (str.length < 8) {
                        if (updatedText.toDouble() < 5) {
                            binding.lowBalance.setErrorText(getString(R.string.str_top_up_amount_must_be_more))
                            false

                        } else {
                            binding.lowBalance.removeError()
                            true
                        }
                    } else {
                        binding.lowBalance.setErrorText(getString(R.string.str_top_up_amount_must_be_8_characters))
                        false
                    }

                } else {
                    binding.lowBalance.removeError()
                }
                binding.lowBalance.editText.removeTextChangedListener(this)
                if (updatedText.isNotEmpty())
                    binding.lowBalance.setText("£$updatedText")
                Selection.setSelection(
                    binding.lowBalance.getText(),
                    binding.lowBalance.getText().toString().length
                )
                binding.lowBalance.editText.addTextChangedListener(this)
            }


            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {
        binding.btnPay.isEnabled = lowBalance

    }

    private fun handlePaymentWithExistingCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("500") == true) {
                    ErrorUtil.showError(binding.root, status.data.message)
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.DATA, status.data)
                    bundle.putString("amount", arguments?.getString("amount"))
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                    bundle.putString(Constants.TRANSACTIONID, status.data?.transactionId)
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                    findNavController().navigate(
                        R.id.action_accountSuspendedFinalPayFragment_to_accountSuspendReOpenFragment,
                        bundle
                    )

                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    private fun handlePaymentWithNewCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("0") == true) {
                    val bundle = Bundle()
                    bundle.putString("amount", arguments?.getString("amount"))
                    bundle.putParcelable(Constants.DATA, responseModel)
                    bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlow)

                    bundle.putString(Constants.TRANSACTIONID, status.data.transactionId)

                    findNavController().navigate(
                        R.id.action_accountSuspendedFinalPayFragment_to_accountSuspendReOpenFragment,
                        bundle
                    )
                } else {
                    ErrorUtil.showError(binding.root, status.data?.message)
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }


}