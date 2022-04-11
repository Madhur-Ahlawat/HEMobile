package com.heandroid.ui.bottomnav.account.payments.topup

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.heandroid.R
import com.heandroid.data.model.account.ThresholdAmountApiResponse
import com.heandroid.data.model.accountpayment.AccountTopUpUpdateThresholdRequest
import com.heandroid.data.model.accountpayment.AccountTopUpUpdateThresholdResponse
import com.heandroid.databinding.FragmentAccountTopupPaymentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountTopUpPaymentFragment : BaseFragment<FragmentAccountTopupPaymentBinding>(),
    View.OnClickListener {

    private val viewModel: AccountTopUpPaymentViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountTopupPaymentBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("create", "test")
    }

    override fun init() {
        Log.e("init", "test")
        getThresholdAmount()
    }

    override fun initCtrl() {
        binding.updateBtn.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.thresholdLiveData, ::getThresholdApiResponse)
        observe(viewModel.updateAmountLiveData, ::updateThresholdApiResponse)
    }

    private fun getThresholdAmount() {
        viewModel.getThresholdAmount()
    }

    private fun getThresholdApiResponse(resource: Resource<ThresholdAmountApiResponse?>?) {
        try {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.apply {
                        if (statusCode == "0") {
                            thresholdAmountVo.let {
                                binding.topUpMyAccount.setText(thresholdAmountVo.customerAmount)
                                binding.topUpFallsAmount.setText(thresholdAmountVo.thresholdAmount)
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
        } catch (e: Exception) {
        }
    }

    private fun updateThresholdApiResponse(resource: Resource<AccountTopUpUpdateThresholdResponse?>?) {
        try {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.apply {
                        if (statusCode == "0") {

                        }
                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {

                }
            }
        } catch (e: Exception) {

        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.update_btn -> {

                binding.apply {
                    if(topUpMyAccount.text?.isNotEmpty() == false) {
                        requireActivity().showToast("Please enter the amount in my account")
                    }
                    else if (TextUtils.isEmpty(topUpFallsAmount.text.toString())) {
                        requireActivity().showToast("Please enter the amount when top up falls")
                    }
                    else {
                        val customerAmount: Double = topUpMyAccount.text.toString().toDouble()
                        val thresholdAmount: Double = topUpFallsAmount.text.toString().toDouble()

                        when {
                            customerAmount < 5.0 -> {
                                topUpMyAccount.error = resources.getString(R.string.customer_amount_err_msg)
                            }
                            thresholdAmount < 10.0 -> {
                                topUpFallsAmount.error = resources.getString(R.string.threshold_amount_err_msg)
                            }
                            else -> {
                                val request = AccountTopUpUpdateThresholdRequest(thresholdAmount.toString(), customerAmount.toString())
                                viewModel.updateThresholdAmount(request)
                            }
                        }
                    }
                }
            }
        }
    }

}