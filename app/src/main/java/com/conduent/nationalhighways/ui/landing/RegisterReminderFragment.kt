package com.conduent.nationalhighways.ui.landing

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRegisterReminderBinding
import com.conduent.nationalhighways.databinding.LocationPermissionDialogBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.setToolBarTitle
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.GeofenceUtils
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
        GeofenceUtils.startGeofence(this.requireContext())
    }

    override fun onResume() {
        super.onResume()
        if (Utils.areNotificationsEnabled(requireContext()) == false) {
            binding.switchNotification.isChecked = false
            sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION, false)
        } else {
            binding.switchNotification.isChecked =
                (sessionManager.fetchBooleanData(SessionManager.NOTIFICATION_PERMISSION))
        }

        if(Utils.checkLocationpermission(requireContext())==false){
            binding.switchGeoLocation.isChecked = false
            sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, false)
        }else{
            binding.switchGeoLocation.isChecked =
                sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION)

        }


    }

    override fun initCtrl() {

        binding.gotoStartMenuBt.setOnClickListener {
            requireActivity().startNormalActivityWithFinish(LandingActivity::class.java)
        }

        binding.switchGeoLocation.setOnClickListener {
            if (binding.switchGeoLocation.isChecked) {
                requestLocationPermission()
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


    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, true)
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, true)
            }

            else -> {
                // No location access granted.
                sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION, false)
            }
        }

        if (sessionManager.fetchBooleanData(SessionManager.LOCATION_PERMISSION) == true) {
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

    fun displayLocationAlwaysAllowPopup(
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
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
            openAppSettings()
        }
        dialog.show()


    }

    private fun openAppSettings() {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = requireActivity().getPackageName() // Replace with your app's package name
        val appSettingsUri = Uri.fromParts("package", packageName, null)
        appSettingsIntent.data = appSettingsUri
        startActivity(appSettingsIntent)
    }

    override fun observer() {

    }


}