package com.conduent.nationalhighways.ui.landing

import android.Manifest
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRegisterReminderBinding
import com.conduent.nationalhighways.databinding.LocationPermissionDialogBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.service.PlayLocationService
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.setToolBarTitle
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.GeofenceUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
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


        if (arguments?.containsKey("SERVICE_RUN") == true) {
            if (arguments?.getBoolean("SERVICE_RUN") == true) {
                Log.e("TAG", "init:SERVICE_RUN ")
                if (Utils.checkLocationpermission(requireContext())) {
                    Utils.startLocationService(requireContext())
                }

            }
        } else {
         /*   if (sessionManager.fetchStringData("SAVED_FILE").isEmpty()) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_TITLE, ".txt")
                startActivityForResult(intent, 1000)
            }*/
        }

    }

    override fun onResume() {
        super.onResume()

        if (Utils.isLocationServiceRunning(requireContext())) {
            Log.e("TAG", "isLocationServiceRunning-->  ")
        }

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
            ) == false) == false)
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
                    startLocationServiceGeofence(1)
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
                startLocationServiceGeofence(2)
            }
        } else {

        }

        if (sessionManager.fetchStringData("SAVED_FILE").isNotEmpty()) {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // All files access permission is granted
                    // Your code here
                } else {
                    val intent = Intent(
                        ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                    startActivityForResult(intent, 1948)
                }
            } else {
                // For versions lower than Android 11, handle permissions accordingly
                // You may request WRITE_EXTERNAL_STORAGE permission or other relevant permissions
            }*/
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataIntent)
        Log.e(
            "TAG",
            "onActivityResult() called with: requestCode = $requestCode, resultCode = $resultCode, data = $dataIntent"
        )
     if (requestCode == 1000 && dataIntent != null) {
            Log.e("TAG", "onActivityResult: " + dataIntent.data)
//            sessionManager.saveStringData("SAVED_FILE", dataIntent.data?.path ?: "")

        } else if (requestCode == 1948) {
//            val directory =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//            val file = File(directory, "dartlogs.txt")
//            if (file.exists()) {
//                  Toast.makeText(requireContext(), "file exists", Toast.LENGTH_SHORT).show()
//                Utils.writeInFile(requireContext(), "File Created")
//            } else {
//                Toast.makeText(requireContext(), "file not exists", Toast.LENGTH_SHORT).show()
//            }
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
        }

        binding.switchGeoLocation.setAccessibilityDelegate()
        binding.switchNotification.setAccessibilityDelegate()

        binding.switchGeoLocation.setOnClickListener {
            if (!binding.switchGeoLocation.isChecked) {
                stopForeGroundService()
            } else {
                if (Utils.checkLocationpermission(requireContext())) {
                    sessionManager.saveBooleanData(
                        SessionManager.LOCATION_PERMISSION,
                        binding.switchGeoLocation.isChecked
                    )
                    startLocationServiceGeofence(3)
                } else if (binding.switchGeoLocation.isChecked) {
                    if (sessionManager.fetchBooleanData(SessionManager.FOREGROUND_LOCATION_SHOWN)) {
                        showLocationServicesPopup()
                    } else {
                        requestLocationPermission()
                    }
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

    private fun stopForeGroundService() {
        Log.e("TAG", "stopForeGroundService: ")
        activity?.stopService(Intent(requireContext(), PlayLocationService::class.java))

        val manager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(123)

    }

    private fun startLocationServiceGeofence(from: Int) {
        Log.e("TAG", "startLocationServiceGeofence: " + from)
        GeofenceUtils.startGeofence(this.requireContext(), from)
        if (from != 4) {
            val fragmentId = findNavController().currentDestination?.id
            findNavController().popBackStack(fragmentId!!, true)
            val args = Bundle()
            args.putBoolean("SERVICE_RUN", true)
            findNavController().navigate(fragmentId, args)
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
            startLocationServiceGeofence(4)
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