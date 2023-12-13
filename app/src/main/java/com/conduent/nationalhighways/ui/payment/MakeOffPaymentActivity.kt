package com.conduent.nationalhighways.ui.payment

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.MakeOneOffPaymentSuccessfullyFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MakeOffPaymentActivity : BaseActivity<Any>(), LogoutListener {
    private var navHostFragment: NavHostFragment? = null
    lateinit var binding: ActivityCreateAccountBinding

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    private var data: CrossingDetailsModelsResponse? = null

    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        data = intent?.getParcelableExtra(Constants.NAV_DATA_KEY)
        setContentView(binding.root)
        init()

        AdobeAnalytics.setScreenTrack(
            "one of payment",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of payment",
            sessionManager.getLoggedInUser()
        )

    }

    private fun init() {
        NewCreateAccountRequestModel.oneOffVehiclePlateNumber = ""
        NewCreateAccountRequestModel.plateNumber = ""
        binding.toolBarLyt.titleTxt.text = getString(R.string.one_of_payment)


        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val graphInflater = navHostFragment?.navController?.navInflater
        val navGraph = graphInflater?.inflate(R.navigation.nav_graph_account_creation)
        val navController = navHostFragment?.navController
        val destination = R.id.findYourVehicleFragment
        navGraph?.setStartDestination(destination)
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAY_FOR_CROSSINGS)
        if (data != null && data is CrossingDetailsModelsResponse) {
            bundle.putString(
                Constants.PLATE_NUMBER,
                (data as CrossingDetailsModelsResponse).plateNo
            )
            bundle.putParcelable(Constants.NAV_DATA_KEY, data as CrossingDetailsModelsResponse)
        }
        navController?.setGraph(navGraph!!, bundle)


        navController?.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                if(destination.id==R.id.additionalCrossingsFragment){
                    binding.toolBarLyt.titleTxt.text = getString(R.string.additional_crossings_txt)

                }else{
                    binding.toolBarLyt.titleTxt.text = getString(R.string.one_of_payment)

                }
            }

        })

    }

    override fun observeViewModel() {}


    override fun onBackPressed() {
        navHostFragment?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is MakeOneOffPaymentSuccessfullyFragment) {

                } else {
                    navHostFragment?.findNavController()?.popBackStack()
                }

            }
        }

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        LogoutUtil.stopLogoutTimer()
//        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}