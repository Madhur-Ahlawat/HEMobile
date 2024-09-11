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
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.landing.LandingViewModel
import com.conduent.nationalhighways.databinding.FragmentRegisterReminderBinding
import com.conduent.nationalhighways.databinding.LocationPermissionDialogBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.service.PlayLocationService
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterReminderFragment : BaseFragment<FragmentRegisterReminderBinding>() {
    private val TAG = "RegisterReminderFragmen"

    @Inject
    lateinit var sessionManager: SessionManager
    private var previousLocationPermission: Boolean = false
    private var previousNotificationPermission: Boolean = false
    private var selectedLocationPermission: Boolean = false
    private var selectedNotificationPermission: Boolean = false
    private val landingViewModel: LandingViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterReminderBinding =
        FragmentRegisterReminderBinding.inflate(inflater, container, false)

    override fun init() {
        Log.e(TAG, "init: ")
        if (requireActivity() is LandingActivity) {
            (requireActivity() as LandingActivity).showToolBar(true)
            (requireActivity() as LandingActivity).setToolBarTitle(resources.getString(R.string.str_register_to_receive_notifications))
        }


        if (arguments?.containsKey(Constants.SERVICE_RUN) == true) {
            if (arguments?.getBoolean(Constants.SERVICE_RUN) == true) {
                previousLocationPermission =
                    arguments?.getBoolean(Constants.PreviousLocationPermission) ?: false
                previousNotificationPermission =
                    arguments?.getBoolean(Constants.PreviousNotificationPermission) ?: false
                selectedLocationPermission = previousLocationPermission
                selectedNotificationPermission = previousNotificationPermission
                if (Utils.checkLocationPermission(requireContext())) {
                    binding.switchGeoLocation.isChecked = true
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


        if (arguments?.containsKey(Constants.SERVICE_RUN) == null) {
            if (!Utils.checkLocationPermission(requireContext())) {
                Log.e(TAG, "init: previousLocationPermission** " + previousLocationPermission)
                previousLocationPermission = false
                binding.alwaysDescTv.visible()
            } else {

                previousLocationPermission =
                    sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION)
                if (landingViewModel.fromReminderPage.value == true) {
                    previousLocationPermission = true
                }
                Log.e(TAG, "init: previousLocationPermission " + previousLocationPermission)
                if (previousLocationPermission) {
                    binding.alwaysDescTv.gone()
                } else {
                    binding.alwaysDescTv.visible()
                }

            }

            if (!Utils.areNotificationsEnabled(requireContext())) {
                previousNotificationPermission = false
            } else {

                binding.notificationTv.gone()
                binding.switchNotification.gone()
                binding.pushLineLl.gone()
                previousNotificationPermission =
                    sessionManager.fetchBooleanData(SessionManager.NOTIFICATION_PERMISSION)
                if (landingViewModel.fromReminderPage.value == true) {
                    previousNotificationPermission = true
                }
            }


            selectedLocationPermission = previousLocationPermission
            selectedNotificationPermission = previousNotificationPermission
        }


    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        checkNotificationGeoEnabledOrNot()

        checkContinueButton()
        landingViewModel.fromReminderPage.value = false
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

    private fun checkNotificationGeoEnabledOrNot() {
        if (!Utils.areNotificationsEnabled(requireContext())) {
            binding.switchNotification.isChecked = false
            sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION, false)
        } else {
            binding.notificationTv.gone()
            binding.switchNotification.gone()
            binding.pushLineLl.gone()
            if (arguments?.containsKey(Constants.NOTIFICATION_STATUS) == true) {
                binding.switchNotification.isChecked =
                    arguments?.getBoolean(Constants.NOTIFICATION_STATUS) ?: false
            } else {
                binding.switchNotification.isChecked =
                    selectedNotificationPermission
            }
        }


        if (!Utils.checkLocationPermission(requireContext())) {
            binding.switchGeoLocation.isChecked = false
            sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, false)
        } else {
            if (arguments?.containsKey(Constants.GEOLOCATION_STATUS) == true && arguments?.getBoolean(
                    Constants.GEOLOCATION_STATUS
                ) == true
            ) {
                binding.switchGeoLocation.isChecked =
                    true
            } else {
                binding.switchGeoLocation.isChecked =
                    selectedLocationPermission
            }

        }
        if (sessionManager.fetchBooleanData(SessionManager.NotificationSettingsClick)) {
            sessionManager.saveBooleanData(SessionManager.NotificationSettingsClick, false)
            binding.switchNotification.isChecked = Utils.areNotificationsEnabled(requireContext())
            selectedNotificationPermission = Utils.areNotificationsEnabled(requireContext())
            binding.notificationTv.visible()
            binding.switchNotification.visible()
            binding.pushLineLl.visible()
        } else if (sessionManager.fetchBooleanData(SessionManager.SettingsClick) && arguments?.containsKey(
                Constants.GpsSettings
            ) != false
        ) {
            sessionManager.saveBooleanData(SessionManager.SettingsClick, false)
            if (!Utils.checkLocationPermission(requireContext())) {
                if (!Utils.checkAccessFineLocationPermission(requireContext())) {
                    sessionManager.saveBooleanData(SessionManager.FOREGROUND_LOCATION_SHOWN, true)
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                } else {
                    displayLocationAlwaysAllowPopup()
                }

            } else {
                sessionManager.saveBooleanData(SessionManager.SettingsClick, false)
                binding.switchGeoLocation.isChecked = true
                selectedLocationPermission = true

                /*if (binding.switchGeoLocation.isChecked) {
                    startLocationServiceGeofence(1, true)
                } else {
                }*/
            }


        }
    }

    private fun visibleAlwaysDesc() {
//        if(Utils.checkLocationPermission(requireContext())){
//            binding.alwaysDescTv.gone()
//        }else{
//        }
//        binding.alwaysDescTv.visible()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataIntent)
        Log.e(
            "TAG",
            "onActivityResult() called with: requestCode = $requestCode, resultCode = $resultCode, data = $dataIntent"
        )
        if (requestCode == 1000 && dataIntent != null) {
            Log.e(TAG, "onActivityResult: " + dataIntent.data)
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
        binding.continueBt.setOnClickListener {

            if (binding.switchGeoLocation.isChecked && binding.switchNotification.isChecked) {
                findNavController().navigate(R.id.action_registerReminderFragment_to_registerDailyReminderFragment)
            } else {
                sessionManager.saveBooleanData(
                    SessionManager.NOTIFICATION_PERMISSION,
                    binding.switchNotification.isChecked
                )
                sessionManager.saveBooleanData(
                    SessionManager.LOCATION_PERMISSION,
                    binding.switchGeoLocation.isChecked
                )
                stopForeGroundService()
                val bundle = Bundle()
                bundle.putBoolean(Constants.GEO_FENCE_NOTIFICATION, false)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_registerReminderFragment_to_reminderStatusFragment,
                    bundle
                )
            }
        }

        binding.switchGeoLocation.setAccessibilityDelegate()
        binding.switchNotification.setAccessibilityDelegate()

        binding.switchGeoLocation.setOnClickListener {
            if (!binding.switchGeoLocation.isChecked) {
                visibleAlwaysDesc()
//                binding.switchNotification.isChecked = false
                selectedLocationPermission = false
                checkContinueButton()
            } else {

                if (Utils.checkLocationPermission(requireContext())) {
                    selectedLocationPermission = true
//                    startLocationServiceGeofence(3, true)
                    checkContinueButton()
                } else if (binding.switchGeoLocation.isChecked) {
                    if (sessionManager.fetchBooleanData(SessionManager.FOREGROUND_LOCATION_SHOWN)) {
                        displayLocationAlwaysAllowPopup()
                    } else {
                        requestLocationPermission()
                    }
                }
            }

        }

        binding.switchNotification.setOnClickListener {
            if (!Utils.areNotificationsEnabled(requireContext())) {
                displayCustomMessage(
                    resources.getString(R.string.str_enable_push_notification),
                    resources.getString(R.string.str_enable_push_notification_desc),
                    resources.getString(R.string.goto_settings),
                    resources.getString(R.string.enablelater_lower_case),
                    object : DialogPositiveBtnListener {
                        override fun positiveBtnClick(dialog: DialogInterface) {
                            binding.switchNotification.isChecked = true
                            sessionManager.saveBooleanData(
                                SessionManager.NotificationSettingsClick,
                                true
                            )
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
            if (!binding.switchNotification.isChecked) {
                visibleAlwaysDesc()
//                binding.switchGeoLocation.isChecked = false
                selectedNotificationPermission = false
                checkContinueButton()
            } else {
                if (Utils.areNotificationsEnabled(requireContext())) {
                    selectedNotificationPermission = true
                    checkContinueButton()
                }
            }
        }


    }

    private fun checkContinueButton() {
        if (binding.switchNotification.visibility == View.VISIBLE) {
            if (landingViewModel.fromReminderPage.value == true) {
                binding.continueBt.enable()
            } else if ((previousLocationPermission == binding.switchGeoLocation.isChecked)
                && (previousNotificationPermission == binding.switchNotification.isChecked)
            ) {
                binding.continueBt.disable()
            } else {
                if (((previousLocationPermission != binding.switchGeoLocation.isChecked) || (previousNotificationPermission != binding.switchNotification.isChecked))
                    && (binding.switchGeoLocation.isChecked == binding.switchNotification.isChecked)
                ) {
                    binding.continueBt.enable()
                } else {
                    binding.continueBt.disable()
                }
            }
        } else {
            if (landingViewModel.fromReminderPage.value == true) {
                binding.continueBt.enable()
            } else if (previousLocationPermission == binding.switchGeoLocation.isChecked) {
                binding.continueBt.disable()
            } else {
                if ((previousLocationPermission != binding.switchGeoLocation.isChecked)) {
                    binding.continueBt.enable()
                } else {
                    binding.continueBt.disable()
                }
            }
        }

    }

    private fun stopForeGroundService() {
        activity?.stopService(Intent(requireContext(), PlayLocationService::class.java))

        val manager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(123)

    }

    private fun startLocationServiceGeofence(from: Int, geoLocationStatus: Boolean = false) {
        if (from != 4) {
            val fragmentId = findNavController().currentDestination?.id
            findNavController().popBackStack(fragmentId!!, true)
            val args = Bundle()
            args.putBoolean(Constants.SERVICE_RUN, true)
            args.putBoolean(Constants.GEOLOCATION_STATUS, geoLocationStatus)
            args.putBoolean(Constants.NOTIFICATION_STATUS, binding.switchNotification.isChecked)
            args.putBoolean(Constants.PreviousLocationPermission, previousLocationPermission)
            args.putBoolean(
                Constants.PreviousNotificationPermission,
                previousNotificationPermission
            )
            findNavController().navigate(fragmentId, args)
        }

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
            Utils.checkLocationPermission(requireContext())
        )

        if (fineLocation || coarseLocation) {
//            startLocationServiceGeofence(4)
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
            displayLocationAlwaysAllowPopup()
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
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
            Utils.openAppSettings(requireActivity())
            sessionManager.saveBooleanData(SessionManager.SettingsClick, true)
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()


    }


    override fun observer() {

    }


}