package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentTwoStepVerificationBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY

class TwoStepVerificationFragment : BaseFragment<FragmentTwoStepVerificationBinding>(),
    View.OnClickListener {
    private lateinit var  navFlow:String // create account , forgot password
    private var oldtwoStepVerification = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTwoStepVerificationBinding.inflate(inflater, container, false)

    override fun init() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()

        binding.twoFactor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                NewCreateAccountRequestModel.twoStepVerification=true
                binding.btnNext.enable()
            }else{
                NewCreateAccountRequestModel.twoStepVerification=false
                binding.btnNext.enable()
            }

        }

        binding.btnNext.setOnClickListener(this)

        when(navFlowCall){

            EDIT_ACCOUNT_TYPE,EDIT_SUMMARY -> { oldtwoStepVerification = NewCreateAccountRequestModel.twoStepVerification
                binding.twoFactor.isChecked = oldtwoStepVerification
            }

        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext ->{
                val bundle= Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                when(navFlowCall){

                    EDIT_SUMMARY -> {findNavController().popBackStack()}
                    EDIT_ACCOUNT_TYPE -> { if(NewCreateAccountRequestModel.mobileNumber?.isNotEmpty() == true){
                        bundle.putString(Constants.PLATE_NUMBER, "")
                        bundle.putInt(Constants.VEHICLE_INDEX, 0)
                        findNavController().navigate(R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,bundle)
                    }else{
                        findNavController().navigate(R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,bundle)
                    }}
                    else -> {findNavController().navigate(R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,bundle)}

                }


            }
        }
    }

}