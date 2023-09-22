package com.conduent.nationalhighways.ui.account.profile

import androidx.fragment.app.DialogFragment
import com.conduent.nationalhighways.databinding.ActivityProfileBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.toolbar
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.conduent.nationalhighways.R


@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>(), LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private var accountType: String? = Constants.PERSONAL_ACCOUNT
    private var isSecondaryUser: Boolean = false
    private lateinit var binding: ActivityProfileBinding
    private var loader: LoaderDialog? = null


    override fun initViewBinding() {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_account_management))
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        accountType = sessionManager.getAccountType()
        isSecondaryUser = sessionManager.getSecondaryUser()
        setFragmentInView()
    }

    override fun observeViewModel() {}

    override fun onStart() {
        super.onStart()
        loadSession()
    }

    fun showLoader() {

        loader?.show(supportFragmentManager, Constants.LOADER_DIALOG)

    }

    fun hideLoader() {
        if (loader?.isVisible == true) {
            loader?.dismiss()
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
        Utils.sessionExpired(this, this, sessionManager)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

    private fun setFragmentInView() {

        val navController: NavController =
            Navigation.findNavController(this, R.id.fragmentContainerView)
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_profile)

        when {
            !isSecondaryUser && accountType == Constants.PERSONAL_ACCOUNT -> {
                navGraph.setStartDestination(R.id.viewProfile)
            }

            !isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT -> {
                navGraph.setStartDestination(R.id.viewBusinessAccountProfileFragment)
            }

            isSecondaryUser && accountType == Constants.PERSONAL_ACCOUNT -> {
                navGraph.setStartDestination(R.id.viewNominatedUserAccountProfileFragment)
            }
            isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT -> {
                navGraph.setStartDestination(R.id.viewNominatedUserAccountProfileFragment)
            }
            else -> {
                navGraph.setStartDestination(R.id.viewPayGAccountProfileFragment)
            }

        }

        navController.graph = navGraph

    }

}