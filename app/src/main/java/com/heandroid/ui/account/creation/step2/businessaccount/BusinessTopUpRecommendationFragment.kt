package com.heandroid.ui.account.creation.step2.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentBusinessTopUpRecommendationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessTopUpRecommendationFragment :
    BaseFragment<FragmentBusinessTopUpRecommendationBinding>(), View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null
    private var noOfCrossings: String? = null
    private var noOfVehicle: String? = null
    private var isEditAccountType : Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessTopUpRecommendationBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        noOfVehicle = arguments?.getString(Constants.NO_OF_VEHICLE_BUSINESS)
        noOfCrossings = arguments?.getString(Constants.NO_OF_CROSSING_BUSINESS)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        binding.isEnable = true
        calculateAndUpdateUI()
    }

    override fun initCtrl() {
        binding.apply {
            topUpAmount.onTextChanged {
                enableContinueBtn()
            }
            topUpAmountFalls.onTextChanged {
                enableContinueBtn()
            }
            binding.continueBusinessTopup.setOnClickListener(this@BusinessTopUpRecommendationFragment)
        }
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.continue_business_topup -> {
                requestModel?.thresholdAmount = binding.topUpAmountFalls.text.toString()
                requestModel?.replenishmentAmount = binding.topUpAmount.text.toString()
                requestModel?.transactionAmount = binding.topUpAmount.text.toString()
                val mCode =
                    arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT, 0)
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                isEditAccountType?.let {
                    bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
                }

                if (mCode == Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT_KEY) {
                    findNavController().navigate(
                        R.id.action_businessPrePayAutoTopUpFragment_to_paymentSummaryScreen,
                        bundle
                    )

                } else {
                    findNavController().navigate(
                        R.id.action_businessPrePayAutoTopUpFragment_to_personalDetailsEntryFragment,
                        bundle
                    )

                }
            }
        }
    }

    private fun calculateAndUpdateUI() {
        binding.apply {
            val noOfVehicle = noOfVehicle!!.toInt()
            val noOfCrossing = noOfCrossings!!.toInt()
            val myAccountAutoTopUp = noOfVehicle * noOfCrossing * 2
            val autoTopUpFalls = noOfVehicle * noOfCrossing

            val recommendCal =
                "$noOfVehicle vehicles * $noOfCrossing/month * Assume Car charge at £2.00 = £$myAccountAutoTopUp"
            topUpCalculate.text = recommendCal
            topUpAmount.setText(myAccountAutoTopUp.toString())
            topUpAmountFalls.setText(autoTopUpFalls.toString())

            val recommendAmount =
                "We recommend that you have a minimum top-up amount of £$myAccountAutoTopUp and a minimum balance threshold of £$autoTopUpFalls.\n" +
                        "Please note that you can change these amounts by manually entering the amounts you require."
            topUpCalculateRecommend.text = recommendAmount
        }
    }

    private fun enableContinueBtn() {
        binding.apply {
            val topUpAmountLength = topUpAmount.text.toString().length
            val topUpAmountFallsLength = topUpAmountFalls.text.toString().length
            isEnable = topUpAmountLength > 0 && topUpAmountFallsLength > 0
        }
    }
}