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
    private lateinit var requestModel:NewCreateAccountRequestModel
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOptForSmsBinding = FragmentOptForSmsBinding.inflate(inflater, container, false)

    override fun init() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()
        requestModel= NewCreateAccountRequestModel("",false,false,false)
        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId -> // checkedId is the RadioButton selected
            when(checkedId){
                R.id.radioButtonYes -> {
                    binding.checkBoxTerms.visibility = View.VISIBLE
                    binding.btnNext.disable()
                    binding.checkBoxTerms.isChecked = false
                    requestModel.communicationTextMessage=true

                }
                R.id.radioButtonNo -> {
                    requestModel.communicationTextMessage=false

                    binding.checkBoxTerms.visibility = View.GONE
                    binding.btnNext.enable()
                }
            }
        }


        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.btnNext.enable()
                requestModel.termsCondition=true


            }else{
                requestModel.termsCondition=false

                binding.btnNext.disable()
            }
        }
        binding.btnNext.setOnClickListener(this)


        binding.checkBoxTerms.makeLinks(Pair("terms and conditions", View.OnClickListener {
            val url =
                "https://pay-dartford-crossing-charge.service.gov.uk/dart-charge-terms-conditions"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)

            /*requireActivity().openActivityWithDataBack(ContactDartChargeActivity::class.java) {
                putInt(
                    Constants.FROM_LOGIN_TO_CASES,
                    Constants.FROM_ANSWER_TO_CASE_VALUE
                )
            }*/
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
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,requestModel)
                findNavController().navigate(R.id.action_optForSmsFragment_to_twoStepVerificationFragment,bundle)
            }
        }
    }

}