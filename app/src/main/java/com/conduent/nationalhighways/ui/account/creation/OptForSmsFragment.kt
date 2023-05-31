package com.conduent.nationalhighways.ui.account.creation

import android.content.Intent
import android.net.Uri
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
import com.conduent.nationalhighways.utils.extn.makeLinks


class OptForSmsFragment : BaseFragment<FragmentOptForSmsBinding>(), View.OnClickListener {
    private lateinit var  navFlow:String // create account , forgot password
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
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)


        }))

    }

    override fun initCtrl() {


    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext ->{
                val bundle=Bundle()
                findNavController().navigate(R.id.action_optForSmsFragment_to_twoStepVerificationFragment,bundle)
            }
        }
    }

}