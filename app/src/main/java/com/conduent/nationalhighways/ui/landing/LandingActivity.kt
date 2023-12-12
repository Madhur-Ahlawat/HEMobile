package com.conduent.nationalhighways.ui.landing

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityLandingBinding
import com.conduent.nationalhighways.databinding.LocationPermissionDialogBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.FAILED_RETRY_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LANDING_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LOGOUT_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LRDS_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.SERVER_ERROR
import com.conduent.nationalhighways.utils.common.Constants.SESSION_TIME_OUT
import com.conduent.nationalhighways.utils.common.Constants.START_NOW_SCREEN
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import org.bouncycastle.util.Pack
import javax.inject.Inject


@AndroidEntryPoint
class LandingActivity : BaseActivity<ActivityLandingBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    //    private var loader: LoaderDialog? = null
    private var navFlowFrom: String = ""
    private var plateNumber: String = ""
    private var email: String = ""
    private var mobileNumber: String = ""
    private var countryCode: String = ""

    private lateinit var navController: NavController
    private var screenType: String = ""
    val viewModel: WebSiteServiceViewModel by viewModels()

    companion object {
        private lateinit var binding: ActivityLandingBinding

        fun showToolBar(isShown: Boolean) {
            if (isShown) {
                binding.toolbar.visible()
            } else {
                binding.toolbar.gone()
            }
        }

        fun setToolBarTitle(title: String) {
            binding.titleTxt.text = title
        }

    }

    override fun initViewBinding() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(this, R.id.nav_host_fragment_container)
        screenType = intent?.getStringExtra(Constants.SHOW_SCREEN).toString()
        Logg.logging("landingActivy", "test called $screenType")

        if (intent?.hasExtra(Constants.NAV_FLOW_FROM) == true) {
            navFlowFrom = intent.getStringExtra(Constants.NAV_FLOW_FROM) ?: ""
        }
        if (intent?.hasExtra(Constants.PLATE_NUMBER) == true) {
            plateNumber = intent.getStringExtra(Constants.PLATE_NUMBER) ?: ""
        }
        if (intent?.hasExtra(Constants.EMAIL) == true) {
            email = intent.getStringExtra(Constants.EMAIL) ?: ""
        }
        if (intent?.hasExtra(Constants.MOBILE_NUMBER) == true) {
            mobileNumber = intent.getStringExtra(Constants.MOBILE_NUMBER) ?: ""
        }
        if (intent?.hasExtra(Constants.COUNTRY_TYPE) == true) {
            countryCode = intent.getStringExtra(Constants.COUNTRY_TYPE) ?: ""
        }
        loadFragment(screenType)

        navControllerListener()
        if (screenType == LRDS_SCREEN) {
            binding.titleTxt.text = resources.getString(R.string.txt_my_account)
            binding.btnBack.gone()
        } else if (screenType == LOGOUT_SCREEN || screenType == SESSION_TIME_OUT) {
            binding.titleTxt.text = resources.getString(R.string.str_signed_out)
            binding.btnBack.gone()
        } else if (screenType == SERVER_ERROR || screenType == FAILED_RETRY_SCREEN) {
            binding.titleTxt.text = resources.getString(R.string.failed_problem_with_service)
            binding.btnBack.gone()
        } else {
            binding.titleTxt.text = resources.getString(R.string.failed_problem_with_service)
            binding.btnBack.visible()
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        backClickListener()

    }

    private fun backClickListener() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.btnBack.visibility == View.VISIBLE) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    // Implement your custom back navigation logic
                } else {
                    Utils.vibrate(this@LandingActivity)
                }
            }
        }

        this.onBackPressedDispatcher.addCallback(this, callback)
    }


    private fun navControllerListener() {
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {

            }
        })

    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
        initCtrl()
        sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION,Utils.areNotificationsEnabled(this))

    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    private fun initCtrl() {
    }

    override fun observeViewModel() {
    }

    private fun loadFragment(screenType: String) {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_landing)

        val bundle: Bundle = Bundle()
        if (intent.extras != null)
            oldGraph.addArgument(
                Constants.TYPE,
                NavArgument.Builder().setDefaultValue(intent.extras).build()
            )

        if (this.screenType == LRDS_SCREEN || this.screenType == SERVER_ERROR) {
            bundle.putString(Constants.SERVICE_TYPE, this.screenType)
            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
        }
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        bundle.putString(Constants.PLATE_NUMBER, plateNumber)
        bundle.putString(Constants.EMAIL, email)
        bundle.putString(Constants.MOBILE_NUMBER, mobileNumber)
        bundle.putString(Constants.COUNTRY_TYPE, countryCode)
        Log.e("TAG", "loadFragment:screenType " + screenType)
        oldGraph.apply {
            when (screenType) {
                START_NOW_SCREEN -> setStartDestination(R.id.landingFragment)
                LANDING_SCREEN -> setStartDestination(R.id.landingFragment)
                LOGOUT_SCREEN -> setStartDestination(R.id.logoutFragment)
                SESSION_TIME_OUT -> setStartDestination(R.id.sessionTimeOutFragment)
                FAILED_RETRY_SCREEN -> {
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    setStartDestination(R.id.failedRetryFragment)
                }

                LRDS_SCREEN, SERVER_ERROR -> {
                    setStartDestination(R.id.serviceUnavailableFragment)
                }
            }
        }
        navController.setGraph(oldGraph, bundle)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e(
            "TAG",
            "onRequestPermissionsResult() called with: requestCode = $requestCode, permissions = $permissions, grantResults = $grantResults"
        )


        if (requestCode == 1001) {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) ==PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )==PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )==PackageManager.PERMISSION_GRANTED){
                sessionManager.saveBooleanData(SessionManager.LOCATION_PERMISSION,true)
            }

            requestLocationPermission()
        }
    }


    private fun requestLocationPermission() {
       if (ContextCompat.checkSelfPermission(
                this,
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

        val dialog = Dialog(this)
        dialog.setCancelable(false)


        val binding: LocationPermissionDialogBinding =
            LocationPermissionDialogBinding.inflate(LayoutInflater.from(this))
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
            openAppSettings()}
        dialog.show()


    }

    private fun openAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", getPackageName(), null)
        intent.data = uri
        startActivity(intent)
    }

}


