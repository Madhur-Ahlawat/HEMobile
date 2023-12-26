package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentSmsNotSupportBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class SmsNotSupportFragment : BaseFragment<FragmentSmsNotSupportBinding>(), View.OnClickListener {

    private var mobileNumber: String = ""
    private var countryCode:String=""

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
                NewCreateAccountRequestModel.isCountryNotSupportForSms = true
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)

                findNavController().navigate(
                    R.id.action_smsNotSupportFragment_to_createVehicleFragment,
                    bundle
                )
            }

        }

    }

}