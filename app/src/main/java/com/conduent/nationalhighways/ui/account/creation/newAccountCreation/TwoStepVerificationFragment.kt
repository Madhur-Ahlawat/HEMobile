package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

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
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentTwoStepVerificationBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TwoStepVerificationFragment : BaseFragment<FragmentTwoStepVerificationBinding>(),
    View.OnClickListener {
    //    private lateinit var  navFlow:String // create account , forgot password
    private var oldtwoStepVerification = false
    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()
    private var isViewCreated: Boolean = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTwoStepVerificationBinding.inflate(inflater, container, false)

    override fun init() {

//        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        when (navFlowCall) {
            EDIT_SUMMARY, EDIT_ACCOUNT_TYPE -> {
                if(!isViewCreated){
                    oldtwoStepVerification = NewCreateAccountRequestModel.twoStepVerification
                }
                binding.twoFactor.isChecked = NewCreateAccountRequestModel.twoStepVerification
            }

            else -> {
                NewCreateAccountRequestModel.twoStepVerification = false
            }
        }

        binding.twoFactor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
//                NewCreateAccountRequestModel.twoStepVerification = true
                binding.btnNext.enable()
            } else {
//                NewCreateAccountRequestModel.twoStepVerification = false
                binding.btnNext.enable()
            }

        }

        binding.btnNext.setOnClickListener(this)

        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
                binding.twoFactor.isChecked = NewCreateAccountRequestModel.twoStepVerification
            }

            PROFILE_MANAGEMENT_2FA_CHANGE -> {
                val data = navData as ProfileDetailModel?
                if (data != null) {
                    binding.twoFactor.isChecked =
                        data.accountInformation?.mfaEnabled.equals("true", true)
                }
                binding.btnNext.enable()
            }

        }
        isViewCreated=true

    }

    override fun initCtrl() {
    }

    override fun observer() {
        observe(viewModel.updateProfileApiVal, ::handleUpdateProfileDetail)
    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                val data = navData as ProfileDetailModel?
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_twoStepVerificationFragment_to_resetForgotPassword,
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

    private fun bundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putString(Constants.NAV_FLOW_FROM, Constants.TwoStepVerification)
        return bundle
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                when (navFlowCall) {

                    EDIT_SUMMARY -> {
                        Log.e("TAG", "onClick: oldtwoStepVerification "+oldtwoStepVerification )
                        Log.e("TAG", "onClick: twoStepVerification "+NewCreateAccountRequestModel.twoStepVerification )
                        NewCreateAccountRequestModel.twoStepVerification = binding.twoFactor.isChecked

                        if (binding.twoFactor.isChecked == oldtwoStepVerification) {
                            findNavController().popBackStack()
                        } else if ((NewCreateAccountRequestModel.communicationTextMessage || binding.twoFactor.isChecked)
                            && NewCreateAccountRequestModel.mobileNumber?.isEmpty() == true) {
                            findNavController().navigate(
                                R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,
                                bundle()
                            )
                        } else if ((!NewCreateAccountRequestModel.communicationTextMessage && !binding.twoFactor.isChecked) && NewCreateAccountRequestModel.telephoneNumber?.isEmpty() == true) {
                            findNavController().navigate(
                                R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,
                                bundle()
                            )
                        } else {
                            findNavController().popBackStack()
                        }
                    }

                    PROFILE_MANAGEMENT_2FA_CHANGE -> {
                        NewCreateAccountRequestModel.twoStepVerification = binding.twoFactor.isChecked
                        val data = navData as ProfileDetailModel?
                        if (data?.personalInformation?.phoneCell.isNullOrEmpty()) {
                            verifyMobileNumber(data)
                        } else {
                            loader?.show(
                                requireActivity().supportFragmentManager,
                                Constants.LOADER_DIALOG
                            )
                            if (data?.accountInformation?.accountType.equals(
                                    Constants.PERSONAL_ACCOUNT,
                                    true
                                )
                            ) {
                                updateStandardUserProfile(data)
                            } else {
                                updateBusinessUserProfile(data)
                            }
                        }
                    }

                    EDIT_ACCOUNT_TYPE -> {
                        NewCreateAccountRequestModel.twoStepVerification =
                            binding.twoFactor.isChecked
                        if ((NewCreateAccountRequestModel.mobileNumber?.isNotEmpty() == true) || (NewCreateAccountRequestModel.telephoneNumber?.isNotEmpty() == true)) {
                            bundle.putString(Constants.PLATE_NUMBER, "")
                            bundle.putInt(Constants.VEHICLE_INDEX, 0)
                            findNavController().navigate(
                                R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,
                                bundle
                            )
                        } else {
                            findNavController().navigate(
                                R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,
                                bundle
                            )
                        }
                    }

                    else -> {
                        NewCreateAccountRequestModel.twoStepVerification =
                            binding.twoFactor.isChecked
                        findNavController().navigate(
                            R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,
                            bundle
                        )
                    }

                }


            }
        }
    }

    private fun verifyMobileNumber(model: ProfileDetailModel?) {
        val bundle = Bundle()
        model?.accountInformation?.mfaEnabled = if (binding.twoFactor.isChecked) "Y" else "N"
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putParcelable(Constants.NAV_DATA_KEY, model)
        findNavController().navigate(
            R.id.action_twoStepVerificationFragment_to_HWMobileNumberCaptureVC,
            bundle
        )
    }

    private fun updateBusinessUserProfile(
        data: ProfileDetailModel?
    ) {
        data?.run {
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
                businessName = personalInformation?.customerName,
                mfaEnabled = if (binding.twoFactor.isChecked) "Y" else "N"
            )

            viewModel.updateUserDetails(request)
        }


    }

    private fun updateStandardUserProfile(
        data: ProfileDetailModel?
    ) {

        data?.personalInformation?.run {
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
                phoneEvening = "",
                mfaEnabled = if (binding.twoFactor.isChecked) "Y" else "N"
            )

            viewModel.updateUserDetails(request)
        }

    }

}