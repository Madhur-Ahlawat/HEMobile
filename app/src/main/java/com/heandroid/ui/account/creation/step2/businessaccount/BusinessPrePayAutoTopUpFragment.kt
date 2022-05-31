package com.heandroid.ui.account.creation.step2.businessaccount

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentBusinessPrepayAutoTopupBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessPrePayAutoTopUpFragment : BaseFragment<FragmentBusinessPrepayAutoTopupBinding>(),
    View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessPrepayAutoTopupBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)

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

            binding.noOfVehicle.setText(requestModel?.mNoOfVehicles ?: "")
            binding.noOfCrossings.setText(requestModel?.mNoOfCrossings ?: "")
        }
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.continue_business_topup -> {

                val noOfCrossing = binding.noOfCrossings.text.toString()
                val noOfVehicle = binding.noOfVehicle.text.toString()
                val mCode =
                    arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT, 0)!!
                val bundle = Bundle()
                bundle.putString(Constants.NO_OF_CROSSING_BUSINESS, noOfCrossing)
                bundle.putString(Constants.NO_OF_VEHICLE_BUSINESS, noOfVehicle)
                bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT, mCode)
                requestModel?.mNoOfVehicles = noOfVehicle
                requestModel?.mNoOfCrossings = noOfCrossing
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                findNavController().navigate(
                    R.id.action_businessPrePayAutoTopUpFragment_to_businessTopUpRecommendationFragment,
                    bundle
                )
            }
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