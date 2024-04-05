package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.databinding.FragmentNewLandingBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.ui.checkpaidcrossings.CheckPaidCrossingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.openActivityWithData
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.notification.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentNewLandingBinding>(), OnRetryClickListener {

    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isChecked = false
    private var isPushNotificationChecked = true
    private var count = 1
    var apiState = Constants.UNAVAILABLE
    var apiEndTime: String = ""
    private var plateNumber: String = ""
    private var email: String = ""
    private var mobileNumber: String = ""
    private var countryCode: String = ""

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewLandingBinding {
        binding = FragmentNewLandingBinding.inflate(inflater, container, false)

        return binding
    }

    override fun init() {
        Log.e("TAG", "init:isLocationServiceRunning--> "+Utils.isLocationServiceRunning(requireContext()) )
        BaseApplication.screenNameAnalytics = ""
        if (arguments?.containsKey(Constants.PLATE_NUMBER) == true) {
            plateNumber = arguments?.getString(Constants.PLATE_NUMBER) ?: ""
        }
        if (arguments?.containsKey(Constants.EMAIL) == true) {
            email = arguments?.getString(Constants.EMAIL) ?: ""
        }
        if (arguments?.containsKey(Constants.MOBILE_NUMBER) == true) {
            mobileNumber = arguments?.getString(Constants.MOBILE_NUMBER) ?: ""
        }
        if (arguments?.containsKey(Constants.COUNTRY_TYPE) == true) {
            countryCode = arguments?.getString(Constants.COUNTRY_TYPE) ?: ""
        }
        binding.scrollViewLanding.post(Runnable {
            binding.scrollViewLanding.smoothScrollTo(
                0,
                binding.scrollViewLanding.bottom
            )
        })
        HomeActivityMain.accountDetailsData = null
        HomeActivityMain.checkedCrossing = null
        HomeActivityMain.crossing = null
        HomeActivityMain.dateRangeModel = null
        HomeActivityMain.paymentHistoryListData = mutableListOf()
        HomeActivityMain.paymentHistoryListDataCheckedCrossings = mutableListOf()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if (isPushNotificationChecked) {
            //callPushNotificationApi()
        }
        val backButton: ImageView? = requireActivity().findViewById(R.id.back_button)

        backButton?.visibility = View.GONE

        AdobeAnalytics.setScreenTrack(
            "home",
            "home",
            "english",
            "home",
            "splash",
            "home",
            sessionManager.getLoggedInUser()
        )

        if (navFlowFrom == Constants.ONE_OFF_PAYMENT_SUCCESS) {
            NewCreateAccountRequestModel.plateNumber = plateNumber
            NewCreateAccountRequestModel.emailAddress = email
            NewCreateAccountRequestModel.mobileNumber = mobileNumber
            NewCreateAccountRequestModel.countryCode = countryCode
            requireActivity().startNormalActivity(CreateAccountActivity::class.java)
        }

        if (navFlowFrom == Constants.CHECK_FOR_PAID_CROSSINGS) {
            requireActivity().startNormalActivity(CreateAccountActivity::class.java)
        }
        if (navFlowFrom == Constants.CHECK_FOR_PAID_CROSSINGS_ONEOFF) {
            requireActivity().startNormalActivity(MakeOffPaymentActivity::class.java)
        }
    }

    private fun setNotificationDescText() {
        if (Utils.areNotificationsEnabled(requireContext()) == false) {
            sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION, false)
        }

        if (sessionManager.fetchBooleanData(SessionManager.NOTIFICATION_PERMISSION) == true
            && sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION) == true
        ) {
            binding.notificationTv.text = resources.getString(R.string.str_disable_notifications)
        } else {
            binding.notificationTv.text = resources.getString(R.string.str_enable_notifications)
        }

    }

    override fun onResume() {
        super.onResume()
        sessionManager.setLoggedInUser(false)

        if (!Utils.areNotificationsEnabled(requireContext())) {
            sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION, false)
        }
        if (!Utils.checkLocationpermission(requireContext())) {
            sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, false)
        }
        setNotificationDescText()

