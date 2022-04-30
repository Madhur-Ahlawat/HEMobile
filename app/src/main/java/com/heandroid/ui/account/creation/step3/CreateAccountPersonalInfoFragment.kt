package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentCreateAccountPersonalInfoBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants.BUSINESS_ACCOUNT
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.Constants.PAYG
import com.heandroid.utils.common.Constants.PERSONAL_ACCOUNT
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountPersonalInfoFragment : BaseFragment<FragmentCreateAccountPersonalInfoBinding>(), View.OnClickListener {

    private var model : CreateAccountRequestModel ? =null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPersonalInfoBinding.inflate(inflater,container,false)

    override fun init() {
        model=arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        model?.firstName=""
        model?.lastName=""
        model?.cellPhone=""
        model?.eveningPhone=""
        model?.enable=false
        binding.model=model
        binding.tvStep.text= getString(R.string.str_step_f_of_l,3,5)

        accountType()
        planType()
    }

    override fun initCtrl() {
        binding.btnAction.setOnClickListener(this)

        if(model?.accountType == PERSONAL_ACCOUNT){
            binding.tieFullName.doAfterTextChanged {
                model?.enable  = (it?.length?:0) > 2
                binding.model = model
            }
        }
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAction -> {

                if(model?.accountType == PERSONAL_ACCOUNT){
                    onClickPersonalAccountValidation()
                }else {
                    onClickBusinessAccountValidation()
                }
         }
        }
    }

    private fun onClickPersonalAccountValidation() {
        if(binding.tieFullName.text.toString().contains(" ")){
            binding.model?.firstName= binding.tieFullName.text.toString().split(" ")[0]
            binding.model?.lastName= binding.tieFullName.text.toString().split(" ")[1]
        }else {
            binding.model?.firstName = binding.tieFullName.text.toString()
            binding.model?.lastName = ""
        }
        val bundle = Bundle()
        bundle.putParcelable(CREATE_ACCOUNT_DATA,binding.model)
        findNavController().navigate(R.id.action_personalDetailsEntryFragment_to_postcodeFragment,bundle)
    }

    private fun onClickBusinessAccountValidation() {
        binding.apply {

            when {
                TextUtils.isEmpty(companyName.text?.toString()) -> setError(companyName, "Please fill the company name")
                companyName.text?.toString()?.length!! < 2 -> setError(companyName, "Company name length must be greater than 1")
                TextUtils.isEmpty(firstName.text?.toString()) -> setError(firstName, "Please fill the first name")
                firstName.text?.toString()?.length!! < 2 -> setError(firstName, "Please enter valid name")
                TextUtils.isEmpty(lastName.text?.toString()) -> setError(lastName, "Please fill the last name")
                lastName.text?.toString()?.length!! < 2 -> setError(lastName, "Please enter valid last name")
                TextUtils.isEmpty(businessMobNo.text?.toString()) -> setError(businessMobNo, "Please enter the mobile number")
                Utils.mobileNumber(businessMobNo.text?.toString()) == "Password not matched" -> setError(businessMobNo, "Please enter valid mobile number (0-9, +)")
                else -> {
                    val bundle = Bundle()
                    bundle.putParcelable(CREATE_ACCOUNT_DATA, binding.model)
                    findNavController().navigate(R.id.action_personalDetailsEntryFragment_to_postcodeFragment, bundle)
                }
            }
        }
        }



    private fun setError(textInputEditText: TextInputEditText, errorMsg: String){
        textInputEditText.error = errorMsg
    }

    private fun accountType() {
        when(model?.accountType){
            BUSINESS_ACCOUNT -> {
                binding.tvPersonaleInfo.text = "Company Info"
                model?.planType = BUSINESS_ACCOUNT
                model?.enable = true
            }
            else -> {
                binding.tvPersonaleInfo.text = "Personal Info"
            }
        }
    }

    private fun planType() {
        when(model?.planType) {
            PAYG ->{
                binding.tilMobileNo.gone()
                binding.tvLabel.text=getString(R.string.pay_as_you_go)  }

            BUSINESS_ACCOUNT -> {
                binding.tvLabel.text=getString(R.string.business_prepay_account)
            }
            else -> { binding.tvLabel.text=getString(R.string.personal_pre_pay_account) }
        }
    }
}