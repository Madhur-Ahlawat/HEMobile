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
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPersonalInfoNewBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PERSONAL_ACCOUNT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
import com.conduent.nationalhighways.utils.common.Utils.splCharCompanyName
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountPersonalInfo : BaseFragment<FragmentCreateAccountPersonalInfoNewBinding>(),
    View.OnClickListener, OnRetryClickListener {

    private var requiredFirstName = false
    private var requiredLastName = false
    private var requiredCompanyName = false
    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()
    private val createAccountHeartBeatViewModel: CreateAccountVehicleViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPersonalInfoNewBinding.inflate(inflater, container, false)

    override fun init() {

        binding.inputFirstName.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.inputLastName.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.inputCompanyName.editText.addTextChangedListener(GenericTextWatcher(2))
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.btnNext.setOnClickListener(this)
        if (NewCreateAccountRequestModel.personalAccount) {
            enablePersonalView()
        }


        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
                emailHeartBeatApi()
                smsHeartBeatApi()
                binding.inputFirstName.setText(NewCreateAccountRequestModel.firstName)
                binding.inputLastName.setText(NewCreateAccountRequestModel.lastName)
                binding.inputCompanyName.setText(NewCreateAccountRequestModel.companyName)
                checkButtonEnable()
            }

            PROFILE_MANAGEMENT -> {
                val title: TextView? = requireActivity().findViewById(R.id.title_txt)
                title?.text = getString(R.string.profile_name)
                val data = navData as ProfileDetailModel?
                if (data?.accountInformation?.accountType.equals(PERSONAL_ACCOUNT, true)) {
                    enablePersonalView()
                }
                data?.personalInformation?.firstName?.let { binding.inputFirstName.setText(it) }
                data?.personalInformation?.lastName?.let { binding.inputLastName.setText(it) }
                data?.personalInformation?.customerName?.let { binding.inputCompanyName.setText(it) }
                checkButtonEnable()
            }

        }
    }

    private fun enablePersonalView() {
        binding.txtCompanyName.gone()
        binding.inputCompanyName.gone()
        binding.inputFirstName.setLabel(getString(R.string.primary_account_holder_first_name))
        binding.inputLastName.setLabel(getString(R.string.primary_account_holder_last_name))
    }

    override fun initCtrl() {
    }

    override fun observer() {
        observe(viewModel.updateProfileApiVal, ::handleUpdateProfileDetail)
        observe(createAccountHeartBeatViewModel.heartBeatLiveData, ::heartBeatApiResponse)

    }

    private fun heartBeatApiResponse(resource: Resource<EmptyApiResponse?>?) {

    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                var data:ProfileDetailModel?=null
                if (navData is ProfileDetailModel) {
                     data = navData as ProfileDetailModel?

                }
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(
                    Constants.NAV_DATA_KEY,
                    data?.personalInformation
                )
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_createAccountPersonalInfo_to_resetForgotPassword,
                    bundle
                )
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {

                when (navFlowCall) {

                    EDIT_SUMMARY -> {
                        storeData()
                        findNavController().popBackStack()
                    }

                    PROFILE_MANAGEMENT -> {
                        val data = navData as ProfileDetailModel?
                        val fName = binding.inputFirstName.getText().toString()
                        val lName = binding.inputLastName.getText().toString()
                        val cName = binding.inputCompanyName.getText().toString()
                        if (fName.equals(data?.personalInformation?.firstName, true) &&
                            lName.equals(data?.personalInformation?.lastName, true) &&
                            cName.equals(data?.personalInformation?.customerName, true)
                        ) {
                            findNavController().popBackStack()
                        } else {
                            loader?.show(
                                requireActivity().supportFragmentManager,
                                Constants.LOADER_DIALOG
                            )

                            updateProfileDetails(data, fName, lName, cName)

                        }


                    }

                    else -> {
                        storeData()
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        findNavController().navigate(
                            R.id.action_createAccountPersonalInfo_to_createAccountPostCodeNew,
                            bundle
                        )
                    }
                }
            }
        }
    }

    private fun storeData() {
        NewCreateAccountRequestModel.firstName =
            binding.inputFirstName.getText().toString()
        NewCreateAccountRequestModel.lastName =
            binding.inputLastName.getText().toString()
        NewCreateAccountRequestModel.companyName =
            binding.inputCompanyName.getText().toString()
    }

    override fun onRetryClick(apiUrl: String) {

    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
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

            if (NewCreateAccountRequestModel.personalAccount) {
                personalAccountErrorMessage(
                    charSequence,
                    start,
                    before,
                    count, index
                )
            } else {
                businessAccountErrorMessage(charSequence, start, before, count, index)
            }


        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButtonEnable() {
        if (requiredFirstName && requiredLastName) {
            if (!NewCreateAccountRequestModel.personalAccount) {
                if (requiredCompanyName) {
                    binding.btnNext.enable()
                } else {
                    binding.btnNext.disable()
                }
            } else {
                binding.btnNext.enable()
            }
        } else {
            binding.btnNext.disable()
        }
    }

    private fun businessAccountErrorMessage(
        charSequence: CharSequence?,
        start: Int,
        before: Int,
        count: Int,
        index: Int
    ) {
        if (index == 0) {

            if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
                binding.inputFirstName.removeError()
                requiredFirstName = false
            } else {
                if (binding.inputFirstName.getText().toString().trim().length <= 50) {
                    if (binding.inputFirstName.getText().toString().trim().replace(" ", "")
                            .matches(Utils.ACCOUNT_NAME_FIRSTNAME_LASTNAME)
                    ) {
                        binding.inputFirstName.removeError()
                        requiredFirstName = true
                    } else {
                        binding.inputFirstName.setErrorText(getString(R.string.str_first_name_error_message))
                        requiredFirstName = false
                    }
                } else {
                    requiredFirstName = false
                    binding.inputFirstName.setErrorText(getString(R.string.str_first_name_length_error_message))
                }
            }

            checkButtonEnable()
        } else if (index == 1) {


            if (binding.inputLastName.getText().toString().trim().isEmpty()) {
                binding.inputLastName.removeError()
                requiredLastName = false
            } else {
                if (binding.inputLastName.getText().toString().trim().length <= 50) {
                    if (binding.inputLastName.getText().toString().trim().replace(" ", "")
                            .matches(Utils.ACCOUNT_NAME_FIRSTNAME_LASTNAME)
                    ) {
                        binding.inputLastName.removeError()
                        requiredLastName = true
                    } else {
                        binding.inputLastName.setErrorText(getString(R.string.str_last_name_error_message))
                        requiredLastName = false
                    }
                } else {
                    requiredLastName = false
                    binding.inputLastName.setErrorText(getString(R.string.str_last_name_length_error_message))
                }
            }


            checkButtonEnable()
        } else if (index == 2) {
            requiredCompanyName =
                if (binding.inputCompanyName.getText().toString().trim().isEmpty()) {
                    binding.inputCompanyName.removeError()
                    false
                } else {
                    if (binding.inputCompanyName.getText().toString().trim().length > 50) {
                        binding.inputCompanyName.setErrorText(getString(R.string.str_company_name_error_message))
                        false
                    } else if (hasSpecialCharacters(
                            binding.inputCompanyName.getText().toString(),
                            splCharCompanyName
                        )
                    ) {
                        binding.inputCompanyName.setErrorText(getString(R.string.company_name_must_only_include_letters_a_to_z_numbers_0_to_9_and_special_characters_such_as_hyphens))
                        false
                    } else {
                        binding.inputCompanyName.removeError()
                        true
                    }

                }
        }
        checkButtonEnable()
    }

    private fun personalAccountErrorMessage(
        charSequence: CharSequence?,
        start: Int,
        before: Int,
        count: Int,
        index: Int
    ) {
        if (index == 0) {

            if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
                binding.inputFirstName.removeError()
                requiredFirstName = false
            } else {
                if (binding.inputFirstName.getText().toString().trim().length <= 50) {
                    if (binding.inputFirstName.getText().toString().trim().replace(" ", "")
                            .matches(Utils.ACCOUNT_NAME_FIRSTNAME_LASTNAME)
                    ) {
                        binding.inputFirstName.removeError()
                        requiredFirstName = true
                    } else {
                        binding.inputFirstName.setErrorText(getString(R.string.str_first_name_error_message))
                        requiredFirstName = false
                    }
                } else {
                    requiredFirstName = false
                    binding.inputFirstName.setErrorText(getString(R.string.str_first_name_length_error_message))
                }
            }

            checkButtonEnable()
        } else if (index == 1) {
            if (binding.inputLastName.getText().toString().trim().isEmpty()) {
                binding.inputLastName.removeError()
                requiredLastName = false
            } else {
                if (binding.inputLastName.getText().toString().trim().length <= 50) {
                    if (binding.inputLastName.getText().toString().trim().replace(" ", "")
                            .matches(Utils.ACCOUNT_NAME_FIRSTNAME_LASTNAME)
                    ) {
                        binding.inputLastName.removeError()
                        requiredLastName = true
                    } else {
                        binding.inputLastName.setErrorText(getString(R.string.str_last_name_error_message))
                        requiredLastName = false
                    }
                } else {
                    requiredLastName = false
                    binding.inputLastName.setErrorText(getString(R.string.str_last_name_length_error_message))
                }
            }
        }
        checkButtonEnable()
    }


    private fun updateProfileDetails(
        data: ProfileDetailModel?,
        fName: String,
        lName: String,
        cName: String
    ) {

        val request = Utils.returnEditProfileModel(
            cName,
            data?.accountInformation?.fein,
            fName,
            lName,
            data?.personalInformation?.addressLine1,
            data?.personalInformation?.addressLine2,
            data?.personalInformation?.city,
            data?.personalInformation?.state,
            data?.personalInformation?.zipcode,
            data?.personalInformation?.zipCodePlus,
            data?.personalInformation?.country,
            data?.personalInformation?.emailAddress,
            data?.personalInformation?.primaryEmailStatus,
            data?.personalInformation?.pemailUniqueCode,
            data?.personalInformation?.phoneCell,
            data?.personalInformation?.phoneCellCountryCode,
            data?.personalInformation?.phoneDay,
            data?.personalInformation?.phoneDayCountryCode,
            data?.personalInformation?.fax,
            data?.accountInformation?.smsOption,
            data?.personalInformation?.eveningPhone,
            data?.accountInformation?.stmtDelivaryMethod,
            data?.accountInformation?.correspDeliveryFrequency,
            Utils.retrunMfaStatus(data?.accountInformation?.mfaEnabled ?: ""),
            accountType = data?.accountInformation?.accountType
        )

        viewModel.updateUserDetails(request)

    }
}