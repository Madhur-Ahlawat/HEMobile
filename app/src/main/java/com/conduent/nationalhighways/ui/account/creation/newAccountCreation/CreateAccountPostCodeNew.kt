package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPostCodeNewBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPostCodeNew : BaseFragment<FragmentCreateAccountPostCodeNewBinding>(),
    View.OnClickListener, OnRetryClickListener {
    private var requiredPostCode = false
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPostCodeNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputPostCode.editText
            .addTextChangedListener(GenericTextWatcher(binding.inputPostCode.editText))
        binding.btnFindAddress.setOnClickListener(this)
        binding.btnEnterAddressManually.setOnClickListener(this)



        binding.inputPostCode.setMaxLength(10)
        when(navFlowCall){

            EDIT_ACCOUNT_TYPE,EDIT_SUMMARY -> { binding.inputPostCode.setText(NewCreateAccountRequestModel.zipCode)}
            PROFILE_MANAGEMENT -> {
                val title: TextView? = requireActivity().findViewById(R.id.title_txt)
                title?.text = getString(R.string.profile_address)
                val data = navData as ProfileDetailModel?
                data?.personalInformation?.zipcode?.let { binding.inputPostCode.setText(it) }

                if (data?.accountInformation?.accountType.equals(Constants.PERSONAL_ACCOUNT,true)) {
                    setPersonalView()
                }

            }
            else -> {
                if (NewCreateAccountRequestModel.personalAccount) {
                    setPersonalView()
                }
            }

        }
    }

    private fun setPersonalView() {
        binding.txtHeading.text = getString(R.string.personal_address)
        binding.txtThisShouldBeVehicle.text =
            getString(R.string.this_should_be_the_address_were_the_vehicle_is_registered)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnFindAddress -> {
                val data = navData as ProfileDetailModel?
              /*  if(binding.inputPostCode.getText().toString().equals(data?.personalInformation?.zipcode, true)){
                    findNavController().popBackStack()
                }else {*/
                    validation()
//                }
            }

            R.id.btnEnterAddressManually -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                if(navFlowCall.equals(PROFILE_MANAGEMENT,true) && navData != null){
                    val data = navData as ProfileDetailModel?
                    bundle.putParcelable(Constants.NAV_DATA_KEY,data)
                }
                findNavController().navigate(
                    R.id.action_createAccountPostCodeNew_to_ManualAddress, bundle
                )
            }

        }
    }

    private fun validation() {
        if (binding.inputPostCode.getText().toString().isNotEmpty()) {
            if (binding.inputPostCode.editText.getText().toString()
                    .contains(Utils.TWO_OR_MORE_HYPEN)
            ) {
                binding.inputPostCode.setErrorText(getString(R.string.postcode_must_not_contain_hypen_more_than_once))
                false
            }
            else if(hasSpecialCharacters(binding.inputPostCode.getText().toString(), Utils.SPECIAL_CHARACTERS_POSTCODE)){
                binding.inputPostCode.setErrorText(getString(R.string.postcode_must_not_contain_special_characters))
                false
            }
            else {
                NewCreateAccountRequestModel.zipCode = binding.inputPostCode.getText().toString()
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                if (navFlowCall.equals(PROFILE_MANAGEMENT, true) && navData != null) {
                    val data = navData as ProfileDetailModel?
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                }
                findNavController().navigate(
                    R.id.action_createAccountPostCodeNew_to_selectaddressfragment,
                    bundle
                )
            }
            } else {
            ErrorUtil.showError(binding.root, getString(R.string.please_enter_postcode))
        }
    }


    override fun onRetryClick() {

    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {

            requiredPostCode = if (binding.inputPostCode.getText().toString().trim().isEmpty()) {
                //binding.inputPostCode.setErrorText(getString(R.string.str_post_code_error_message))
                false
            } else {
                val string = binding.inputPostCode.getText().toString().trim()
                val finalString = string.replace(" ", "")
                if (finalString.length < 4 || finalString.length > 11) {
                    binding.inputPostCode.setErrorText(getString(R.string.postcode_must_be_between_4_and_10_characters))
                    false

                }
                else if (binding.inputPostCode.editText.getText().toString()
                        .contains(Utils.TWO_OR_MORE_HYPEN)
                ) {
                    binding.inputPostCode.setErrorText(getString(R.string.postcode_must_not_contain_hypen_more_than_once))
                    false
                }
                else if(hasSpecialCharacters(finalString,Utils.SPECIAL_CHARACTERS_POSTCODE)){
                    binding.inputPostCode.setErrorText(getString(R.string.postcode_must_not_contain_special_characters))
                    false
                }
                else{
                    binding.inputPostCode.removeError()
                    true
                }

            }


        }

        override fun afterTextChanged(editable: Editable?) {
            binding.btnFindAddress.isEnabled = requiredPostCode
        }
    }
}