package com.conduent.nationalhighways.ui.account.creation

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

class TwoStepVerificationFragment : BaseFragment<FragmentTwoStepVerificationBinding>(),
    View.OnClickListener {
    private lateinit var  navFlow:String // create account , forgot password
    private var requestModel: NewCreateAccountRequestModel?=null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTwoStepVerificationBinding.inflate(inflater, container, false)

    override fun init() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)

        when (binding.radioGroupYesNo.checkedRadioButtonId) {
            R.id.radioButtonYes -> {
                binding.btnNext.enable()
            }
            R.id.radioButtonNo -> {

                binding.btnNext.enable()
            }
        }
        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId -> // checkedId is the RadioButton selected
            when(checkedId){
                R.id.radioButtonYes -> {
                    requestModel?.twoStepVerification=true

                    binding.btnNext.enable()
                }
                R.id.radioButtonNo -> {
                    requestModel?.twoStepVerification=false

                    binding.btnNext.enable()
                }
            }
        }
        binding.btnNext.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext ->{
                val bundle= Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,requestModel)
                bundle.putString(Constants.NAV_FLOW_KEY,Constants.ACCOUNT_CREATION_MOBILE_FLOW)
                findNavController().navigate(R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,bundle)
            }
        }
    }

}