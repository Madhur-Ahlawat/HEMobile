package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPostCodeNewBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_FROM_POST_CODE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils.hasDigits
import com.conduent.nationalhighways.utils.common.Utils.hasLowerCase
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
import com.conduent.nationalhighways.utils.common.Utils.hasUpperCase
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountPostCodeNew : BaseFragment<FragmentCreateAccountPostCodeNewBinding>(),
    View.OnClickListener, OnRetryClickListener {
    private var requiredPostCode = false
    private var loader: LoaderDialog? = null
    private var apiCalled: Boolean = false
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var postcode: String = ""
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPostCodeNewBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.btnFindAddress.setOnClickListener(this)
        binding.btnEnterAddressManually.setOnClickListener(this)
        binding.btnUpdateAddressManually.setOnClickListener(this)
        binding.inputPostCode.setMaxLength(10)

        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
                binding.btnEnterAddressManually.visible()
                binding.btnUpdateAddressManually.gone()
                binding.inputPostCode.setText(NewCreateAccountRequestModel.zipCode)
                if (NewCreateAccountRequestModel.personalAccount) {
                    setPersonalView()
                }
            }

            PROFILE_MANAGEMENT -> {
                binding.btnEnterAddressManually.gone()
                binding.btnUpdateAddressManually.visible()
                val title: TextView? = requireActivity().findViewById(R.id.title_txt)
                title?.text = getString(R.string.profile_address)
                val data = navData as ProfileDetailModel?
                data?.personalInformation?.zipcode?.let { binding.inputPostCode.setText(it) }
                postcode = data?.personalInformation?.zipcode?:""

                if (data?.accountInformation?.accountType.equals(
                        Constants.PERSONAL_ACCOUNT,
                        true
                    )
                ) {
                    setPersonalView()
                }

            }

            else -> {
                binding.btnEnterAddressManually.visible()
                binding.btnUpdateAddressManually.gone()
                if (NewCreateAccountRequestModel.personalAccount) {
                    setPersonalView()
                }
            }

        }
        validatePostCode()
        binding.inputPostCode.editText
            .addTextChangedListener(GenericTextWatcher(binding.inputPostCode.editText))
    }

    private fun validatePostCode() {
        requiredPostCode = if (binding.inputPostCode.getText().toString().trim().isEmpty()) {
            binding.inputPostCode.removeError()
            false
        } else {
            val string = binding.inputPostCode.getText().toString().trim()
            val finalString = string.replace(" ", "")
            if (!(hasLowerCase(
                    binding.inputPostCode.editText.text.toString().trim()
                ) || hasUpperCase(
                    binding.inputPostCode.editText.text.toString().trim()
                )) || !hasDigits(
                    binding.inputPostCode.editText.text.toString().trim()
                ) || hasSpecialCharacters(
                    binding.inputPostCode.getText().toString().trim(),
                    ""
                )
            ) {
                binding.inputPostCode.setErrorText(getString(R.string.postcode_must_not_contain_special_characters))
                false
            } else if (finalString.length < 4 || finalString.length > 10) {
                binding.inputPostCode.setErrorText(getString(R.string.postcode_must_be_between_4_and_10_characters))
                false
            } else {
                binding.inputPostCode.removeError()
                true
            }

        }
        binding.btnFindAddress.isEnabled = requiredPostCode
    }

    private fun setPersonalView() {
        binding.txtHeading.text = getString(R.string.personal_address)
        binding.txtThisShouldBeVehicle.text =
            getString(R.string.this_should_be_the_address_were_the_vehicle_is_registered)
    }

    override fun initCtrl() {
    }

    override fun observer() {

        lifecycleScope.launch {
            viewModel.addressesState.collect {
                handleAddressApiResponse(it)
            }
        }
    }

    private fun handleAddressApiResponse(response: Resource<List<DataAddress?>?>?) {
        if (apiCalled) {
            apiCalled = false
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            when (response) {
                is Resource.Success -> {

                    var addressList: List<DataAddress?>? = response.data
                    when (navFlowCall) {
                        EDIT_SUMMARY, EDIT_ACCOUNT_TYPE -> {
                            addressList?.forEach { it?.isSelected = false }
                            if (NewCreateAccountRequestModel.selectedAddressId != -1) {
                                addressList?.get(NewCreateAccountRequestModel.selectedAddressId)?.isSelected =
                                    true
                            }
                        }
                    }

                    if (NewCreateAccountRequestModel.zipCode != binding.inputPostCode.getText()
                            .toString()
                    ) {
                        NewCreateAccountRequestModel.selectedAddressId = -1
                    }
                    NewCreateAccountRequestModel.zipCode =
                        binding.inputPostCode.editText.text.toString()
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putParcelableArrayList(Constants.ADDRESS_LIST, addressList as ArrayList)
                    if (navFlowCall.equals(PROFILE_MANAGEMENT, true) && navData != null) {
                        val data = navData as ProfileDetailModel?
                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    }
                    findNavController().navigate(
                        R.id.action_createAccountPostCodeNew_to_selectaddressfragment,
                        bundle
                    )
                    lifecycleScope.launch {
                        viewModel._addressesState.emit(null)
                    }

                }

                is Resource.DataError -> {

                    if ((response.errorModel?.errorCode == Constants.TOKEN_FAIL && (response.errorModel.error != null && response.errorModel.error.equals(
                            Constants.INVALID_TOKEN
                        ))) || (response.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR && (response.errorModel.error != null && response.errorModel.error.equals(
                            Constants.SERVER_ERROR
                        )))
                    ) {
                        displaySessionExpireDialog(response.errorModel)
                    } else {
                        enterAddressManual()
                    }

                }

                else -> {

                    enterAddressManual()

                    lifecycleScope.launch {
                        viewModel._addressesState.emit(null)
                    }
                }


            }
        }

    }

    private fun enterAddressManual() {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        if (navData != null) {
            val data = navData as ProfileDetailModel?
            bundle.putParcelable(Constants.NAV_DATA_KEY, data)
        }
        bundle.putString(Constants.POST_CODE, postcode)
        bundle.putString(Constants.EDIT_POST_CODE, binding.inputPostCode.editText.text.toString())
        Log.e("TAG", "handleAddressApiResponse:enterAddressManual-> ")

        findNavController().navigate(R.id.action_createAccountPostCodeNew_to_ManualAddress, bundle)
    }


    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnFindAddress -> {
                validation()
            }

            R.id.btnEnterAddressManually, R.id.btnUpdateAddressManually -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                if (navFlowCall.equals(PROFILE_MANAGEMENT, true) && navData != null) {
                    val data = navData as ProfileDetailModel?
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                }
                bundle.putString(Constants.NAV_FLOW_FROM, EDIT_FROM_POST_CODE)
                findNavController().navigate(
                    R.id.action_createAccountPostCodeNew_to_ManualAddress, bundle
                )
            }

        }
    }

    private fun validation() {
        val string = binding.inputPostCode.getText().toString()
        val finalString = string.replace(" ", "")
        if (binding.inputPostCode.getText().toString().trim().isNotEmpty()) {
            if (hasSpecialCharacters(
                    binding.inputPostCode.getText().toString().trim(),
                    ""
                )
            ) {
                binding.inputPostCode.setErrorText(getString(R.string.postcode_must_not_contain_special_characters))
            } else if (finalString.length < 4 || finalString.length > 10) {
                binding.inputPostCode.setErrorText(getString(R.string.postcode_must_be_between_4_and_10_characters))
            } else {
                if (!apiCalled) {
                    apiCalled = true
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    viewModel.fetchAddressState(binding.inputPostCode.editText.text.toString())
                }
            }
        } else {
            ErrorUtil.showError(binding.root, getString(R.string.please_enter_postcode))
        }
    }


    override fun onRetryClick(apiUrl: String) {

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
                binding.inputPostCode.removeError()
                false
            } else {
                val string = binding.inputPostCode.getText().toString().trim()
                val finalString = string.replace(" ", "")
                if (!(hasLowerCase(
                        binding.inputPostCode.editText.text.toString().trim()
                    ) || hasUpperCase(
                        binding.inputPostCode.editText.text.toString().trim()
                    )) || !hasDigits(
                        binding.inputPostCode.editText.text.toString().trim()
                    ) || hasSpecialCharacters(
                        binding.inputPostCode.getText().toString().trim(),
                        ""
                    )
                ) {
                    binding.inputPostCode.setErrorText(getString(R.string.postcode_must_not_contain_special_characters))
                    false
                } else if (finalString.length < 4 || finalString.length > 10) {
                    binding.inputPostCode.setErrorText(getString(R.string.postcode_must_be_between_4_and_10_characters))
                    false
                } else {
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