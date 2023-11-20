package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.conduent.nationalhighways.databinding.FragmentMakeOneOffPaymentSuccessfullyBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.currentTime
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import javax.inject.Inject


class MakeOneOffPaymentSuccessfullyFragment :
    BaseFragment<FragmentMakeOneOffPaymentSuccessfullyBinding>(), View.OnClickListener {
    private var oneOfPaymentResponse: OneOfPaymentModelResponse? = null
    private var amount: String = ""

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOneOffPaymentSuccessfullyBinding =
        FragmentMakeOneOffPaymentSuccessfullyBinding.inflate(inflater, container, false)

    override fun init() {
        Utils.validationsToShowRatingDialog(requireActivity(),sessionManager)
        binding.createAccount.setOnClickListener(this)
        binding.backToMainMenu.setOnClickListener(this)
        binding.feedbackBt.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun initCtrl() {
        if (arguments?.getParcelable<OneOfPaymentModelResponse>(Constants.ONE_OF_PAYMENTS_PAY_RESP) != null) {
            oneOfPaymentResponse =
                arguments?.getParcelable<OneOfPaymentModelResponse>(Constants.ONE_OF_PAYMENTS_PAY_RESP)
        }

        if (arguments?.getString(Constants.DATA) != null) {
            amount = arguments?.getString(Constants.DATA) ?: ""
        }

        binding.accountNumber.text = oneOfPaymentResponse?.referenceNumber
        binding.vechicleRegistration.text = NewCreateAccountRequestModel.plateNumber
        binding.amountPaid.text = amount
        binding.timeDate.text =
            currentTime() + " " + getString(R.string.str_on) + " " + Utils.currentDate()
        if (NewCreateAccountRequestModel.emailAddress?.isNotEmpty() == true && NewCreateAccountRequestModel.mobileNumber?.isEmpty() == true) {
            binding.emailConformationTxt.text = getString(
                R.string.str_we_have_sent_confirmation_email,
                NewCreateAccountRequestModel.emailAddress
            )

        } else if (NewCreateAccountRequestModel.emailAddress?.isEmpty() == true && NewCreateAccountRequestModel.mobileNumber?.isNotEmpty() == true) {
            binding.emailConformationTxt.text = getString(
                R.string.str_we_have_sent_confirmation_text_message,
                NewCreateAccountRequestModel.mobileNumber
            )

        } else {
            binding.emailConformationTxt.text = getString(
                R.string.str_we_have_sent_confirmation_email_and_confirmation_text_message,
                NewCreateAccountRequestModel.emailAddress,
                NewCreateAccountRequestModel.mobileNumber
            )

        }


    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.createAccount -> {
                var plateNumber = ""
                if (NewCreateAccountRequestModel.vehicleList.size > 0) {
                    plateNumber = NewCreateAccountRequestModel.vehicleList.get(0).plateNumber ?: ""
                }

                NewCreateAccountRequestModel.vehicleList = ArrayList()
                NewCreateAccountRequestModel.onOffVehiclePlateNumber = plateNumber

                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java) {
                    putString(Constants.SHOW_SCREEN, Constants.LANDING_SCREEN)
                    putString(Constants.NAV_FLOW_FROM, Constants.ONE_OFF_PAYMENT_SUCCESS)
                    putString(Constants.PLATE_NUMBER, plateNumber)
                    putString(Constants.EMAIL, NewCreateAccountRequestModel.emailAddress)
                    putString(Constants.MOBILE_NUMBER, NewCreateAccountRequestModel.mobileNumber)
                    putString(Constants.COUNTRY_TYPE, NewCreateAccountRequestModel.countryCode)
                }
            }

            R.id.backToMainMenu -> {
                NewCreateAccountRequestModel.emailAddress = ""
                NewCreateAccountRequestModel.mobileNumber = ""
                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java) {
                    putString(Constants.SHOW_SCREEN, Constants.LANDING_SCREEN)
                }
            }
        }
    }

}