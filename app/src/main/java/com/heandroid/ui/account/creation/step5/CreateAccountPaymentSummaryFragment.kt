package com.heandroid.ui.account.creation.step5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentCreateAccountPaymentSummaryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPaymentSummaryFragment: BaseFragment<FragmentCreateAccountPaymentSummaryBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateAccountPaymentSummaryBinding =
        FragmentCreateAccountPaymentSummaryBinding.inflate(inflater, container, false)

    private var requestModel: CreateAccountRequestModel? = null

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.apply {
            tvEmailAddress.text = requestModel?.emailAddress
            tvAccountType.text = requestModel?.accountType
            tvRegistrationNumber.text = requestModel?.vehicleNo
            amount.text = requestModel?.replenishmentAmount
        }
    }

    override fun initCtrl() {
        binding.payButton.setOnClickListener(this@CreateAccountPaymentSummaryFragment)
        binding.changeAmount.setOnClickListener(this@CreateAccountPaymentSummaryFragment)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.pay_button -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                findNavController().navigate(R.id.action_paymentSummaryScreen_to_choosePaymentScreen, bundle)
            }
            R.id.change_amount ->{
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT_KEY)
                findNavController().navigate(R.id.action_paymentSummaryScreen_to_business_prepay_autotopupfragment, bundle)

            }
        }
    }
}