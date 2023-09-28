package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.databinding.FragmentAboutThisServiceBinding
import com.conduent.nationalhighways.databinding.FragmentGuidanceAndDocumentsBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
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
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutThisServiceFragment : BaseFragment<FragmentAboutThisServiceBinding>(), OnRetryClickListener {

    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isChecked = false
    private var isPushNotificationChecked = true
    private var count = 1
    var apiState = Constants.UNAVAILABLE
    var apiEndTime: String = ""

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAboutThisServiceBinding {
        binding = FragmentAboutThisServiceBinding.inflate(inflater, container, false)

        return binding
    }

    override fun init() {
        LandingActivity.setToolBarTitle("About this Service")
        LandingActivity.showToolBar(true)
        HomeActivityMain.accountDetailsData=null
        HomeActivityMain.checkedCrossing=null
        HomeActivityMain.crossing=null
        HomeActivityMain.dateRangeModel=null
        HomeActivityMain.paymentHistoryListData=null
        HomeActivityMain.paymentHistoryListDataCheckedCrossings= arrayListOf()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if (!isChecked) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            webServiceViewModel.checkServiceStatus()
        }
        isChecked = true
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

    }

    private fun callPushNotificationApi() {
        sessionManager.getFirebaseToken()?.let { firebaseToken ->
            val request = PushNotificationRequest(
                deviceToken = firebaseToken,
                osName = PushNotificationUtils.getOSName(),
                osVersion = PushNotificationUtils.getOSVersion(),
                appVersion = PushNotificationUtils.getAppVersion(requireContext()),
                optInStatus = "Y"
            )
            webServiceViewModel.allowPushNotification(request)
        }
    }

    override fun initCtrl() {
        binding.btnLearnMore.setOnClickListener {
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
                    ErrorUtil.showError(binding.root, "Push notifications allowed successfully")
                }

                is Resource.DataError -> {
                    if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                        displaySessionExpireDialog()
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

    private fun getBundleData(state: String?,endTime:String?=null): Bundle? {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.SERVICE_TYPE, state)
        if(endTime!=null && endTime.replace("null","").isNotEmpty()){
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
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        webServiceViewModel.checkServiceStatus()
    }


}