package com.heandroid.ui.account.profile.pin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.UpdateProfileRequest
import com.heandroid.data.model.profile.AccountPinChangeModel
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.databinding.FragmentProfilePinBinding
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfilePinFragment : BaseFragment<FragmentProfilePinBinding>(), View.OnClickListener {

    @Inject
    lateinit var  sessionManager: SessionManager
    private var accountType : String = Constants.PERSONAL_ACCOUNT
    private var isSecondaryUser : Boolean = false


    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfilePinBinding.inflate(inflater, container, false)

    override fun init() {

        accountType = sessionManager.getAccountType() ?: Constants.PERSONAL_ACCOUNT
        isSecondaryUser = sessionManager.getSecondaryUser()

        binding.enable = false
        binding.enable = true
        binding.data = arguments?.getParcelable(Constants.DATA)
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
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnSave -> {
               loader?.show(requireActivity().supportFragmentManager, "")
                updateUserProfile()

            }

            R.id.btnChangePin -> {
                changeAccountPin()
            }

        }
    }

    private fun updateUserProfile() {
        when  {
            ! isSecondaryUser && accountType== Constants.PERSONAL_ACCOUNT ->
            { updateStandardUserProfile() }
            !isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT ->
            { updateBusinessUserProfile() }

            isSecondaryUser && accountType ==Constants.PERSONAL_ACCOUNT->{
                updateNominatedContactUserProfile()
            }
            isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT->{
               updateNominatedBusinessUserProfile()
            }
            else->{ updatePaygUserProfile() }
        }
    }

    private fun updatePaygUserProfile() {


    }

    private fun updateNominatedBusinessUserProfile() {

    }

    private fun updateNominatedContactUserProfile() {
        // use
    }

    private fun updateBusinessUserProfile() {
        binding.data?.run {
            var request = UpdateProfileRequest(
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
                fein=accountInformation?.fein,
                businessName = personalInformation?.customerName
            )

            viewModel.updateUserDetails(request)
        }


    }

    private fun updateStandardUserProfile() {

        binding.data?.personalInformation?.run {
            var request = UpdateProfileRequest(
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
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = AccountPinChangeModel(
            binding.tvPinOne.text.toString() +
                    binding.tvPinTwo.text.toString() +
                    binding.tvPinThree.text +
                    binding.tvPinFour.text
        )
        viewModel.updateAccountPin(request)
    }

    private fun checkButton() {
        binding.enable = binding.tvPinOne.text.toString().isNotEmpty() &&
                binding.tvPinTwo.text.toString().isNotEmpty() &&
                binding.tvPinThree.text.toString().isNotEmpty() &&
                binding.tvPinFour.text.toString().isNotEmpty()
    }


    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
       loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                requireActivity().showToast(getString(R.string.str_profile_updated_successfully))
                when{
                    accountType==Constants.PERSONAL_ACCOUNT && !isSecondaryUser->{
                        findNavController().navigate(R.id.action_pinFragment_to_viewProfileFragment)
                    }

                    else->{
                        findNavController().navigate(R.id.action_pinFragment_to_viewProfileFragment)
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
}