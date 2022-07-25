package com.heandroid.ui.landing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.data.model.webstatus.WebSiteStatus
import com.heandroid.databinding.FragmentStartNowBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.loader.OnRetryClickListener
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.ui.startNow.contactdartcharge.ContactDartChargeActivity
import com.heandroid.ui.websiteservice.WebSiteServiceViewModel
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.setRightButtonText
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartNowFragment : BaseFragment<FragmentStartNowBinding>(), View.OnClickListener,
    OnRetryClickListener {

    private var screenType: String = ""
    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isChecked = true
    private var count = 1

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStartNowBinding {
        return FragmentStartNowBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is LandingActivity) {
            val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
            toolbar.findViewById<TextView>(R.id.btn_login).visible()
            requireActivity().setRightButtonText(getString(R.string.login))
        }
    }

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (isChecked) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            webServiceViewModel.checkServiceStatus()
        }
    }

    override fun initCtrl() {
        binding.apply {
            tvAboutService.setOnClickListener(this@StartNowFragment)
            rlAboutService.setOnClickListener(this@StartNowFragment)
            tvCrossingServiceUpdates.setOnClickListener(this@StartNowFragment)
            rlCrossingServiceUpdate.setOnClickListener(this@StartNowFragment)
            rlContactDartCharge.setOnClickListener(this@StartNowFragment)
            tvContactDartCharge.setOnClickListener(this@StartNowFragment)
            btnStartNow.setOnClickListener(this@StartNowFragment)
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

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.tv_about_service,
                R.id.rl_about_service -> {
                    screenType = Constants.ABOUT_SERVICE
                    startServicesActivity()
                }

                R.id.tv_contact_dart_charge,
                R.id.rl_contact_dart_charge -> {
                    screenType = Constants.CONTACT_DART_CHARGES
                    startContactDartChargeActivity()
                }

                R.id.tv_crossing_service_updates,
                R.id.rl_crossing_service_update -> {
                    screenType = Constants.CROSSING_SERVICE_UPDATE
                    startServicesActivity()
                }

                R.id.btn_start_now -> {
                    findNavController().navigate(R.id.action_startNow_to_landingFragment)
                }
            }
        }
    }

    private fun startServicesActivity() {
        Intent(requireActivity(), StartNowBaseActivity::class.java).run {
            putExtra(Constants.SHOW_SCREEN, screenType)
            startActivity(this)
        }
    }

    private fun startContactDartChargeActivity() {
        Intent(requireActivity(), ContactDartChargeActivity::class.java).run {
            startActivity(this)
        }
    }

    override fun onRetryClick() {
        count++
        isChecked = true
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        webServiceViewModel.checkServiceStatus()
    }
}