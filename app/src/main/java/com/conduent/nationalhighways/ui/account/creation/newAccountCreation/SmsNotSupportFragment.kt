package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentSmsNotSupportBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class SmsNotSupportFragment : BaseFragment<FragmentSmsNotSupportBinding>(), View.OnClickListener {

    private var mobileNumber: String = ""
    private var countryCode: String = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSmsNotSupportBinding.inflate(inflater, container, false)


    override fun init() {
        binding.btnChangeCountryCode.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            binding.btnChangeCountryCode.id -> {
                findNavController().popBackStack()

            }

            binding.btnNext.id -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                NewCreateAccountRequestModel.isCountryNotSupportForSms = true



                var res = 0
                res = if (navFlowCall == Constants.PAY_FOR_CROSSINGS) {
                    bundle.putParcelable(
                        Constants.NAV_DATA_KEY,
                        (navData as CrossingDetailsModelsResponse) as Parcelable?
                    )

                    bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

                    R.id.action_smsNotSupportFragment_to_crossingCheckAnswersFragment
                } else {
                    R.id.action_smsNotSupportFragment_to_createVehicleFragment
                }


                findNavController().navigate(res, bundle)
            }

        }

    }

}