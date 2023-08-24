package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.conduent.nationalhighways.databinding.FragmentMakeOneOffPaymentSuccessfullyBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils


class MakeOneOffPaymentSuccessfullyFragment :
    BaseFragment<FragmentMakeOneOffPaymentSuccessfullyBinding>(), View.OnClickListener {
    private var oneOfPaymentResponse: OneOfPaymentModelResponse?=null
    private var amount:String=""



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOneOffPaymentSuccessfullyBinding =
        FragmentMakeOneOffPaymentSuccessfullyBinding.inflate(inflater, container, false)

    override fun init() {
        binding.createAccount.setOnClickListener(this)
        binding.backToMainMenu.setOnClickListener(this)

    }

    override fun initCtrl() {
        if (arguments?.getParcelable<OneOfPaymentModelResponse>(Constants.ONE_OF_PAYMENTS_PAY_RESP)!=null){
            oneOfPaymentResponse=arguments?.getParcelable<OneOfPaymentModelResponse>(Constants.ONE_OF_PAYMENTS_PAY_RESP)
        }

        if (arguments?.getString(Constants.DATA)!=null){
            amount=arguments?.getString(Constants.DATA)?:""
        }


        binding.accountNumber.text=oneOfPaymentResponse?.referenceNumber
        binding.vechicleRegistration.text=NewCreateAccountRequestModel.plateNumber
        binding.amountPaid.text=amount
        binding.timeDate.text=Utils.currentTime()+" "+getString(R.string.str_on)+" "+Utils.currentDate()
        binding.emailConformationTxt.text=getString(R.string.str_we_have_sent_confirmation_email_and_confirmation_text_message,NewCreateAccountRequestModel.emailAddress,NewCreateAccountRequestModel.mobileNumber)


    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.createAccount -> {
                findNavController().navigate(R.id.action_make_one_off_payment_successfully_to_createAccountPrerequisite)
            }
            R.id.backToMainMenu->{
                findNavController().navigate(R.id.action_make_one_off_payment_successfully_to_landingFragment)
            }
        }
    }

}