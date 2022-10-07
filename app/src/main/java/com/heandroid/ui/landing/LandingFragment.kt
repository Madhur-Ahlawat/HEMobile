package com.heandroid.ui.landing

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.webstatus.WebSiteStatus
import com.heandroid.databinding.FragmentLandingBinding
import com.heandroid.ui.account.creation.controller.CreateAccountActivity
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.checkpaidcrossings.CheckPaidCrossingActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.loader.OnRetryClickListener
import com.heandroid.ui.payment.MakeOffPaymentActivity
import com.heandroid.ui.startNow.guidancedocuments.GuidanceAndDocumentsActivity
import com.heandroid.ui.websiteservice.WebSiteServiceViewModel
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.startNormalActivity
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>(), OnRetryClickListener {

    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isChecked = true
    private var count = 1

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLandingBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (isChecked) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            webServiceViewModel.checkServiceStatus()
        }
    }

    override fun initCtrl() {
        binding.layoutCreateAccount.setOnClickListener {
            requireActivity().startNormalActivity(CreateAccountActivity::class.java)
        }
        binding.layoutMakePayment.setOnClickListener {
            requireActivity().startNormalActivity(MakeOffPaymentActivity::class.java)
        }
        binding.layoutPenaltyCharge.setOnClickListener {
            openUrlInWebBrowser()
        }
        binding.layoutPaidCrossing.setOnClickListener {
            requireActivity().startNormalActivity(
                CheckPaidCrossingActivity::class.java
            )
        }
        binding.layoutGuidance.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_startNow)
        }
        binding.btnLogin.setOnClickListener {
            requireActivity().startNormalActivity(
                AuthActivity::class.java
            )
        }
    }

    override fun observer() {
        observe(webServiceViewModel.webServiceLiveData, ::handleMaintenanceNotification)
    }

    private fun handleMaintenanceNotification(resource: Resource<WebSiteStatus?>) {
        if (isChecked) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            when (resource) {
                is Resource.Success -> {
                    resource.data?.apply {
                        if (!state.equals(Constants.LIVE, true) && title != null) {
                            binding.maintainanceLyt.visible()
                            binding.maintainanceTitle.text = title
                            if (message != null)
                                binding.maintainanceDesc.text = message
                        } else {
                            binding.maintainanceLyt.gone()
                        }
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