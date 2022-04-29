package com.heandroid.ui.account.creation.step2.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentBusinessPrepayAutoTopupBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import com.heandroid.utils.onTextChanged

class BusinessPrePayAutoTopUpFragment : BaseFragment<FragmentBusinessPrepayAutoTopupBinding>(),
    View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessPrepayAutoTopupBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.llRecommendParent.gone()
        binding.topUpAmount.text?.clear()
    }

    override fun initCtrl() {
        binding.apply {
            noOfVehicle.onTextChanged {
                enableContinueBtn()
            }
            noOfCrossings.onTextChanged {
                enableContinueBtn()
            }
            continueBusinessTopup.setOnClickListener(this@BusinessPrePayAutoTopUpFragment)
        }
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.continue_business_topup -> {
                binding.apply {
                    val topUpCalLength = topUpAmount.text.toString().length

                    if (topUpCalLength > 0) {
                        val bundle = Bundle()
                        bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,requestModel)
                        findNavController().navigate(R.id.action_businessPrePayAutoTopUpFragment_to_personalDetailsEntryFragment ,bundle)
                    } else {
                        calculateAndUpdateUI()
                    }
                }
            }
        }
    }

    private fun calculateAndUpdateUI() {
        binding.apply {
            llRecommendParent.visible()
            val noOfVehicle = noOfVehicle.text.toString().toInt()
            val noOfCrossing = noOfCrossings.text.toString().toInt()
            val myAccountAutoTopUp = noOfVehicle * noOfCrossing * 2
            val autoTopUpFalls = noOfVehicle * noOfCrossing

            val recommendCal = "$noOfVehicle vehicles * $noOfCrossing/month * Assume Car charge at £2.00 = £$myAccountAutoTopUp"
            topUpCalculate.text = recommendCal
            topUpAmount.setText(myAccountAutoTopUp.toString())
            topUpAmountFalls.setText(autoTopUpFalls.toString())

            val recommendAmount = "We recommend that you have a minimum top-up amount of £$myAccountAutoTopUp and a minimum balance threshold of £$autoTopUpFalls.\n" +
                    "Please note that you can change these amounts by manually entering the amounts you require."
            topUpCalculateRecommend.text = recommendAmount
        }
    }

    private fun enableContinueBtn() {
        binding.apply {
            val crossingLength = noOfCrossings.text.toString().length
            val vehicleLength = noOfVehicle.text.toString().length
            isEnable = crossingLength > 0 && vehicleLength > 0
        }
    }
}