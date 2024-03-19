package com.conduent.nationalhighways.ui.account.creation.controller

import android.view.accessibility.AccessibilityEvent
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class CreateAccountActivity : BaseActivity<Any>(), LogoutListener {
    lateinit var binding: ActivityCreateAccountBinding

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_create_an_account)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        AdobeAnalytics.setScreenTrack(
            "create account",
            "create account",
            "english",
            "create account",
            "home",
            "create account",
            sessionManager.getLoggedInUser()
        )

    }
    fun focusToolBarCreateAccount() {
        binding.toolBarLyt.backButton.requestFocus() // Focus on the backButton
        binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)

        val task = Runnable {
            binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            binding.toolBarLyt.backButton.contentDescription = getString(R.string.str_back)
        }
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        worker.schedule(task, 1, TimeUnit.SECONDS)
    }
    override fun observeViewModel() {}

    override fun onPostResume() {
        super.onPostResume()
        binding.toolBarLyt.backButton.requestFocus() // Focus on the backButton
        binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)

        val task = Runnable {
            binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        worker.schedule(task, 1, TimeUnit.SECONDS)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is AccountSuccessfullyCreationFragment) {

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
//        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}

