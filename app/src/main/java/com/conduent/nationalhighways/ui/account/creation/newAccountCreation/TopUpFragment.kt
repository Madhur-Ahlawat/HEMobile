package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountGetThresholdResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.conduent.nationalhighways.databinding.FragmentTopUpBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.payments.topup.AccountTopUpPaymentViewModel
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
    private var paymentListSize: Int = 0
    private var apiLowBalanceAmount: String = "5"
    private var apiTopUpAmountBalance: String = "10"
    private var gtwLowBalance: GenericTextWatcher? = null
    private var gtwTopBalance: GenericTextWatcher? = null
    private var isClick = false


    val formatter = DecimalFormat("#,###.00")
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTopUpBinding = FragmentTopUpBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initCtrl() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""
        paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0

        if (navFlow == Constants.THRESHOLD) {
            if (!isViewCreated) {
                getThresholdAmount()
            }
        } else {
            binding.minimumAMountTopUp.text = (getString(
                R.string.str_minimum_amount,
                this@TopUpFragment.apiTopUpAmountBalance.toDouble().toInt().toString()
            ))
            binding.minimumAmount.text = (getString(
                R.string.str_minimum_amount,
                this@TopUpFragment.apiLowBalanceAmount.toDouble().toInt().toString()
            ))
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

    inner class GenericTextWatcher(private var isTopUp: Boolean) : TextWatcher {

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
                if (binding.top.getText().toString().trim().isNullOrEmpty()) {
                    binding.top.setErrorText(getString(R.string.enter_a_top_up_amount))
                } else {
                    binding.top.removeError()
                    topUpBalance =
                        Utils.validateAmount(
                            binding.top,
                            formatter.format(apiTopUpAmountBalance.toDouble()).toDouble(),
                            true
                        )
                }

            } else {
                if (binding.lowBalance.getText().toString().trim().isNullOrEmpty()) {
                    binding.lowBalance.setErrorText(getString(R.string.enter_a_top_up_amount))
                } else {
                    binding.lowBalance.removeError()
                    lowBalance =
                        Utils.validateAmount(
                            binding.lowBalance,
                            formatter.format(apiLowBalanceAmount.toDouble()).toDouble(),
                            false
                        )
                }
            }
            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun getThresholdAmount() {
        showLoaderDialog()
        viewModel.getThresholdAmount()
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.thresholdLiveData, ::getThresholdApiResponse)
            observe(viewModel.updateAmountLiveData, ::updateThresholdApiResponse)


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
                    val topUpAmount = binding.top.editText.text.toString().trim().replace("$", "£")
                        .replace("£", "").replace(",", "").replace(" ", "")
                    val thresholdAmount =
                        binding.lowBalance.editText.text.toString().trim().replace("$", "£")
                            .replace("£", "").replace(",", "").replace(" ", "")

                    val request = AccountTopUpUpdateThresholdRequest(
                        thresholdAmount,
                        topUpAmount
                    )

                    viewModel.updateThresholdAmount(request)
                    binding.topUpBtn.isEnabled = false
                    isClick = true

                } else {
                    emailHeartBeatApi()
                    smsHeartBeatApi()

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


        }

    }


    private fun topBalanceDecimal(b: Boolean) {
        if (!b) {
            var mText = binding.top.getText().toString().trim()
            if (mText.isEmpty()) {
                mText = "0.0"
            }

            mText = mText.replace("$", "").replace("£", "").replace("£.", "").replace(",", "")
                .replace(" ", "")
            if (mText.length == 1 && mText == ".") {
                mText = "0.0"
            }
            var forMatedAmount = formatter.format(mText.toDouble())
            if (!forMatedAmount.isNullOrEmpty() && forMatedAmount.equals(".00")) {
                forMatedAmount = "0.00"
            }
            binding.top.setText("£$forMatedAmount")
        }
    }

    private fun lowBalanceDecimal(b: Boolean) {
        if (!b) {
            var mText = binding.lowBalance.getText().toString().trim()
            if (mText.isEmpty()) {
                mText = "0.0"
            }

            mText = mText.replace("$", "").replace("£", "").replace("£.", "").replace(",", "")
                .replace(" ", "")
            if (mText.length == 1 && mText == ".") {
                mText = "0.0"
            }
            var forMatedAmount = formatter.format(mText.toDouble())
            if (!forMatedAmount.isNullOrEmpty() && forMatedAmount.equals(".00")) {
                forMatedAmount = "0.00"
            }
            binding.lowBalance.setText("£$forMatedAmount")
        }
    }

    private fun checkButton() {
        binding.topUpBtn.isEnabled = lowBalance && topUpBalance
    }

    private fun getThresholdApiResponse(resource: Resource<AccountGetThresholdResponse?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                resource.data?.apply {
                    if (statusCode == "0") {
                        binding.apply {
                            lowBalance.setText(thresholdAmountVo?.thresholdAmount.toString())

                            if (thresholdAmountVo?.customerAmount?.isNotEmpty() == true) {
                                top.setText(thresholdAmountVo.customerAmount.toString())
                            }
                            minimumAMountTopUp.text = (getString(
                                R.string.str_minimum_amount,
                                this@TopUpFragment.apiTopUpAmountBalance.toDouble().toInt()
                                    .toString()
                            ))
                            minimumAmount.text = (getString(
                                R.string.str_minimum_amount,
                                this@TopUpFragment.apiLowBalanceAmount.toDouble().toInt().toString()
                            ))
                        }
                    }
                    topBalanceDecimal(false)
                    lowBalanceDecimal(false)
                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
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
        dismissLoaderDialog()
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
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
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