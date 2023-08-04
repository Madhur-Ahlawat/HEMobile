package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.annotation.SuppressLint
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
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountGetThresholdResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.conduent.nationalhighways.databinding.FragmentTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.topup.AccountTopUpPaymentViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.SHOW_BACK_BUTTON
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TopUpFragment : BaseFragment<FragmentTopUpBinding>(), View.OnClickListener {

    private var lowBalance: Boolean = false
    private var topUpBalance: Boolean = false
    private var navFlow: String = ""
    private var isViewCreated: Boolean = false
    private val viewModel: AccountTopUpPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null

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

        if (navFlow == Constants.THRESHOLD) {
            if (!isViewCreated) {
                getThresholdAmount()

            }

        }
        isViewCreated = true


        binding.topUpBtn.setOnClickListener(this)

        binding.lowBalance.editText.setOnClickListener(this)
        binding.lowBalance.editText.isFocusable = false
        binding.lowBalance.editText.isClickable = true

        binding.top.editText.setOnClickListener(this)
        binding.top.editText.isFocusable = false
        binding.top.editText.isClickable = true



        binding.top.setOnClickListener(this)

        binding.lowBalance.editText.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                val bundle = Bundle()

                bundle.putString(Constants.LOW_BALANCE, Constants.LOW_BALANCE)
                bundle.putString(Constants.TOP_UP_AMOUNT, binding.top.editText.text.toString())

                findNavController().navigate(
                    R.id.action_topUpFragment_to_amountKeyPadFragment,
                    bundle
                )
            }
            true
        }


        binding.top.editText.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                val bundle = Bundle()

                bundle.putString(Constants.LOW_BALANCE, Constants.TOP_UP_BALANCE)
                bundle.putString(
                    Constants.LOW_BALANCE_AMOUNT,
                    binding.lowBalance.editText.text.toString()
                )

                findNavController().navigate(
                    R.id.action_topUpFragment_to_amountKeyPadFragment,
                    bundle
                )
            }
            true
        }


        binding.lowBalance.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.top.editText.addTextChangedListener(GenericTextWatcher(1))

        /* binding.lowBalance.editText.setOnFocusChangeListener { _, b -> lowBalanceDecimal(b) }
         binding.top.editText.setOnFocusChangeListener { _, b -> topBalanceDecimal(b) }
*/
        setFragmentResultListener(Constants.LOW_BALANCE) { _, bundle ->


            if (bundle.getString(Constants.LOW_BALANCE) != null) {
                binding.lowBalance.editText.setText(bundle.getString(Constants.LOW_BALANCE))


            }
            if (bundle.getString(Constants.TOP_UP_BALANCE) != null) {

                binding.top.editText.setText(bundle.getString(Constants.TOP_UP_BALANCE))
            }


        }

        setFragmentResultListener(Constants.TOP_UP_BALANCE) { _, bundle ->

            if (bundle.getString(Constants.LOW_BALANCE) != null) {
                binding.lowBalance.editText.setText(bundle.getString(Constants.LOW_BALANCE))


            }


            if (bundle.getString(Constants.TOP_UP_BALANCE) != null) {
                binding.top.editText.setText(bundle.getString(Constants.TOP_UP_BALANCE))

            }
        }

    }

    private fun getThresholdAmount() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getThresholdAmount()
    }

    override fun observer() {
        lifecycleScope.launch() {
            observe(viewModel.thresholdLiveData, ::getThresholdApiResponse)
            observe(viewModel.updateAmountLiveData, ::updateThresholdApiResponse)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.topUpBtn -> {
                val bundle = Bundle()

                if (navFlow == Constants.THRESHOLD) {
                    val amount = binding.top.getText().toString().trim().replace("£", "")
                    val thresholdAmount =
                        binding.lowBalance.getText().toString().trim().replace("£", "")
                    val request = AccountTopUpUpdateThresholdRequest(
                        amount,
                        thresholdAmount
                    )
                    loader?.show(
                        requireActivity().supportFragmentManager,
                        Constants.LOADER_DIALOG
                    )
                    viewModel.updateThresholdAmount(request)

                } else {
                    val amount = binding.top.getText().toString().trim().replace("£", "")
                    val thresholdAmount =
                        binding.lowBalance.getText().toString().trim().replace("£", "")
                    bundle.putDouble(Constants.DATA, amount.toDouble())
                    bundle.putDouble(Constants.THRESHOLD_AMOUNT, thresholdAmount.toDouble())
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.NOTSUSPENDED)

                    findNavController().navigate(
                        R.id.action_topUpFragment_to_nmiPaymentFragment,
                        bundle
                    )
                }

            }

            R.id.lowBalance -> {
                val bundle = Bundle()

                bundle.putString(Constants.LOW_BALANCE, Constants.LOW_BALANCE)
                bundle.putString(Constants.TOP_UP_AMOUNT, binding.top.text.toString())

                findNavController().navigate(
                    R.id.action_topUpFragment_to_amountKeyPadFragment,
                    bundle
                )
            }

            R.id.top -> {
                val bundle = Bundle()

                bundle.putString(Constants.LOW_BALANCE, Constants.TOP_UP_BALANCE)
                bundle.putString(Constants.LOW_BALANCE_AMOUNT, binding.lowBalance.text.toString())


                findNavController().navigate(
                    R.id.action_topUpFragment_to_amountKeyPadFragment,
                    bundle
                )

            }
        }

    }

    private fun lowBalanceDecimal(b: Boolean) {
        if (b.not()) {
            val text = binding.lowBalance.getText().toString().trim()
            val updatedText = text.replace("£", "")
            if (updatedText.isNotEmpty() && updatedText.contains(".").not()) {
                binding.lowBalance.setText(String.format("%.2f", updatedText.toDouble()))
            }
        }
    }

    private fun topBalanceDecimal(b: Boolean) {
        if (b.not()) {
            val text = binding.top.getText().toString().trim()
            val updatedText = text.replace("£", "")
            if (updatedText.isNotEmpty() && updatedText.contains(".").not()) {
                binding.top.setText(String.format("%.2f", updatedText.toDouble()))
            }
        }
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
                var updatedText: String = ""
                updatedText = if (text.contains("$")) {
                    text.replace("$", "")
                } else {
                    text.replace("£", "")
                }

                if (updatedText.isNotEmpty()) {
                    val str: String = updatedText.substringBeforeLast(".")
                    lowBalance = if (str.length < 8) {
                        if (updatedText.toDouble() < 5) {
                            binding.lowBalance.setErrorText(getString(R.string.str_low_balance_must_be_more))
                            false

                        } else {
                            binding.lowBalance.removeError()
                            true
                        }
                    } else {
                        binding.lowBalance.setErrorText(getString(R.string.str_low_balance_must_be_8_characters))
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
            } else if (index == 1) {
                val text = binding.top.getText().toString().trim()
                var updatedText: String = ""
                updatedText = if (text.contains("$")) {
                    text.replace("$", "")
                } else {
                    text.replace("£", "")
                }
                if (updatedText.trim()[0].equals(".")) {
                    updatedText.replace(".", "")
                }
                if (updatedText.isNotEmpty()) {
                    val str: String = updatedText.substringBeforeLast(".")
                    topUpBalance = if (str.length < 8) {
                        if (updatedText.toDouble() < 10) {
                            binding.top.setErrorText(getString(R.string.str_top_up_amount_must_be_more))
                            false

                        } else {
                            binding.top.removeError()
                            true
                        }
                    } else {
                        binding.top.setErrorText(getString(R.string.str_top_up_amount_must_be_8_characters))
                        false
                    }
                } else {
                    binding.top.removeError()
                }
                binding.top.editText.removeTextChangedListener(this)
                if (updatedText.isNotEmpty())
                    binding.top.setText("£$updatedText")
                Selection.setSelection(
                    binding.top.getText(),
                    binding.top.getText().toString().length
                )
                binding.top.editText.addTextChangedListener(this)
            }

            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

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
                            if (thresholdAmountVo?.customerAmount?.isNotEmpty() == true) {
                                binding.lowBalance.editText.setText(thresholdAmountVo.customerAmount)
                            }

                            if (thresholdAmountVo?.thresholdAmount?.isNotEmpty() == true) {
                                binding.top.editText.setText(thresholdAmountVo.thresholdAmount)

                            }

                        }
                    }
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
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
                resource.data?.apply {
                    if (statusCode == "0") {
                        val bundle = Bundle()
                        val amount = binding.top.getText().toString().trim().replace("£", "")
                        val thresholdAmount =
                            binding.lowBalance.getText().toString().trim().replace("£", "")
                        bundle.putDouble(Constants.TOP_UP_AMOUNT, amount.toDouble())
                        bundle.putDouble(Constants.THRESHOLD_AMOUNT, thresholdAmount.toDouble())
                        bundle.putBoolean(SHOW_BACK_BUTTON, false)
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlow)

                        findNavController().navigate(
                            R.id.action_topUpFragment_to_deletePaymentMethodSuccessFragment,
                            bundle
                        )
                    }
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }
    }


}