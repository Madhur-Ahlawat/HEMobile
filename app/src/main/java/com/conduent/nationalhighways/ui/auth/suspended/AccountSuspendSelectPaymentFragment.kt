package com.conduent.nationalhighways.ui.auth.suspended

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
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
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.PaymentSingletonClass
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountSuspendSelectPaymentFragment : BaseFragment<FragmentAccountSuspendHaltBinding>(),
    View.OnClickListener, SuspendPaymentMethodAdapter.paymentMethodSelectCallBack {
    private lateinit var suspendPaymentMethodAdapter: SuspendPaymentMethodAdapter
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var lowBalance: Boolean = true
    private var cardSelection: Boolean = false
    private val viewModel: PaymentMethodViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var position: Int = 0
    private var isViewCreated: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var currentBalance: String = ""
    private var navFlow: String = ""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltBinding =
        FragmentAccountSuspendHaltBinding.inflate(inflater, container, false)


    @SuppressLint("ClickableViewAccessibility")
    override fun initCtrl() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (!isViewCreated) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.saveCardList()
        }

        isViewCreated = false


        binding.btnContinue.setOnClickListener(this)
        binding.btnAddNewPaymentMethod.setOnClickListener(this)
        binding.btnAddNewPayment.setOnClickListener(this)
        binding.lowBalance.editText.addTextChangedListener(GenericTextWatcher())
        //  binding.lowBalance.editText.setOnFocusChangeListener { _, b -> topBalanceDecimal(b) }


        binding.lowBalance.editText.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                val bundle = Bundle()

                bundle.putString(Constants.LOW_BALANCE, Constants.TOP_UP_BALANCE)

                bundle.putString(
                    Constants.TOP_UP_AMOUNT,
                    binding.lowBalance.editText.text.toString()
                )

                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_amountKeyPadFragment,
                    bundle
                )
            }
            true
        }

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)

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
            SuspendPaymentMethodAdapter(requireContext(), paymentList, this)
        binding.rvPaymentMethods.adapter = suspendPaymentMethodAdapter

        binding.lowBalance.setText("£10.00")


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
            observe(viewModel.savedCardList, ::handleSaveCardResponse)

        }
    }

    override fun onResume() {
        if (PaymentSingletonClass.topUpBalance.isNotEmpty()) {
            val tBalance = PaymentSingletonClass.topUpBalance
            var fBalance: String = ""
            fBalance = if (tBalance.contains("$")) {
                tBalance.replace("$", "")
            } else {
                tBalance.replace("£", "").toString()
            }
            binding.lowBalance.editText.setText(
                (String.format(
                    "%.2f",
                    fBalance.toDouble()
                ))
            )
        }
        super.onResume()
    }
    inner class GenericTextWatcher() : TextWatcher {

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


            val text = binding.lowBalance.getText().toString().trim()
            var updatedText: String = ""
            updatedText = if (text.contains("$")) {
                text.replace("$", "")
            } else {
                text.replace("£", "")
            }



            if (updatedText.isNotEmpty()) {
                val str: String = updatedText.substringBeforeLast(".")
                lowBalance = if (str.length < 8) {
                    if (updatedText.toDouble() < 10) {
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
        binding.btnAddNewPaymentMethod.isEnabled = lowBalance


    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                val topUpAmount = binding.lowBalance.getText().toString().trim().replace("£", "")

                val bundle = Bundle()
                bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount.toDouble())
                bundle.putInt(Constants.POSITION, position)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)

                bundle.putParcelableArrayList(Constants.DATA, paymentList as ArrayList)
                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_accountSuspendedFinalPayFragment,
                    bundle
                )
            }

            R.id.btnAddNewPaymentMethod -> {
                val topUpAmount = binding.lowBalance.getText().toString().trim().replace("£", "")
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, topUpAmount.toDouble())
                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList?.size ?: 0)

                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_nmiPaymentFragment,
                    bundle
                )
            }

            R.id.btnAddNewPayment -> {
                val topUpAmount = binding.lowBalance.getText().toString().trim().replace("£", "")
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, topUpAmount.toDouble())
                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
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
                // paymentList?.clear()
                paymentList = status.data?.creditCardListType?.cardsList
                if (paymentList?.isNotEmpty() == true) {

                    suspendPaymentMethodAdapter.updateList(paymentList)
                    binding.rvPaymentMethods.visible()
                    binding.btnContinue.visible()

                    binding.noCardFoundLayout.gone()

                    binding.btnAddNewPayment.gone()
                    binding.btnAddNewPaymentMethod.visible()

                } else {
                    binding.btnAddNewPayment.visible()
                    binding.btnAddNewPaymentMethod.gone()
                    binding.noCardFoundLayout.visible()
                    binding.rvPaymentMethods.gone()
                    binding.btnContinue.gone()
                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    override fun paymentMethodCallback(position: Int) {
        this.position = position
        suspendPaymentMethodAdapter?.notifyDataSetChanged()
        cardSelection = paymentList?.get(position)?.isSelected == true
        checkButton()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (requireActivity() is HomeActivityMain){
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }




    }

}