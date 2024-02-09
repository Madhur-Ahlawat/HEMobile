package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModelList
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsResp
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.databinding.FragmentOptForSmsBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CommunicationPrefsViewModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OptForSmsFragment : BaseFragment<FragmentOptForSmsBinding>(), View.OnClickListener {
    private var oldCommunicationTextMessage = false
    private var loader: LoaderDialog? = null
    private val viewModelProfile: ProfileViewModel by viewModels()
    private var oldSmsOption: Boolean = false
    private var oldPushOption: Boolean = false
    private var isViewCreated: Boolean = false
    private var personalInformationModel: PersonalInformation? = null
    private var accountInformationModel: AccountInformation? = null
    private val dashboardViewmodel: DashboardViewModel by activityViewModels()
    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()


    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOptForSmsBinding = FragmentOptForSmsBinding.inflate(inflater, container, false)

    override fun init() {
        binding.btnNext.setOnClickListener(this)

        when (navFlowCall) {
            EDIT_SUMMARY, EDIT_ACCOUNT_TYPE -> {
                if (!isViewCreated) {
                    oldCommunicationTextMessage =
                        NewCreateAccountRequestModel.communicationTextMessage
                }

                binding.switchCommunication.isChecked =
                    NewCreateAccountRequestModel.communicationTextMessage
                binding.btnNext.enable()

            }

            Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                personalInformationModel = dashboardViewmodel.personalInformationData.value
                accountInformationModel = dashboardViewmodel.accountInformationData.value
            }

            else -> {
                NewCreateAccountRequestModel.communicationTextMessage = false
            }
        }

        binding.switchCommunication.setOnClickListener {
            if (binding.switchCommunication.isChecked) {
                binding.btnNext.enable()
                NewCreateAccountRequestModel.communicationTextMessage = true
            } else {
                NewCreateAccountRequestModel.communicationTextMessage = false
                binding.btnNext.enable()
            }
        }

        binding.switchNotification.setOnClickListener {
            if (!Utils.areNotificationsEnabled(requireContext())) {
                displayCustomMessage(
                    resources.getString(R.string.str_notification_title),
                    resources.getString(R.string.str_notification_desc),
                    resources.getString(R.string.str_allow),
                    resources.getString(R.string.str_dont_allow),
                    object : DialogPositiveBtnListener {
                        override fun positiveBtnClick(dialog: DialogInterface) {
                            binding.switchNotification.isChecked = true
                            Utils.redirectToNotificationPermissionSettings(requireContext())
                        }
                    },
                    object : DialogNegativeBtnListener {
                        override fun negativeBtnClick(dialog: DialogInterface) {
                            binding.switchNotification.isChecked = false
                        }
                    },
                    View.VISIBLE
                )
            }
        }



        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
                binding.switchCommunication.isChecked = oldCommunicationTextMessage
                binding.switchNotification.gone()
                binding.notificationTxt.gone()
            }

            Constants.ACCOUNT_CREATION_EMAIL_FLOW -> {
                binding.switchNotification.gone()
                binding.notificationTxt.gone()
            }

            Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                binding.switchNotification.gone()
                binding.notificationTxt.gone()
                val title: TextView? = requireActivity().findViewById(R.id.title_txt)
                title?.text = getString(R.string.communication_preferences)

                showLoader()
                communicationPrefsViewModel.getAccountSettingsPrefs()

            }

        }
        isViewCreated = true

    }

    override fun onResume() {
        super.onResume()
        when (navFlowCall) {
            Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                if (Utils.areNotificationsEnabled(requireContext()) == false) {
                    oldPushOption = false
                } else {
                    oldPushOption = sessionManager.fetchNotificationOption() ?: false
                }
                binding.switchNotification.isChecked =
                    oldPushOption
            }
        }
    }

    override fun initCtrl() {


    }

    override fun observer() {
        observe(viewModelProfile.updateProfileApiVal, ::handleUpdateProfileDetail)
        observe(webServiceViewModel.pushNotification, ::handlePushNotificationResponse)
        observe(
            communicationPrefsViewModel.updateCommunicationPrefs,
            ::updateCommunicationSettingsPrefs
        )
        observe(
            communicationPrefsViewModel.getAccountSettingsPrefs,
            ::getCommunicationSettingsPref
        )
    }

    private fun getCommunicationSettingsPref(resource: Resource<ProfileDetailModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                accountInformationModel = resource.data?.accountInformation
                Log.e(
                    "TAG",
                    "getCommunicationSettingsPref: " + accountInformationModel?.communicationPreferences.orEmpty().size
                )

                dashboardViewmodel.communicationPreferenceData.value =
                    resource.data?.accountInformation?.communicationPreferences ?: ArrayList()
                dashboardViewmodel.accountInformationData.value?.communicationPreferences =
                    resource.data?.accountInformation?.communicationPreferences ?: ArrayList()

                for (i in 0 until resource.data?.accountInformation?.communicationPreferences.orEmpty().size) {
                    val communicationModel =
                        resource.data?.accountInformation?.communicationPreferences?.get(i)

                    if (communicationModel?.category?.lowercase().equals("standard notification")) {
                        oldSmsOption = communicationModel?.smsFlag?.uppercase().equals("Y")
                        binding.switchCommunication.isChecked = oldSmsOption
                        break
                    }
                }


            }

            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    private fun updateCommunicationSettingsPrefs(resource: Resource<CommunicationPrefsResp?>?) {

        when (resource) {
            is Resource.Success -> {
                updateSmsOption()
            }

            is Resource.DataError -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }

            }

            else -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }
            }
        }
    }

    private fun handlePushNotificationResponse(resource: Resource<EmptyApiResponse?>) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (resource) {
            is Resource.Success -> {

                sessionManager.saveNotificationOption(binding.switchNotification.isChecked)
                if (oldSmsOption != binding.switchCommunication.isChecked) {
                    if (personalInformationModel?.phoneCell?.isEmpty() == true) {
                        verifyMobileNumber()
                    } else {
                        updateSmsOption()
                    }
                } else {
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.NAV_FLOW_KEY,
                        Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
                    )
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    findNavController().navigate(
                        R.id.action_optForSmsFragment_to_resetForgotPassword,
                        bundle
                    )
                }

                sessionManager.saveNotificationOption(binding.switchNotification.isChecked)

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
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


    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                when (navFlowCall) {
                    Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        if (binding.switchCommunication.isChecked) {
                            sessionManager.saveSmsOption("Y")
                        } else {
                            sessionManager.saveSmsOption("N")
                        }
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.NAV_FLOW_KEY,
                            Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
                        )
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                        findNavController().navigate(
                            R.id.action_optForSmsFragment_to_resetForgotPassword,
                            bundle
                        )
                    }

                    Constants.PROFILE_MANAGEMENT -> {
                    }

                    else -> {
                        findNavController().popBackStack()
                    }
                }

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
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
        when (v?.id) {
            R.id.btnNext -> {

                when (navFlowCall) {

                    EDIT_SUMMARY -> {
                        emailHeartBeatApi()
                        smsHeartBeatApi()

                        NewCreateAccountRequestModel.communicationTextMessage =
                            binding.switchCommunication.isChecked
                        if (binding.switchCommunication.isChecked == oldCommunicationTextMessage) {
                            findNavController().popBackStack()
                        } else if ((NewCreateAccountRequestModel.communicationTextMessage || binding.switchCommunication.isChecked) && NewCreateAccountRequestModel.mobileNumber?.isEmpty() == true) {
                            findNavController().navigate(
                                R.id.action_optForSmsFragment_to_mobileVerificationFragment,
                                bundle()
                            )
                        } else if ((!NewCreateAccountRequestModel.communicationTextMessage && !binding.switchCommunication.isChecked) && NewCreateAccountRequestModel.telephoneNumber?.isEmpty() == true) {
                            findNavController().navigate(
                                R.id.action_optForSmsFragment_to_mobileVerificationFragment,
                                bundle()
                            )
                        } else {
                            findNavController().popBackStack()
                        }
                    }

                    Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        if ((oldSmsOption == binding.switchCommunication.isChecked) && (oldPushOption == binding.switchNotification.isChecked)) {
                            findNavController().popBackStack()
                        } else {
                            if (personalInformationModel?.phoneCell?.isEmpty() == true) {
                                verifyMobileNumber()
                            } else {
                                if (Utils.isSupportedCountry(personalInformationModel?.phoneCellCountryCode.toString())) {
                                    showLoader()
                                    val model = CommunicationPrefsRequestModel(ArrayList())
                                    var smsOption = "N"
                                    if (binding.switchCommunication.isChecked) {
                                        smsOption = "Y"
                                    }
                                    val communicationList =
                                        ArrayList<CommunicationPrefsRequestModelList>()
                                    for (i in 0 until accountInformationModel?.communicationPreferences.orEmpty().size) {
                                        val communicationModel =
                                            accountInformationModel?.communicationPreferences?.get(i)
                                        val smsFlag = if (communicationModel?.category?.lowercase()
                                                .equals("standard notification")
                                        ) {
                                            smsOption
                                        } else {
                                            communicationModel?.smsFlag ?: "Y"
                                        }

                                        communicationList.add(
                                            CommunicationPrefsRequestModelList(
                                                communicationModel?.id,
                                                communicationModel?.category,
                                                communicationModel?.oneMandatory,
                                                communicationModel?.defEmail,
                                                communicationModel?.emailFlag,
                                                communicationModel?.mailFlag,
                                                communicationModel?.defSms,
                                                smsFlag,
                                                communicationModel?.defVoice,
                                                communicationModel?.voiceFlag,
                                                communicationModel?.pushNotFlag,
                                                communicationModel?.defPushNot,
                                                communicationModel?.defMail,
                                            )
                                        )

                                    }
                                    model.categoryList = communicationList
                                    communicationPrefsViewModel.updateCommunicationPrefs(model)
                                } else {
                                    verifyMobileNumber()
                                }
                            }
                        }
                    }

                    EDIT_ACCOUNT_TYPE -> {
                        emailHeartBeatApi()
                        smsHeartBeatApi()

                        NewCreateAccountRequestModel.communicationTextMessage =
                            binding.switchCommunication.isChecked
                        findNavController().navigate(
                            R.id.action_optForSmsFragment_to_twoStepVerificationFragment, bundle()
                        )
                    }

                    else -> {
                        emailHeartBeatApi()
                        NewCreateAccountRequestModel.communicationTextMessage =
                            binding.switchCommunication.isChecked

                        findNavController().navigate(
                            R.id.action_optForSmsFragment_to_twoStepVerificationFragment, bundle()
                        )
                    }

                }
            }
        }
    }

    private fun callPushNotificationApi() {
        var optInStatus = "N"
        if (binding.switchNotification.isChecked) {
            optInStatus = "Y"
        }
        sessionManager.getFirebaseToken()?.let { firebaseToken ->
            val request = PushNotificationRequest(
                deviceToken = firebaseToken,
                osName = PushNotificationUtils.getOSName(),
                osVersion = PushNotificationUtils.getOSVersion(),
                appVersion = PushNotificationUtils.getAppVersion(requireContext()),
                optInStatus = optInStatus
            )
            webServiceViewModel.allowPushNotification(request)
        }
    }


    private fun bundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putString(Constants.NAV_FLOW_FROM, Constants.OPTSMS)
        return bundle
    }

    private fun verifyMobileNumber() {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED)
        bundle.putParcelable(Constants.NAV_DATA_KEY, dashboardViewmodel.profileDetailModel.value)
        findNavController().navigate(
            R.id.action_optForSmsFragment_to_mobileVerificationFragment,
            bundle
        )
    }


    private fun updateSmsOption() {
        showLoader()
        var smsOption = "N"
        if (binding.switchCommunication.isChecked) {
            smsOption = "Y"
        }
        personalInformationModel?.run {
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
                phoneCell = phoneCell,
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = smsOption,
                mfaEnabled = Utils.returnMfaStatus(accountInformationModel?.mfaEnabled ?: ""),
                phoneEvening = "",
                phoneCellCountryCode = phoneCellCountryCode,
                phoneDayCountryCode = phoneDayCountryCode
            )

            viewModelProfile.updateUserDetails(request)
        }
    }

    fun showLoader() {
        val fragmentManager = requireActivity().supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag(Constants.LOADER_DIALOG)

        if (existingFragment != null) {
            (existingFragment as LoaderDialog).dismiss()
        }

//        if (existingFragment == null) {
            // Fragment is not added, add it now
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomLoaderDialog)
            loader?.show(fragmentManager, Constants.LOADER_DIALOG)
//        }
    }

}