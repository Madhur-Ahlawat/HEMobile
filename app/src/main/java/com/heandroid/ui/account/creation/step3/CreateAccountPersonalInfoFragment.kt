package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
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
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.BUSINESS_ACCOUNT
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.Constants.PAYG
import com.heandroid.utils.common.Constants.PERSONAL_ACCOUNT
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountPersonalInfoFragment : BaseFragment<FragmentCreateAccountPersonalInfoBinding>(),
    View.OnClickListener {

    private var model: CreateAccountRequestModel? = null
    private var isEditAccountType : Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPersonalInfoBinding.inflate(inflater, container, false)

    override fun init() {
        model = arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        model?.firstName = ""
        model?.lastName = ""
        model?.cellPhone = ""
        model?.eveningPhone = ""
        model?.enable = false
        binding.model = model
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 6)

        accountType()
        planType()
    }

    override fun initCtrl() {
        binding.btnAction.setOnClickListener(this)

        if (model?.accountType == PERSONAL_ACCOUNT) {
            binding.tieFullName.doAfterTextChanged {
                model?.enable = (it?.length ?: 0) > 2
                binding.model = model
            }
        }
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAction -> {

                if (model?.accountType == PERSONAL_ACCOUNT) {
                    onClickPersonalAccountValidation()
                } else {
                    onClickBusinessAccountValidation()
                }
            }
        }
    }

    private fun onClickPersonalAccountValidation() {

        if(model?.planType == PAYG){
            binding.apply {

                when {
                    TextUtils.isEmpty(tieFullName.text?.toString()) -> setError(
                        tieFullName,
                        "Please fill the first name"
                    )
                    tieFullName.text?.toString()?.length!! < 2 -> setError(
                        tieFullName,
                        "Please enter valid name"
                    )
                    TextUtils.isEmpty(tieLastName.text?.toString()) -> setError(
                        tieLastName,
                        "Please fill the last name"
                    )
                    tieLastName.text?.toString()?.length!! < 2 -> setError(
                        tieLastName,
                        "Please enter valid last name"
                    )
                    else -> {

                        binding.model?.firstName = binding.tieFullName.text.toString()
                        binding.model?.lastName = binding.tieLastName.text.toString()
                        binding.model?.cellPhone = binding.tieMobileNo.text.toString()


                        val bundle = Bundle()
                        bundle.putParcelable(CREATE_ACCOUNT_DATA, binding.model)
                        isEditAccountType?.let {
                            bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
                        }
                        findNavController().navigate(
                            R.id.action_personalDetailsEntryFragment_to_postcodeFragment,
                            bundle
                        )
                    }
                }
            }

        }else{
            binding.apply {

                when {
                    TextUtils.isEmpty(tieFullName.text?.toString()) -> setError(
                        tieFullName,
                        "Please fill the first name"
                    )
                    tieFullName.text?.toString()?.length!! < 2 -> setError(
                        tieFullName,
                        "Please enter valid name"
                    )
                    TextUtils.isEmpty(tieLastName.text?.toString()) -> setError(
                        tieLastName,
                        "Please fill the last name"
                    )
                    tieLastName.text?.toString()?.length!! < 2 -> setError(
                        tieLastName,
                        "Please enter valid last name"
                    )
                    TextUtils.isEmpty(tieMobileNo.text.toString()) -> setError(
                        tieMobileNo,
                        "Please enter the mobile number"
                    )
                    Utils.mobileNumber(tieMobileNo.text.toString()) == "Password not matched" -> setError(
                        tieMobileNo,
                        "Please enter valid mobile number (0-9, +)"
                    )
                    else -> {

                        binding.model?.firstName = binding.tieFullName.text.toString()
                        binding.model?.lastName = binding.tieLastName.text.toString()
                        binding.model?.cellPhone = binding.tieMobileNo.text.toString()


                        val bundle = Bundle()
                        bundle.putParcelable(CREATE_ACCOUNT_DATA, binding.model)
                        isEditAccountType?.let {
                            bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
                        }
                        findNavController().navigate(
                            R.id.action_personalDetailsEntryFragment_to_postcodeFragment,
                            bundle
                        )
                    }
                }
            }

        }


    }

    private fun onClickBusinessAccountValidation() {
        binding.apply {

            when {
                TextUtils.isEmpty(companyName.text?.toString()) -> setError(
                    companyName,
                    "Please fill the company name"
                )
                companyName.text?.toString()?.length!! < 2 -> setError(
                    companyName,
                    "Company name length must be greater than 1"
                )
                TextUtils.isEmpty(firstName.text?.toString()) -> setError(
                    firstName,
                    "Please fill the first name"
                )
                firstName.text?.toString()?.length!! < 2 -> setError(
                    firstName,
                    "Please enter valid name"
                )
                TextUtils.isEmpty(lastName.text?.toString()) -> setError(
                    lastName,
                    "Please fill the last name"
                )
                lastName.text?.toString()?.length!! < 2 -> setError(
                    lastName,
                    "Please enter valid last name"
                )
                TextUtils.isEmpty(businessMobNo.text?.toString()) -> setError(
                    businessMobNo,
                    "Please enter the mobile number"
                )
                Utils.mobileNumber(businessMobNo.text?.toString()) == "Password not matched" -> setError(
                    businessMobNo,
                    "Please enter valid mobile number (0-9, +)"
                )
                else -> {

                    model?.companyName = companyName.text.toString()
                    model?.firstName = firstName.text.toString()
                    model?.lastName = lastName.text.toString()
                    model?.cellPhone = businessMobNo.text.toString()
                    model?.fein = "12345678"

                    val bundle = Bundle()
                    bundle.putParcelable(CREATE_ACCOUNT_DATA, model)
                    isEditAccountType?.let {
                        bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
                    }
                    findNavController().navigate(
                        R.id.action_personalDetailsEntryFragment_to_postcodeFragment,
                        bundle
                    )
                }
            }
        }
    }

    private fun setError(textInputEditText: TextInputEditText, errorMsg: String) {
        textInputEditText.error = errorMsg
    }

    private fun accountType() {
        when (model?.accountType) {
            BUSINESS_ACCOUNT -> {
                val content = SpannableString(getString(R.string.underline_company_info))
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                binding.tvPersonaleInfo.text = content
                model?.planType = BUSINESS_ACCOUNT
                model?.enable = true
                binding.businessAccountParent.visible()
                binding.personalAccountParent.gone()
            }
            else -> {
                val content = SpannableString(getString(R.string.underline_personal_info))
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                binding.tvPersonaleInfo.text = content
                binding.personalAccountParent.visible()
                binding.businessAccountParent.gone()
            }
        }
    }

    private fun planType() {
        when (model?.planType) {
            PAYG -> {
                binding.tilMobileNo.gone()
                binding.tvLabel.text = getString(R.string.pay_as_you_go)
            }

            BUSINESS_ACCOUNT -> {
                binding.tvLabel.text = getString(R.string.business_prepay_account)
            }
            else -> {
                binding.tvLabel.text = getString(R.string.personal_pre_pay_account)
            }
        }
    }
}