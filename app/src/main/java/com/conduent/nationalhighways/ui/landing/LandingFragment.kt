package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
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
import com.conduent.nationalhighways.databinding.FragmentNewLandingBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.checkpaidcrossings.CheckPaidCrossingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.*
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentNewLandingBinding>(), OnRetryClickListener {

    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isChecked = true
    private var isPushNotificationChecked = true
    private var count = 1

    private var navFlow: String = Constants.ACCOUNT_CREATION_MOBILE_FLOW
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()


    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewLandingBinding {
        binding=FragmentNewLandingBinding.inflate(inflater, container, false)

        return binding
    }

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if (isChecked) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            webServiceViewModel.checkServiceStatus()
        }
        if (isPushNotificationChecked) {
            //callPushNotificationApi()
        }
        var backButton: ImageView? = requireActivity().findViewById(R.id.back_button)

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
        binding.payCrossingLayout.setOnClickListener {

            AdobeAnalytics.setActionTrack(
                "create account",
                "home",
                "home",
                "english",
                "home",
                "splash",
                sessionManager.getLoggedInUser()
            )

            requireActivity().startNormalActivity(MakeOffPaymentActivity::class.java)

        }
        binding.pcnLayout.setOnClickListener {
            AdobeAnalytics.setActionTrack(
                "one of payment",
                "home",
                "home",
                "english",
                "home",
                "splash",
                sessionManager.getLoggedInUser()
            )
            requireActivity().startNormalActivity(CreateAccountActivity::class.java)

        }

        binding.crossingLayout.setOnClickListener {
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
        binding.guidanceLayout.setOnClickListener {
            AdobeAnalytics.setActionTrack(
                "dart charge guidance and documents",
                "home",
                "home",
                "english",
                "home",
                "splash",
                sessionManager.getLoggedInUser()
            )
            findNavController().navigate(R.id.action_landingFragment_to_startNow)



        }
        binding.btnSignIn.setOnClickListener {
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
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                    // do nothing
                }
            }
            isPushNotificationChecked = false
        }
    }

    private fun handleMaintenanceNotification(resource: Resource<WebSiteStatus?>) {
        if (isChecked) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            when (resource) {
                is Resource.Success -> {
                    resource.data?.apply {
/*
                        if (!state.equals(Constants.LIVE, true) && title != null) {
                            binding.maintainanceLyt.visible()
                            binding.maintainanceTitle.text = title
                            if (message != null)
                                binding.maintainanceDesc.text = message
                        } else {
                            binding.maintainanceLyt.gone()
                        }
*/
                    }
                }
                is Resource.DataError -> {
                    if (resource.errorMsg.contains("Connect your VPN", true)) {
                        if (count > Constants.RETRY_COUNT) {
                            requireActivity().startActivity(
                                Intent(context, LandingActivity::class.java)
                                    .putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        ErrorUtil.showRetry(this)
                    } else {
                        ErrorUtil.showError(binding.root, resource.errorMsg)
                    }
                }
                else -> {
                    // do nothing
                }
            }
            isChecked = false
        }

    }

    private fun openUrlInWebBrowser() {
        val url = Constants.PCN_RESOLVE_URL
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            startActivity(Intent.createChooser(this, "Browse with"))
        }
    }

    override fun onRetryClick() {
        count++
        isChecked = true
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        webServiceViewModel.checkServiceStatus()
    }




}