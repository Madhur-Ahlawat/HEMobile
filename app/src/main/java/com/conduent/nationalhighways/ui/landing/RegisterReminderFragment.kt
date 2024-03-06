package com.conduent.nationalhighways.ui.landing

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRegisterReminderBinding
import com.conduent.nationalhighways.databinding.LocationPermissionDialogBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.setToolBarTitle
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.GeofenceUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterReminderFragment : BaseFragment<FragmentRegisterReminderBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterReminderBinding =
        FragmentRegisterReminderBinding.inflate(inflater, container, false)

    override fun init() {
        showToolBar(true)
        setToolBarTitle(resources.getString(R.string.str_register_to_receive_notifications))
    }

    override fun onResume() {
        super.onResume()


        if (!Utils.areNotificationsEnabled(requireContext())) {
            binding.switchNotification.isChecked = false
            sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION, false)
        } else {
            binding.switchNotification.isChecked =
                sessionManager.fetchBooleanData(SessionManager.NOTIFICATION_PERMISSION)
        }

        if (!Utils.checkLocationpermission(requireContext())) {
            binding.switchGeoLocation.isChecked = false
            sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, false)
        } else {
            binding.switchGeoLocation.isChecked =
                sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION)
        }

        if (sessionManager.fetchBooleanData(SessionManager.SettingsClick) && ((arguments?.containsKey(
                Constants.GpsSettings
            )==false) == false)
        ) {
            sessionManager.saveBooleanData(SessionManager.SettingsClick, false)
            if (!Utils.checkLocationpermission(requireContext())) {
                if (!Utils.checkAccessFineLocationPermission(requireContext())) {
                    sessionManager.saveBooleanData(SessionManager.FOREGROUND_LOCATION_SHOWN, true)
                }
                findNavController().navigate(R.id.action_registerReminderFragment_to_gpsSettingsFragment)
            } else {
                sessionManager.saveBooleanData(SessionManager.SettingsClick, false)

                sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, true)
                binding.switchGeoLocation.isChecked =
                    sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION)
                if (binding.switchGeoLocation.isChecked) {
                    GeofenceUtils.startGeofence(this.requireContext())
                }
            }
        } else if (arguments?.containsKey(Constants.GpsSettings) == true) {
            sessionManager.saveBooleanData(SessionManager.SettingsClick, false)
            val fromGpsSettings = arguments?.getBoolean(Constants.GpsSettings) ?: false

            if (fromGpsSettings) {
                sessionManager.saveBooleanData(
                    SessionManager.LOCATION_PERMISSION,
                    Utils.checkLocationpermission(requireContext())
                )
                binding.switchGeoLocation.isChecked =
                    sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION)
            }
            if (binding.switchGeoLocation.isChecked) {
                GeofenceUtils.startGeofence(this.requireContext())
            }
        }else{

        }

    }

    override fun initCtrl() {
        binding.gotoStartMenuBt.setOnClickListener {
            requireActivity().startNormalActivityWithFinish(LandingActivity::class.java)
        }
        binding.switchGeoLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                GeofenceUtils.startGeofence(this.requireContext())
            }
            binding.switchGeoLocation.contentDescription = if (isChecked) {
                "${resources.getString(R.string.accessibility_on)} ${binding.switchGeoLocation.text}"
            } else {
                "${resources.getString(R.string.accessibility_off)} ${binding.switchGeoLocation.text}"
            }
        }
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            binding.switchNotification.contentDescription = if (isChecked) {
                "${resources.getString(R.string.accessibility_on)} ${binding.switchNotification.text}"
            } else {
                "${resources.getString(R.string.accessibility_off)} ${binding.switchNotification.text}"
            }
        }
        binding.switchGeoLocation.setOnClickListener {
            if (Utils.checkLocationpermission(requireContext())) {
                sessionManager.saveBooleanData(
                    SessionManager.LOCATION_PERMISSION,
                    binding.switchGeoLocation.isChecked
                )
            } else if (binding.switchGeoLocation.isChecked) {
                if (sessionManager.fetchBooleanData(SessionManager.FOREGROUND_LOCATION_SHOWN)) {
                    showLocationServicesPopup()
                } else {
                    requestLocationPermission()
                }
            }
        }

        binding.switchNotification.setOnClickListener {
            if (Utils.areNotificationsEnabled(requireContext())) {
                sessionManager.saveBooleanData(
                    SessionManager.NOTIFICATION_PERMISSION,
                    binding.switchNotification.isChecked
                )
            }
            if (!Utils.areNotificationsEnabled(requireContext())) {
                displayCustomMessage(
                    resources.getString(R.string.str_enable_push_notification),
                    resources.getString(R.string.str_enable_push_notification_desc),
                    resources.getString(R.string.enablenow_lower_case),
                    resources.getString(R.string.enablelater_lower_case),
                    object : DialogPositiveBtnListener {
                        override fun positiveBtnClick(dialog: DialogInterface) {
                            binding.switchNotification.isChecked = true
                            Utils.redirectToNotificationPermissionSettings(requireContext())
                        }
                    },
                    object : DialogNegativeBtnListener {
                        override fun negativeBtnClick(dialog: DialogInterface) {
                            binding.switchNotification.isChecked = false
                            sessionManager.saveBooleanData(
                                SessionManager.LOCATION_PERMISSION,
                                false
                            )
                        }
                    },
                    View.VISIBLE
                )
            }
        }


    }

    private fun showLocationServicesPopup() {
        displayCustomMessage(
            resources.getString(R.string.str_enable_location_services),
            resources.getString(R.string.str_enable_location_services_desc),
            getString(R.string.enablenow_lower_case),
            getString(R.string.enablelater_lower_case),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    dialog.dismiss()
                    sessionManager.saveBooleanData(SessionManager.SettingsClick, true)
                    Utils.openAppSettings(requireActivity())
                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    sessionManager.saveBooleanData(
                        SessionManager.FOREGROUND_LOCATION_SHOWN,
                        true
                    )
                    binding.switchGeoLocation.isChecked = false
                    dialog.dismiss()
                }
            })

    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var fineLocation = false
        var coarseLocation = false
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                fineLocation = true
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                coarseLocation = true
            }

            else -> {
                sessionManager.saveBooleanData(
                    SessionManager.FOREGROUND_LOCATION_SHOWN,
                    true
                )
                // No location access granted.
            }
        }

        sessionManager.saveBooleanData(
            SessionManager.LOCATION_PERMISSION,
            Utils.checkLocationpermission(requireContext())
        )

        if (fineLocation || coarseLocation) {
            GeofenceUtils.startGeofence(this.requireContext())
            requestBackgroundLocationPermission()
        }
    }


    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                displayLocationAlwaysAllowPopup()
            }
        }
    }

    private fun displayLocationAlwaysAllowPopup(
    ) {

        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)


        val binding: LocationPermissionDialogBinding =
            LocationPermissionDialogBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)

        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.

        binding.cancelBtn.setOnClickListener {
            sessionManager.saveBooleanData(
                SessionManager.FOREGROUND_LOCATION_SHOWN,
                true
            )
            this.binding.switchGeoLocation.isChecked = false
            sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, false)
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
            Utils.openAppSettings(requireActivity())
            sessionManager.saveBooleanData(SessionManager.SettingsClick, true)
            dialog.dismiss()
        }
        dialog.show()


    }


    override fun observer() {

    }


}