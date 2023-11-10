package com.conduent.nationalhighways.ui.auth.controller

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.ReplenishmentInformation
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityAuthBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.auth.suspended.AccountSuspendReOpenFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseActivity<Any?>(),LogoutListener{

    private lateinit var binding: ActivityAuthBinding
    public var previousScreen = "home"
    private lateinit var navController: NavController
    private var navFlow: String = ""
    private var currentBalance: String = ""
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var crossingCount: String = ""
    private var navFlowFrom: String = ""


    @Inject
    lateinit var api: ApiService
    @Inject
    lateinit var sessionManager: SessionManager
    override fun initViewBinding() {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.getStringExtra(Constants.NAV_FLOW_KEY) != null) {
            navFlow = intent.getStringExtra(Constants.NAV_FLOW_KEY) ?: ""

        }
        if(intent.hasExtra(Constants.NAV_FLOW_FROM)){
            navFlowFrom=intent.getStringExtra(Constants.NAV_FLOW_FROM)?:""
        }
        if (intent.getParcelableExtra<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                intent.getParcelableExtra<PersonalInformation>(Constants.PERSONALDATA)

        }
        if (intent.getParcelableExtra<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                intent.getParcelableExtra<AccountInformation>(Constants.ACCOUNTINFORMATION)
        }

        if (intent.getParcelableExtra<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION) != null) {
            replenishmentInformation =
                intent.getParcelableExtra<ReplenishmentInformation>(Constants.REPLENISHMENTINFORMATION)
        }

        crossingCount = intent.getStringExtra(Constants.CROSSINGCOUNT) ?: ""
        currentBalance = intent.getStringExtra(Constants.CURRENTBALANCE) ?: ""



        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        previousScreen = if (intent.getIntExtra(
                Constants.FROM_DART_CHARGE_FLOW,
                0
            ) == Constants.DART_CHARGE_FLOW_CODE
        ) {
            "contact dart charge"
        } else {
            "home"

        }


        AdobeAnalytics.setScreenTrack(
            "login",
            "login",
            "english",
            "login",
            previousScreen,
            "login",
            sessionManager.getLoggedInUser()
        )
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_auth)

        val bundle = Bundle()
        if (navFlow == Constants.FORGOT_PASSWORD_FLOW) {
            binding.toolBarLyt.titleTxt.text = getString(R.string.forgot_password)

            graph.setStartDestination(R.id.forgotPasswordFragment)
            bundle.putString(Constants.NAV_FLOW_KEY, Constants.FORGOT_PASSWORD_FLOW)

        } else if (navFlow == Constants.TWOFA) {
            bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
            bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
            binding.toolBarLyt.titleTxt.text = getString(R.string.str_sign_in_validation)
            graph.setStartDestination(R.id.chooseOptionFragment)


        } else if (navFlow == Constants.SUSPENDED) {

            binding.toolBarLyt.titleTxt.text = getString(R.string.str_account_suspended)
            bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            bundle.putString(Constants.CURRENTBALANCE, currentBalance)
            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
            bundle.putString(Constants.CROSSINGCOUNT,crossingCount)
            graph.setStartDestination(R.id.accountSuspendedFragment)


        }else if (navFlow==Constants.PAYMENT_TOP_UP){
            binding.toolBarLyt.titleTxt.text = getString(R.string.top_up)
            bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)



            graph.setStartDestination(R.id.accountSuspendedPaymentFragment)
        }

        navController = navHostFragment.navController
        navController.setGraph(graph, bundle)

    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)

    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    override fun observeViewModel() {}
    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is AccountSuspendReOpenFragment) {

                } else {
                    onBackPressedDispatcher.onBackPressed()
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
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager,api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}