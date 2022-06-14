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
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
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
            amount.text = "£ " + requestModel?.replenishmentAmount
        }
        if (requestModel?.accountType == Constants.BUSINESS_ACCOUNT) {
            binding.paymentLayout.visible()
            binding.prePayAmountLayout.gone()
        } else {
            binding.paymentLayout.gone()
        }

        if (requestModel?.accountType == Constants.PERSONAL_ACCOUNT
            && requestModel?.planType == Constants.PAYG) {
            binding.paymentLayout.gone()
            binding.prePayAmountLayout.gone()
        } else if (requestModel?.accountType == Constants.PERSONAL_ACCOUNT
            && requestModel?.planType == null){
            binding.paymentLayout.gone()
            binding.prePayAmountLayout.visible()
        }

    }

    override fun initCtrl() {
        binding.payButton.setOnClickListener(this@CreateAccountPaymentSummaryFragment)
        binding.lytEmailAddress.setOnClickListener(this@CreateAccountPaymentSummaryFragment)
        binding.lytAccountNo.setOnClickListener(this@CreateAccountPaymentSummaryFragment)
        binding.lytVrmRegisterNo.setOnClickListener(this@CreateAccountPaymentSummaryFragment)
        binding.lytPaymentAmount.setOnClickListener(this@CreateAccountPaymentSummaryFragment)
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
            R.id.lyt_payment_amount ->{
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT_KEY)
                findNavController().navigate(R.id.action_paymentSummaryScreen_to_business_prepay_autotopupfragment, bundle)
            }
            R.id.lyt_vrm_register_no -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                findNavController().navigate(R.id.action_paymentSummaryScreen_to_createAccountFindVehicleFragment, bundle)
            }

            R.id.lyt_email_address -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL_KEY)
                findNavController().navigate(R.id.action_paymentSummaryFragment_to_emailVerification, bundle)
            }

            R.id.lyt_account_no -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
                findNavController().navigate(R.id.action_paymentSummaryFragment_to_accountTypeSelectionFragment, bundle)
            }

        }
    }
}