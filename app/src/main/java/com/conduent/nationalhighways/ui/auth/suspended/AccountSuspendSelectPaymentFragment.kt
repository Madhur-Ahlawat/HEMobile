package com.conduent.nationalhighways.ui.auth.suspended

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltBinding
import com.conduent.nationalhighways.ui.auth.adapter.SuspendPaymentMethodAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat


@AndroidEntryPoint
class AccountSuspendSelectPaymentFragment : BaseFragment<FragmentAccountSuspendHaltBinding>(),
    View.OnClickListener, SuspendPaymentMethodAdapter.PaymentMethodSelectCallBack {
    private var edtLength: Int? = 0
    private var cursorPosition: Int? = 0
    private lateinit var suspendPaymentMethodAdapter: SuspendPaymentMethodAdapter
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var real_paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var lowBalance: Boolean = true
    private var cardSelection: Boolean = false
    private val viewModel: PaymentMethodViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var position: Int = 0
    private var isViewCreated: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var currentBalance: String = ""
    private var navFlow: String = ""
    private val formatter = DecimalFormat("#,###.00")
    private var paymentListSize: Int = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltBinding =
        FragmentAccountSuspendHaltBinding.inflate(inflater, container, false)


    @SuppressLint("ClickableViewAccessibility")
    override fun initCtrl() {
        paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (!isViewCreated) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.saveCardListState()
        }

        isViewCreated = false


        binding.btnContinue.setOnClickListener(this)
        binding.btnAddNewPayment.setOnClickListener(this)
        binding.topBalance.editText.setOnFocusChangeListener { _, b -> topBalanceDecimal(b) }

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)

        }
        if (arguments?.getString(Constants.NAV_FLOW_KEY) != null) {
            navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""

    }

    override fun init() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvPaymentMethods.layoutManager = linearLayoutManager
        suspendPaymentMethodAdapter =
            SuspendPaymentMethodAdapter(requireActivity(), paymentList, this, navFlow)
        binding.rvPaymentMethods.adapter = suspendPaymentMethodAdapter
        binding.topBalance.setText("£10.00")
        binding.topBalance.editText.addTextChangedListener(GenericTextWatcher())
        cursorPosition = binding.topBalance.editText.selectionStart
        edtLength = binding.topBalance.editText.text?.length
        Selection.setSelection(binding.topBalance.editText.text, edtLength!! - 1)
        if (navFlowCall.equals(Constants.PAYMENT_TOP_UP)) {
            HomeActivityMain.setTitle("Top Up New Payment Method")
        }
    }

    private fun topBalanceDecimal(b: Boolean) {
        if (!b) {
            var mText = binding.topBalance.getText().toString().trim()
            if(mText.isEmpty()){
                mText= "0"
            }
            var updatedText: Int =0

            mText = mText.replace("$", "").replace("£", "").replace("£.", "").replace(",", "").replace(" ", "")
                .replace(" ", "")
            if(mText.length==1 && mText.equals(".")){
                mText="0"
            }
            updatedText=mText.toDouble().toInt()
            binding.topBalance.setText("£" + formatter.format(updatedText))
        }

    }


    override fun observer() {
        lifecycleScope.launch {
            viewModel.savedCardState.collect {
                handleSaveCardResponse(it)
            }
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
            lowBalance = Utils.validateAmount(binding.topBalance, 10, true)
            checkButton()
            checkNewPaymentMethodButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {
        binding.btnContinue.isEnabled = lowBalance && cardSelection

    }

    private fun checkNewPaymentMethodButton() {
        binding.btnAddNewPayment.isEnabled = lowBalance
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                topBalanceDecimal(false)
                val topUpAmount = binding.topBalance.getText().toString().trim().replace("£", "").replace("£.", "")
                    .replace("$", "").replace(",", "").replace(" ", "").toDouble().toInt()
                val bundle = Bundle()
                bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount.toDouble())
                bundle.putInt(Constants.POSITION, position)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                bundle.putParcelableArrayList(Constants.DATA, paymentList as ArrayList)
                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_accountSuspendedFinalPayFragment,
                    bundle
                )
            }

            R.id.btnAddNewPayment -> {
                val topUpAmount =
                    binding.topBalance.getText().toString().trim().replace("£", "")
                        .replace(".00", "")
                        .replace("$", "").replace(",", "")
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, topUpAmount.toDouble())
                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList?.size ?: 0)

                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_nmiPaymentFragment,
                    bundle
                )
            }
        }
    }

    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                paymentList?.clear()
                real_paymentList?.clear()
                real_paymentList = status.data?.creditCardListType?.cardsList
                for (i in 0 until status.data?.creditCardListType?.cardsList.orEmpty().size) {
                    if (status.data?.creditCardListType?.cardsList?.get(i)?.bankAccount == false) {
                        paymentList?.add(status.data.creditCardListType.cardsList?.get(i))
                    }
                }
                for (i in 0 until paymentList.orEmpty().size) {
                    checkNullValuesOfModel(paymentList?.get(i))
                }

                if (paymentList.orEmpty().size == 1) {
                    paymentList?.get(0)?.primaryCard = true
                }
                if (paymentList?.isNotEmpty() == true) {

                    for (i in 0 until (paymentList?.size ?: 0)) {
                        if (paymentList?.get(i)?.primaryCard == true && paymentList?.get(i)?.bankAccount == false) {
                            position = i
                            cardSelection = true
                            checkButton()
                            break
                        }

                    }



                    suspendPaymentMethodAdapter.updateList(paymentList, navFlow)
                    binding.rvPaymentMethods.visible()
                    binding.btnContinue.visible()

                    binding.noCardFoundLayout.gone()

                    binding.btnAddNewPayment.gone()

                } else {
                    binding.btnAddNewPayment.visible()
                    binding.noCardFoundLayout.visible()
                    binding.rvPaymentMethods.gone()
                    binding.btnContinue.gone()
                }

                lifecycleScope.launch {
                    viewModel._savedCardListState.emit(null)
                }


            }

            is Resource.DataError -> {
                if (status.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    private fun checkNullValuesOfModel(model: CardListResponseModel?) {
        if (model?.check == null) {
            model?.check = false
        }
        if (model?.isSelected == null) {
            model?.isSelected = false
        }
        if (model?.bankRoutingNumber == null) {
            model?.bankRoutingNumber = ""
        }
        if (model?.cardType == null) {
            model?.cardType = ""
        }
        if (model?.cardNumber == null) {
            model?.cardNumber = ""
        }
        if (model?.middleName == null) {
            model?.middleName = ""
        }
        if (model?.expMonth == null) {
            model?.expMonth = ""
        }
        if (model?.expYear == null) {
            model?.expYear = ""
        }
        if (model?.bankAccountNumber == null) {
            model?.bankAccountNumber = ""
        }
        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }

        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }
        if (model?.rowId == null) {
            model?.rowId = ""
        }
        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }
        if (model?.bankAccountNumber == null) {
            model?.bankAccountNumber = ""
        }
        if (model?.firstName == null) {
            model?.firstName = ""
        }
        if (model?.lastName == null) {
            model?.lastName = ""
        }
        if (model?.customerVaultId == null) {
            model?.customerVaultId = ""
        }
        if (model?.addressLine1 == null) {
            model?.addressLine1 = ""
        }
        if (model?.city == null) {
            model?.city = ""
        }
        if (model?.state == null) {
            model?.state = ""
        }
        if (model?.zipCode == null) {
            model?.zipCode = ""
        }
        if (model?.country == null) {
            model?.country = ""
        }
        if (model?.emandateStatus == null) {
            model?.emandateStatus = ""
        }
        if (model?.paymentSeqNumber == null) {
            model?.paymentSeqNumber = 0
        }
        if (model?.bankAccount == null) {
            model?.bankAccount = false
        }
    }

    override fun paymentMethodCallback(position: Int) {
        this.position = position
        for (i in 0 until (paymentList?.size ?: 0)) {
            paymentList?.get(i)?.isSelected = false
            paymentList?.get(i)?.primaryCard = false
        }
        paymentList?.get(position)?.primaryCard = true

        paymentList?.get(position)?.isSelected = true
        suspendPaymentMethodAdapter.updateList(paymentList, navFlow)
        cardSelection = paymentList?.get(position)?.isSelected == true
        checkButton()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }


    }

}