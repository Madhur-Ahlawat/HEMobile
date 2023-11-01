package com.conduent.nationalhighways.ui.bottomnav.dashboard.topup

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.payment.*
import com.conduent.nationalhighways.databinding.FragmentManualTopUpCardBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentCardAdapter
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ManualTopUpCardFragment : BaseFragment<FragmentManualTopUpCardBinding>(),
    View.OnClickListener, (Boolean?, Int?, CardListResponseModel?) -> Unit {

    private val paymentViewModel: PaymentMethodViewModel by viewModels()
    private val manualTopUpViewModel: ManualTopUpViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var rowId: String? = null
    private var isDefaultDeleted = false
    private var cardsList: MutableList<CardListResponseModel?>? = ArrayList()
    private var position: Int? = 0
    private var defaultCardModel: CardListResponseModel? = null
    private var defaultConstantCardModel: CardListResponseModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentManualTopUpCardBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        paymentViewModel.saveCardList()
        binding.tieAmount.setText(arguments?.getString("amount"))
    }


    override fun initCtrl() {
        binding.apply {
            binding.rbDefaultMethod.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && cardsList?.isNotEmpty() == true) {
                    binding.rvOtherPayment.adapter?.let { adapter ->
                        (adapter as PaymentCardAdapter).clearAllChecks()
                    }
                }
            }
            binding.rbAddCard.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && cardsList?.isNotEmpty() == true) {
                    binding.rvOtherPayment.adapter?.let { adapter ->
                        (adapter as PaymentCardAdapter).clearAllChecks()
                    }
                }
            }
            binding.rbDirectDebit.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && cardsList?.isNotEmpty() == true) {
                    binding.rvOtherPayment.adapter?.let { adapter ->
                        (adapter as PaymentCardAdapter).clearAllChecks()
                    }
                }
            }
            btnContinue.setOnClickListener(this@ManualTopUpCardFragment)
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(paymentViewModel.savedCardList, ::handleSaveCardResponse)
            observe(
                manualTopUpViewModel.paymentWithExistingCard,
                ::handlePaymentWithExistingCardResponse
            )
        }

        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {

                when (binding.rgPayment.checkedRadioButtonId) {
                    R.id.rbAddCard -> {
                        val bundle = Bundle()
                        bundle.putString("amount", arguments?.getString("amount"))
                        findNavController().navigate(
                            R.id.action_manualTopUpCardFragment_to_manualTopUpAddCardFragment,
                            bundle
                        )
                    }
                    R.id.rbDirectDebit -> {
                        showError(binding.root, "Development is in progress")
                    }
                    else -> {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )

                        val model = PaymentWithExistingCardModel(
                            transactionAmount = arguments?.getString("amount"),
                            cardType = "",
                            cardNumber = "",
                            cvv = "",
                            rowId = defaultCardModel?.rowId,
                            saveCard ="",
                            useAddressCheck = "N",
                            firstName = defaultCardModel?.firstName,
                            middleName = defaultCardModel?.middleName?:"",
                            lastName = defaultCardModel?.lastName,
                            paymentType ="",
                            primaryCard = "",
                            maskedCardNumber = "",
                            easyPay = "",

                        )
                        manualTopUpViewModel.paymentWithExistingCard(model)
                    }
                }
            }
        }
    }


    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        binding.clMain.visible()
        when (status) {
            is Resource.Success -> {
                cardsList?.clear()
                cardsList = status.data?.creditCardListType?.cardsList
                defaultCardModel =
                    status.data?.creditCardListType?.cardsList?.filter { it?.primaryCard == true }
                        ?.get(0)
                defaultConstantCardModel = defaultCardModel
                if (defaultCardModel == null) {
                    binding.tvDefaultLabel.gone()
                    binding.rbDefaultMethod.gone()
//                    binding.viewDefault.gone()
                } else {
                    val spannableString =
                        if (defaultCardModel?.bankAccount == true) SpannableString(defaultCardModel?.bankAccountType + "\n" + defaultCardModel?.bankAccountNumber)
                        else SpannableString(defaultCardModel?.cardType + "\n" + defaultCardModel?.cardNumber)
                    spannableString.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.txt_disable
                            )
                        ),
                        spannableString.length - (defaultCardModel?.cardNumber?.length ?: 0),
                        spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    binding.rbDefaultMethod.text = spannableString
                    binding.rbDefaultMethod.isChecked = true

                    cardsList?.remove(defaultCardModel)
                }

                binding.rvOtherPayment.layoutManager = LinearLayoutManager(requireActivity())
                binding.rvOtherPayment.adapter =
                    PaymentCardAdapter(requireActivity(), cardsList, this)
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handlePaymentWithExistingCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("500") == true) {
                    showError(binding.root, status.data.message)
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.DATA, status.data)
                    bundle.putString("amount", arguments?.getString("amount"))
                    findNavController().navigate(
                        R.id.action_manualTopUpCardFragment_to_manualTopUpSuccessfulFragment,
                        bundle
                    )
                }
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    override fun invoke(check: Boolean?, position: Int?, model: CardListResponseModel?) {
        rowId = model?.rowId
        defaultCardModel = model
        this.position = position
        isDefaultDeleted = false
        binding.rgPayment.clearCheck()
    }
}