//        if (!isChecked) {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        webServiceViewModel.checkServiceStatus()
//        }
        isChecked = true
        if (navFlowFrom != Constants.ONE_OFF_PAYMENT_SUCCESS) {
            resetOneOffData()
        }
    }

    private fun resetOneOffData() {
        NewCreateAccountRequestModel.referenceId = ""
        NewCreateAccountRequestModel.emailAddress = ""
        NewCreateAccountRequestModel.mobileNumber = ""
        NewCreateAccountRequestModel.countryCode = ""
        NewCreateAccountRequestModel.telephoneNumber = ""
        NewCreateAccountRequestModel.telephone_countryCode = ""
        NewCreateAccountRequestModel.communicationTextMessage = false
        NewCreateAccountRequestModel.termsCondition = false
        NewCreateAccountRequestModel.twoStepVerification = false
        NewCreateAccountRequestModel.personalAccount = false
        NewCreateAccountRequestModel.firstName = ""
        NewCreateAccountRequestModel.lastName = ""
        NewCreateAccountRequestModel.companyName = ""
        NewCreateAccountRequestModel.addressLine1 = ""
        NewCreateAccountRequestModel.addressLine2 = ""
        NewCreateAccountRequestModel.townCity = ""
        NewCreateAccountRequestModel.state = ""
        NewCreateAccountRequestModel.country = ""
        NewCreateAccountRequestModel.zipCode = ""
        NewCreateAccountRequestModel.selectedAddressId = -1
        NewCreateAccountRequestModel.prePay = false
        NewCreateAccountRequestModel.plateCountry = ""
        NewCreateAccountRequestModel.plateNumber = ""
        NewCreateAccountRequestModel.plateNumberIsNotInDVLA = false
        NewCreateAccountRequestModel.vehicleList = mutableListOf<NewVehicleInfoDetails>()
        NewCreateAccountRequestModel.addedVehicleList = ArrayList<VehicleResponse?>()
        NewCreateAccountRequestModel.addedVehicleList2 = ArrayList<VehicleResponse?>()
        NewCreateAccountRequestModel.isRucEligible = false
        NewCreateAccountRequestModel.isExempted = false
        NewCreateAccountRequestModel.isVehicleAlreadyAdded = false
        NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal = false
        NewCreateAccountRequestModel.isMaxVehicleAdded = false
        NewCreateAccountRequestModel.isManualAddress = false
        NewCreateAccountRequestModel.emailSecurityCode = ""
        NewCreateAccountRequestModel.smsSecurityCode = ""
        NewCreateAccountRequestModel.password = ""
    }


    override fun initCtrl() {
        LandingActivity.showToolBar(false)
        binding.btnGuidanceAndDocuments.setOnClickListener {
            var bundle = Bundle()
            bundle.putString(Constants.API_STATE, apiState)
            bundle.putString(Constants.API_END_TIME, apiEndTime)
            requireActivity().openActivityWithData(
                RaiseEnquiryActivity::class.java, bundle
            )
//            when (apiState) {
//
//                Constants.LIVE -> {
//                    AdobeAnalytics.setActionTrack(
//                        "dart charge guidance and documents",
//                        "home",
//                        "home",
//                        "english",
//                        "home",
//                        "splash",
//                        sessionManager.getLoggedInUser()
//                    )
//
//                }
//
//                else -> {
//                    findNavController().navigate(
//                        R.id.action_landingFragment_to_serviceUnavailableFragment,
//                        getBundleData(apiState, apiEndTime)
//                    )
//                }
//            }

        }
        binding.payCrossingLayout.setOnClickListener {
            when (apiState) {
                Constants.LIVE -> {
                    AdobeAnalytics.setActionTrack(
                        "one of payment",
                        "home",
                        "home",
                        "english",
                        "home",
                        "splash",
                        sessionManager.getLoggedInUser()
                    )

                    requireActivity().startNormalActivity(MakeOffPaymentActivity::class.java)
                }

                else -> {
                    findNavController().navigate(
                        R.id.action_landingFragment_to_serviceUnavailableFragment,
                        getBundleData(apiState, apiEndTime)
                    )
                }
            }

        }
        binding.pcnLayout.setOnClickListener {
            BaseApplication.flowNameAnalytics = Constants.CREATE_ACCOUNT
            when (apiState) {
                Constants.LIVE -> {
                    AdobeAnalytics.setActionTrack(
                        "create account",
                        "home",
                        "home",
                        "english",
                        "home",
                        "splash",
                        sessionManager.getLoggedInUser()
                    )
                    requireActivity().startNormalActivity(CreateAccountActivity::class.java)
                }

                else -> {
                    findNavController().navigate(
                        R.id.action_landingFragment_to_serviceUnavailableFragment,
                        getBundleData(apiState, apiEndTime)
                    )

                }
            }


        }

        binding.crossingLayout.setOnClickListener {
            when (apiState) {
                Constants.LIVE -> {
                    AdobeAnalytics.setActionTrack(
                        "check crossings",
                        "home",
                        "home",
                        "english",
                        "home",
                        "splash",
                        sessionManager.getLoggedInUser()
                    )

                    requireActivity().startNormalActivity(
                        CheckPaidCrossingActivity::class.java
                    )
                }

                else -> {
                    findNavController().navigate(
                        R.id.action_landingFragment_to_serviceUnavailableFragment,
                        getBundleData(apiState, apiEndTime)
                    )
                }
            }

        }
        binding.btnSignIn.setOnClickListener {
            when (apiState) {
                Constants.LIVE -> {
                    AdobeAnalytics.setActionTrack(
                        "login",
                        "home",
                        "home",
                        "englsh",
                        "home",
                        "splash",
                        sessionManager.getLoggedInUser()
                    )
                    requireActivity().startNormalActivity(
                        LoginActivity::class.java
                    )
                }

                else -> {
                    findNavController().navigate(
                        R.id.action_landingFragment_to_serviceUnavailableFragment,
                        getBundleData(apiState, apiEndTime)
                    )
                }
            }

        }

        binding.receiveNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_registerReminderFragment)
        }
    }

    override fun observer() {
        observe(webServiceViewModel.webServiceLiveData, ::handleMaintenanceNotification)
        observe(webServiceViewModel.pushNotification, ::handlePushNotification)
    }

    private fun handlePushNotification(resource: Resource<EmptyApiResponse?>) {
        if (isPushNotificationChecked) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            when (resource) {
                is Resource.Success -> {
                    ErrorUtil.showError(
                        binding.root,
                        getString(R.string.push_notifications_allowed_successfully)
                    )
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
                    // do nothing
                }
            }
            isPushNotificationChecked = false
        }
    }

    private fun handleMaintenanceNotification(resource: Resource<WebSiteStatus?>) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                apiState = resource.data?.state ?: Constants.UNAVAILABLE
                apiEndTime = resource.data?.endTime ?: ""
                if (resource.data?.state == Constants.LIVE) {
                } else {
//                    findNavController().navigate(
//                        R.id.action_landingFragment_to_serviceUnavailableFragment,
//                        getBundleData(resource.data?.state,resource.data?.endTime)
//                    )
                }
            }

            is Resource.DataError -> {
                /*  if(resource.errorModel?.error!="invalid_token"){
                      apiState = Constants.UNAVAILABLE
                  }else{
                      apiState = Constants.LIVE
                  }*/

                apiState = Constants.UNAVAILABLE

//                findNavController().navigate(
//                    R.id.action_landingFragment_to_serviceUnavailableFragment,
//                    getBundleData(Constants.UNAVAILABLE)
//                )
            }

            else -> {
            }

        }


    }

    private fun getBundleData(state: String?, endTime: String? = null): Bundle {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.SERVICE_TYPE, state)
        if (endTime != null && endTime.replace("null", "").isNotEmpty()) {
            bundle.putString(Constants.END_TIME, endTime)
        }
        return bundle
    }

    private fun openUrlInWebBrowser() {
        val url = Constants.PCN_RESOLVE_URL
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            startActivity(Intent.createChooser(this, "Browse with"))
        }
    }

    override fun onRetryClick(apiUrl: String) {
        count++
        isChecked = true
        webServiceViewModel.checkServiceStatus()
    }


}