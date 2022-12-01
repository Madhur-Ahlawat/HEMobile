package com.conduent.nationalhighways.ui.account.profile.password

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.databinding.FragmentProfilePasswordBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePasswordFragment : BaseFragment<FragmentProfilePasswordBinding>(),
    View.OnClickListener {


    private var accountType: String = Constants.PERSONAL_ACCOUNT
    private var isSecondaryUser: Boolean = false
    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfilePasswordBinding.inflate(inflater, container, false)

    override fun init() {
        checkButton()
        binding.data = arguments?.getParcelable(Constants.DATA)

        accountType = sessionManager.getAccountType() ?: Constants.PERSONAL_ACCOUNT
        isSecondaryUser = sessionManager.getSecondaryUser()

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


        binding.data?.personalInformation?.confirmPassword =
            binding.data?.accountInformation?.password
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@ProfilePasswordFragment)
            btnChangePassword.setOnClickListener(this@ProfilePasswordFragment)
            tiePassword.onTextChanged { checkButton() }
            tieConfirmPassword.onTextChanged { checkButton() }
        }
    }

    override fun observer() {
        observe(viewModel.updateProfileApiVal, ::handleUpdateProfileDetail)

    }


    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                requireActivity().showToast(getString(R.string.str_profile_updated_successfully))
                when {
                    accountType == Constants.PERSONAL_ACCOUNT && !isSecondaryUser -> {
                        findNavController().navigate(R.id.action_passwordFragment_to_viewProfileFragment)
                    }
                    accountType == Constants.BUSINESS_ACCOUNT && !isSecondaryUser -> {
                        findNavController().navigate(R.id.action_passwordFragment_to_viewBusinessAccountProfile)
                    }

                    isSecondaryUser -> {
                        findNavController().navigate(R.id.action_passwordFragment_to_viewNominatedPrimaryProfileFragment)
                    }
                    else -> {
                        findNavController().navigate(R.id.action_passwordFragment_to_viewProfileFragment)
                    }
                }

            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        val bundle = Bundle()
        bundle.putParcelable(Constants.DATA, binding.data)
        when (v?.id) {
            R.id.btnChangePassword -> {
                findNavController().navigate(
                    R.id.action_passwordFragment_to_updatePasswordFragment,
                    bundle
                )
            }
            R.id.btnAction -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                updateUserProfile()

//                findNavController().navigate(R.id.action_passwordFragment_to_pinFragment, bundle)
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


    private fun checkButton() {
        binding.enable = (binding.tiePassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString()
            .trim() == binding.tiePassword.text.toString().trim())
    }
}
