package com.conduent.nationalhighways.ui.account.profile.pin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.profile.AccountPinChangeModel
import com.conduent.nationalhighways.databinding.FragmentProfilePinBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePinFragment : BaseFragment<FragmentProfilePinBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private var accountType: String = Constants.PERSONAL_ACCOUNT
    private var isSecondaryUser: Boolean = false
    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfilePinBinding.inflate(inflater, container, false)

    override fun init() {
        accountType = sessionManager.getAccountType() ?: Constants.PERSONAL_ACCOUNT
        isSecondaryUser = sessionManager.getSecondaryUser()

        checkButton()
        if (sessionManager.getSecondaryUser()) {
            // binding.nominated = arguments?.getParcelable(Constants.DATA)
        } else {
            binding.data = arguments?.getParcelable(Constants.DATA)
        }
        // binding.data?.personalInformation?.confirmPassword=binding.data?.accountInformation?.password
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            btnSave.setOnClickListener(this@ProfilePinFragment)
            btnChangePin.setOnClickListener(this@ProfilePinFragment)
            tvPinOne.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinTwo.requestFocus()
                checkButton()
            }
            tvPinTwo.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinThree.requestFocus()
                else binding.tvPinOne.requestFocus()
                checkButton()
            }
            tvPinThree.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinFour.requestFocus()
                else binding.tvPinTwo.requestFocus()

                checkButton()
            }
            tvPinFour.doAfterTextChanged {
                if (it?.isNotEmpty() == true) hideKeyboard()
                else binding.tvPinThree.requestFocus()
                checkButton()
            }
        }
    }

    override fun observer() {
        observe(viewModel.updateProfileApiVal, ::handleUpdateProfileDetail)
        observe(viewModel.updateAccountPinApiVal, ::handlePinChange)
    }

    private fun handlePinChange(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                val bundle = Bundle().apply {
                    putBoolean(Constants.UPDATE_PIN_FLOW, true)
                    putParcelable(Constants.DATA, arguments?.getParcelable(Constants.DATA))
                }
                findNavController().navigate(
                    R.id.action_pinFragment_to_updatePasswordSuccessfulFragment,
                    bundle
                )
            }
            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                }else {
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
            R.id.btnSave -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                updateUserProfile()

            }

            R.id.btnChangePin -> {
                changeAccountPin()
            }

        }
    }

    private fun updateUserProfile() {
        when {
            !isSecondaryUser && accountType == Constants.PERSONAL_ACCOUNT -> {
                updateStandardUserProfile()
            }
            !isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT -> {
                updateBusinessUserProfile()
            }
            isSecondaryUser -> {
                // updateAccountHolderByNominated()
                updateStandardUserProfile()
            }
            else -> {
                updatePaygUserProfile()
            }
        }
    }

    private fun updatePaygUserProfile() {


    }

    private fun updateAccountHolderByNominated() {
        binding.data?.personalInformation?.run {
            val request = UpdateProfileRequest(
                firstName = firstName,
                lastName = lastName,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                city = city,
                state = state,
                zipCode = zipcode,
                zipCodePlus = zipCodePlus,
                country = country,
                emailAddress = emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = pemailUniqueCode,
                phoneCell = phoneNumber ?: "",
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = ""
            )

            viewModel.updateUserDetails(request)
        }

    }


    private fun updateBusinessUserProfile() {
        binding.data?.run {
            val request = UpdateProfileRequest(
                firstName = personalInformation?.firstName,
                lastName = personalInformation?.lastName,
                addressLine1 = personalInformation?.addressLine1,
                addressLine2 = personalInformation?.addressLine2,
                city = personalInformation?.city,
                state = personalInformation?.state,
                zipCode = personalInformation?.zipcode,
                zipCodePlus = personalInformation?.zipCodePlus,
                country = personalInformation?.country,
                emailAddress = personalInformation?.emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = personalInformation?.pemailUniqueCode,
                phoneCell = personalInformation?.phoneNumber ?: "",
                phoneDay = personalInformation?.phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = "",
                fein = accountInformation?.fein,
                businessName = personalInformation?.customerName
            )

            viewModel.updateUserDetails(request)
        }


    }

    private fun updateStandardUserProfile() {

        binding.data?.personalInformation?.run {
            val request = UpdateProfileRequest(
                firstName = firstName,
                lastName = lastName,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                city = city,
                state = state,
                zipCode = zipcode,
                zipCodePlus = zipCodePlus,
                country = country,
                emailAddress = emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = pemailUniqueCode,
                phoneCell = phoneNumber ?: "",
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = ""
            )

            viewModel.updateUserDetails(request)
        }

    }


    private fun changeAccountPin() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = AccountPinChangeModel(
            binding.tvPinOne.getText().toString() +
                    binding.tvPinTwo.text.toString() +
                    binding.tvPinThree.text +
                    binding.tvPinFour.text
        )
        viewModel.updateAccountPin(request)
    }

    private fun checkButton() {
        binding.enable = binding.tvPinOne.getText().toString().isNotEmpty() &&
                binding.tvPinTwo.getText().toString().isNotEmpty() &&
                binding.tvPinThree.getText().toString().isNotEmpty() &&
                binding.tvPinFour.getText().toString().isNotEmpty()
    }


    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                requireActivity().showToast(getString(R.string.str_profile_updated_successfully))
                when {
                    accountType == Constants.PERSONAL_ACCOUNT && !isSecondaryUser -> {
                        findNavController().navigate(R.id.action_pinFragment_to_viewProfileFragment)
                    }
                    accountType == Constants.BUSINESS_ACCOUNT && !isSecondaryUser -> {
                        findNavController().navigate(R.id.action_pinFragment_to_viewBusinessAccountProfile)
                    }

                    isSecondaryUser -> {
                        findNavController().navigate(R.id.action_pinFragment_to_viewNominatedPrimaryProfileFragment)
                    }
                    else -> {
                        findNavController().navigate(R.id.action_pinFragment_to_viewProfileFragment)
                    }
                }

            }
            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(Constants.INVALID_TOKEN))|| resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(resource.errorModel)
                }else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }
            else -> {
            }
        }
    }
}