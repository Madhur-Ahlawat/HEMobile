package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidateExistingPaymentBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.auth.suspended.ManualTopUpViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
import com.conduent.nationalhighways.utils.setAccessibilityDelegateForDigits
import dagger.hilt.android.AndroidEntryPoint

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
        responseModel = paymentList[position]
        binding.cardSecurityEt.editText.filters = arrayOf(InputFilter.LengthFilter(4))
        binding.ivCardType.setImageResource(Utils.setCardImage(responseModel?.cardType ?: ""))
        val htmlText =
            Html.fromHtml(responseModel?.cardType?.uppercase() + " " + responseModel?.cardNumber?.let {
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

        binding.cardSecurityEt.editText.addTextChangedListener(GenericTextWatcher())
        binding.checkBoxTerms.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.cardSecurityEt.setText(binding.cardSecurityEt.editText.text.toString())
            } else {
                binding.nextBtn.disable()
            }
        }
        binding.cardSecurityEt.editText.setAccessibilityDelegateForDigits()
        binding.checkBoxTerms.setAccessibilityDelegate()

        if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
        binding.cardSecurityEt.isDisplayClearIcon = false
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
            if (charSequence.toString().trim().isEmpty()) {
                binding.cardSecurityEt.removeError()
                binding.nextBtn.disable()
            } else if (charSequence.toString().trim().length < 3) {
                binding.nextBtn.disable()
                binding.cardSecurityEt.setErrorText(resources.getString(R.string.card_security_code_more_3characters))
            } else {
                binding.cardSecurityEt.removeError()
                checkEnableButton()
            }
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkEnableButton() {
        if (binding.checkBoxTerms.isChecked) {
            binding.nextBtn.enable()
        } else {
            binding.nextBtn.disable()
        }
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
            bundle.putString(
                Constants.CARD_SECURITY_CODE,
                binding.cardSecurityEt.editText.text.toString()
            )
            bundle.putBoolean(Constants.CARD_VALIDATION_FIRST_TIME, cardValidationFirstTime)
            bundle.putBoolean(Constants.CARD_VALIDATION_SECOND_TIME, cardValidationSecondTime)
            bundle.putBoolean(Constants.CARD_VALIDATION_PAYMENT_FAIL, true)
            bundle.putBoolean(Constants.CARD_VALIDATION_EXISTING_CARD, true)
            findNavController().navigate(
                R.id.action_reValidateExistingPaymentFragment_to_threeDSWebiewFragment,
                bundle
            )
        }
    }

    override fun observer() {
    }

}