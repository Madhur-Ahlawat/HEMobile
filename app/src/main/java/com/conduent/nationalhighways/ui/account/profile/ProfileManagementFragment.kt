package com.conduent.nationalhighways.ui.account.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountSummaryBinding
import com.conduent.nationalhighways.ui.account.biometric.BiometricActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.openActivityWithDataBack
import com.conduent.nationalhighways.utils.extn.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileManagementFragment : BaseFragment<FragmentCreateAccountSummaryBinding>(),
    View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var profileDetailModel: ProfileDetailModel? = null
    private val countryViewModel: CreateAccountPostCodeViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateAccountSummaryBinding =
        FragmentCreateAccountSummaryBinding.inflate(inflater, container, false)

    override fun init() {
        binding.accountCard.gone()
        binding.accountSubType.gone()
        binding.emailCard.gone()
        binding.title.gone()
        binding.communicationCard.gone()
        binding.vehicleHeading.invisible()
        binding.recyclerView.gone()
        binding.checkBoxTerms.gone()
        binding.btnNext.gone()
        binding.emailProfileManagement.visible()
        binding.emailAddressSummary.gone()
        binding.emailCardProfile.visible()
        binding.biometricsCard.visible()
        binding.passwordCard.visible()


        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        if(sessionManager.fetchStringData(SessionManager.COUNTRIES).isEmpty()){
            countryViewModel.getCountries()
        }else{
            viewModel.accountDetail()
        }
        val title: TextView? = requireActivity().findViewById(R.id.title_txt)
        title?.text = getString(R.string.profile_management)
    }

    override fun initCtrl() {

        binding.editFullName.setOnClickListener(this)
        binding.editAddress.setOnClickListener(this)
        binding.editEmailAddressProfile.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileManagementFragment_to_emailAddressFragment,
                bundle()
            )
        }
        binding.editMobileNumber.setOnClickListener(this)
        binding.editCommunications.setOnClickListener(this)
        binding.editTwoStepVerification.setOnClickListener(this)
        binding.editEmailAddress.setOnClickListener(this)
        binding.editBiometrics.setOnClickListener(this)
        binding.editAccountType.setOnClickListener(this)
        binding.editCompanyName.setOnClickListener(this)
        binding.editPassword.setOnClickListener(this)


    }

    override fun onStart() {
        if (sessionManager.fetchTouchIdEnabled()) {
            binding.biometrics.text = getString(R.string.yes)
        } else {
            binding.biometrics.text = getString(R.string.no)
        }
        super.onStart()
    }

    override fun observer() {
        observe(viewModel.accountDetail, ::handleAccountDetail)
        observe(countryViewModel.countriesList, ::getCountriesList)
    }


    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
        viewModel.accountDetail()

        when (response) {
            is Resource.Success -> {
               sessionManager.saveStringData(SessionManager.COUNTRIES,response.data.toString())
            }

            is Resource.DataError -> {
                if ((response.errorModel?.errorCode == Constants.TOKEN_FAIL && response.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || response.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(response.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, response.errorMsg)
                }
            }

            else -> {
            }

        }
    }



    private fun handleAccountDetail(status: Resource<ProfileDetailModel?>?) {
        loader?.dismiss()

        when (status) {
            is Resource.Success -> {
                status.data?.run {
                    if (status.equals("500")) ErrorUtil.showError(binding.root, message)
                    else {
                        profileDetailModel = status.data
//                        profileDetailModel?.personalInformation?.phoneCell = ""
                        (personalInformation?.firstName + " " + personalInformation?.lastName).also {
                            binding.fullName.text = it
                        }

                        if (accountInformation?.mfaEnabled.equals("true", true)) {
                            binding.twoStepVerification.text = getString(R.string.yes)
                        } else {
                            binding.twoStepVerification.text = getString(R.string.no)
                        }
                        if (personalInformation?.addressLine1?.isEmpty() == true && personalInformation?.city?.isEmpty() == true && personalInformation?.zipcode?.isEmpty() == true) {

                        } else {


                            binding.address.text =
                                personalInformation?.addressLine1 + "\n" + personalInformation?.city + "\n" + personalInformation?.zipcode+"\n"+ Utils.getCountryName(sessionManager,personalInformation?.country?:"")
                        }
                        binding.emailAddressProfile.text =
                            personalInformation?.userName?.lowercase()

                        if (personalInformation?.phoneCell.isNullOrEmpty().not()) {
                            binding.txtMobileNumber.text = getString(R.string.mobile_phone_number)
                            personalInformation?.phoneCell?.let {
                                binding.mobileNumber.text =
                                    personalInformation?.phoneCellCountryCode + " " + it
                            }
                        } else if (personalInformation?.phoneDay.isNullOrEmpty().not()) {
                            binding.txtMobileNumber.text = getString(R.string.telephone_number)

                            personalInformation?.phoneDay?.let {
                                binding.mobileNumber.text =
                                    personalInformation?.phoneDayCountryCode + " " + it
                            }
                        } else {
                            binding.txtMobileNumber.text = getString(R.string.telephone_number)
                        }
                        binding.accountType.text = accountInformation!!.accountType

                        if (accountInformation.accountType.equals(
                                Constants.BUSINESS_ACCOUNT,
                                true
                            )
                        ) {
                            binding.companyNameCard.visible()
                            binding.companyName.text = personalInformation?.customerName
                        } else {
                            binding.companyNameCard.gone()
                        }
                        binding.password.text = accountInformation.password
                    }
                }

            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(NAV_DATA_KEY, profileDetailModel)
        when (v?.id) {

            R.id.editCompanyName, R.id.editFullName -> {
                findNavController().navigate(
                    R.id.action_profileManagementFragment_to_personalInfoFragment,
                    bundle()
                )
            }

            R.id.editAddress -> {
                findNavController().navigate(
                    R.id.action_profileManagementFragment_to_postCodeFragment,
                    bundle()
                )
            }

            R.id.editEmailAddress -> {
                findNavController().navigate(
                    R.id.action_profileManagementFragment_to_emailAddressFragment,
                    bundle()
                )
            }

            R.id.editMobileNumber -> {

                bundle.putString(NAV_FLOW_KEY, PROFILE_MANAGEMENT_MOBILE_CHANGE)
                findNavController().navigate(
                    R.id.action_profileManagementFragment_to_mobileNumberFragment,
                    bundle
                )
            }

            R.id.editTwoStepVerification -> {
                bundle.putString(NAV_FLOW_KEY, PROFILE_MANAGEMENT_2FA_CHANGE)
                findNavController().navigate(
                    R.id.action_profileManagementFragment_to_twoStepCommunicationFragment,
                    bundle
                )
            }

            R.id.editPassword -> {
                findNavController().navigate(
                    R.id.action_profileManagementFragment_to_changePassword,
                    bundle
                )
            }

            R.id.editAccountType -> {
                findNavController().navigate(
                    R.id.action_profileManagementFragment_to_createAccountTypes,
                    bundle
                )
            }

            R.id.editBiometrics -> {
                requireActivity().openActivityWithDataBack(BiometricActivity::class.java) {
                    putInt(
                        Constants.FROM_LOGIN_TO_BIOMETRIC,
                        Constants.FROM_ACCOUNT_TO_BIOMETRIC_VALUE
                    )
                }
            }
        }
    }

    private fun bundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, PROFILE_MANAGEMENT)
        bundle.putParcelable(NAV_DATA_KEY, profileDetailModel)
        return bundle
    }


}