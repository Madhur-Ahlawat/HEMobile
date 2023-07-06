package com.conduent.nationalhighways.ui.auth.suspended

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendPayBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.topup.ManualTopUpViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import kotlinx.coroutines.launch


class AccountSuspendPayFragment : BaseFragment<FragmentAccountSuspendPayBinding>(),
    View.OnClickListener {

    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var position: Int = 0
    private var lowBalance: Boolean = false
    private var loader: LoaderDialog? = null
    private val manualTopUpViewModel: ManualTopUpViewModel by viewModels()


    private var topUpAmount = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendPayBinding =
        FragmentAccountSuspendPayBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.DATA) != null) {
            paymentList = arguments?.getParcelableArrayList(Constants.DATA)
        }
        position = arguments?.getInt(Constants.POSITION, 0) ?: 0
        topUpAmount = arguments?.getString(Constants.PAYMENT_TOP_UP) ?: ""


        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.btnPay.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.lowBalance.editText.addTextChangedListener(GenericTextWatcher(0))

        binding.lowBalance.setText("£$topUpAmount")

        if (paymentList?.get(position)?.cardType.equals("visa", true)) {
            binding.ivCardType.setImageResource(R.drawable.visablue)
        } else if (paymentList?.get(position)?.cardType.equals("maestro", true)) {
            binding.ivCardType.setImageResource(R.drawable.maestro)

        } else {
            binding.ivCardType.setImageResource(R.drawable.mastercard)

        }

        binding.tvSelectPaymentMethod.text = paymentList?.get(position)?.cardNumber ?: ""


    }

    override fun observer() {
        lifecycleScope.launch {
            observe(
                manualTopUpViewModel.paymentWithExistingCard,
                ::handlePaymentWithExistingCardResponse
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnPay -> {
                payWithExistingCard()
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            }

            R.id.btnCancel -> {

            }
        }
    }

    private fun payWithExistingCard() {
        val model = PaymentWithExistingCardModel(
            transactionAmount = topUpAmount,
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


}