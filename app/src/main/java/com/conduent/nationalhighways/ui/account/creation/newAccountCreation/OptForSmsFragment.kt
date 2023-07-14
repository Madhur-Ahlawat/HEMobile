package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentOptForSmsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.extn.makeLinks


class OptForSmsFragment : BaseFragment<FragmentOptForSmsBinding>(), View.OnClickListener {
    private lateinit var  navFlow:String // create account , forgot password
    private var oldCommunicationTextMessage = false
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOptForSmsBinding = FragmentOptForSmsBinding.inflate(inflater, container, false)

    override fun init() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId -> // checkedId is the RadioButton selected
            when(checkedId){
                R.id.radioButtonYes -> {
                    binding.checkBoxTerms.visibility = View.VISIBLE
                    binding.btnNext.disable()
                    binding.checkBoxTerms.isChecked = false
                    NewCreateAccountRequestModel.communicationTextMessage=true

                }
                R.id.radioButtonNo -> {
                    NewCreateAccountRequestModel.communicationTextMessage=false

                    binding.checkBoxTerms.visibility = View.GONE
                    binding.btnNext.enable()
                }
            }
        }


        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.btnNext.enable()
                NewCreateAccountRequestModel.termsCondition=true


            }else{
                NewCreateAccountRequestModel.termsCondition=false

                binding.btnNext.disable()
            }
        }
        binding.btnNext.setOnClickListener(this)


        binding.checkBoxTerms.makeLinks(Pair("terms and conditions", View.OnClickListener {
            var url:String=""
            url = if (NewCreateAccountRequestModel.prePay){
                "https://pay-dartford-crossing-charge.service.gov.uk/dart-charge-terms-conditions"

            }else{
                "https://pay-dartford-crossing-charge.service.gov.uk/payg-terms-condtions"

            }
            val bundle=Bundle()
            bundle.putString(Constants.TERMSCONDITIONURL,url)
            findNavController().navigate(R.id.action_optForSmsFragment_to_termsConditionFragment,bundle)

        }))
        when(navFlowCall){

            EDIT_ACCOUNT_TYPE,EDIT_SUMMARY -> {oldCommunicationTextMessage = NewCreateAccountRequestModel.communicationTextMessage
                if(oldCommunicationTextMessage){
                    binding.radioButtonYes.isChecked = true
                }else{
                    binding.radioButtonNo.isChecked = true
                }}

        }
    }

    override fun initCtrl() {


    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext ->{

                when(navFlowCall){

                    EDIT_SUMMARY -> {if(NewCreateAccountRequestModel.mobileNumber?.isNotEmpty() == true){
                        findNavController().popBackStack()
                    }else{
                        findNavController().navigate(R.id.action_optForSmsFragment_to_mobileVerificationFragment,bundle())
                    }}
                    EDIT_ACCOUNT_TYPE -> {findNavController().navigate(
                        R.id.action_optForSmsFragment_to_twoStepVerificationFragment,bundle())}
                    else -> {findNavController().navigate(
                        R.id.action_optForSmsFragment_to_twoStepVerificationFragment,bundle())}

                }
            }
        }
    }

    private fun bundle() : Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
        return bundle
    }

}