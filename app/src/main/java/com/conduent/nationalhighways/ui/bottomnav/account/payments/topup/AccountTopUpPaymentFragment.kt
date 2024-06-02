package com.conduent.nationalhighways.ui.bottomnav.account.payments.topup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountGetThresholdResponse
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.conduent.nationalhighways.databinding.FragmentAccountTopupPaymentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountTopUpPaymentFragment : BaseFragment<FragmentAccountTopupPaymentBinding>(),
    View.OnClickListener {

    private val viewModel: AccountTopUpPaymentViewModel by viewModels()
    private var topUpAmount = 0.0f
    private var thresholdAmount = 0.0f

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAccountTopupPaymentBinding.inflate(inflater, container, false)

    override fun init() {
        getThresholdAmount()
        checkButton()
    }

    override fun initCtrl() {
        binding.updateBtn.setOnClickListener(this)
        binding.topUpFallsAmount.onTextChanged {
            checkButton()
        }
        binding.topUpMyAccount.onTextChanged {
            checkButton()
        }
    }

    override fun observer() {
        observe(viewModel.thresholdLiveData, ::getThresholdApiResponse)
        observe(viewModel.updateAmountLiveData, ::updateThresholdApiResponse)
    }

    private fun getThresholdAmount() {
        showLoaderDialog()
        viewModel.getThresholdAmount()
    }

    private fun getThresholdApiResponse(resource: Resource<AccountGetThresholdResponse?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                resource.data?.apply {
                    if (statusCode == "0") {
                        binding.apply {
                            if (thresholdAmountVo?.suggestedAmount?.isEmpty() == true)
                                customerAmount.text = "50.00"
                            else
                                customerAmount.text =
                                    "${thresholdAmountVo?.suggestedAmount}.00"

                            if (thresholdAmountVo?.suggestedThresholdAmount?.isEmpty() == true)
                                suggestedThresholdAmount.text = "15.00"
                            else
                                suggestedThresholdAmount.text =
                                    "${thresholdAmountVo?.suggestedThresholdAmount}.00"

                            topUpMyAccount.setText("${thresholdAmountVo?.customerAmount}.00")
                            topUpAmount = thresholdAmountVo?.customerAmount?.toFloat() ?: 0.0f
                            topUpFallsAmount.setText("${thresholdAmountVo?.thresholdAmount}.00")
                            thresholdAmount = thresholdAmountVo?.thresholdAmount?.toFloat() ?: 0.0f
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
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                resource.data?.apply {
                    if (statusCode == "0") {
                        requireActivity().showToast("data updated successfully")
                        viewModel.getThresholdAmount()
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

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.update_btn -> {
                binding.apply {
                    val autoTopUP: Double = topUpMyAccount.getText().toString().toDouble()
                    val thresholdAmount: Double = topUpFallsAmount.getText().toString().toDouble()

                    when {
                        autoTopUP < 10.0 -> {
                            ErrorUtil.showError(
                                binding.root,
                                resources.getString(R.string.customer_amount_err_msg)
                            )
                        }

                        thresholdAmount < 5.0 -> {
                            ErrorUtil.showError(
                                binding.root,
                                resources.getString(R.string.threshold_amount_err_msg)
                            )
                        }

                        else -> {
                            val request = AccountTopUpUpdateThresholdRequest(
                                thresholdAmount.toString(),
                                customerAmount.getText().toString()
                            )
                            showLoaderDialog()
                            viewModel.updateThresholdAmount(request)
                        }
                    }
                }
            }
        }
    }

    private fun checkButton() {
        val topUpValue = binding.topUpMyAccount.getText().toString().trim()
        val thresholdValue = binding.topUpFallsAmount.getText().toString().trim()
        val topUp = try {
            topUpValue.toFloat()
        } catch (ex: NumberFormatException) {
            0f
        }
        val threshold = try {
            thresholdValue.toFloat()
        } catch (ex: NumberFormatException) {
            0f
        }
        binding.model = topUpValue.isNotEmpty() && thresholdValue.isNotEmpty()
                && (topUp.compareTo(topUpAmount) != 0 || threshold.compareTo(thresholdAmount) != 0)
    }

}