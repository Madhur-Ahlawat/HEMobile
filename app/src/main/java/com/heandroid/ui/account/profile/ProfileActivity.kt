package com.heandroid.ui.account.profile

import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityProfileBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    public fun showLoader(){
        loader?.show(supportFragmentManager, Constants.LOADER_DIALOG)

    }
    public fun hideLoader(){
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
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }

    private fun setFragmentInView() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val oldGraph = navController.navInflater.inflate(R.navigation.navigation_profile)


        when {
            !isSecondaryUser && accountType == Constants.PERSONAL_ACCOUNT -> {
                oldGraph.setStartDestination(R.id.viewProfile)
            }

            !isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT -> {
                oldGraph.setStartDestination(R.id.viewBusinessAccountProfileFragment)
            }

            isSecondaryUser && accountType == Constants.PERSONAL_ACCOUNT -> {
                oldGraph.setStartDestination(R.id.viewNominatedUserAccountProfileFragment)
            }
            isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT -> {
                oldGraph.setStartDestination(R.id.viewNominatedUserAccountProfileFragment)
            }
            else -> {
                oldGraph.setStartDestination(R.id.viewPayGAccountProfileFragment)
            }

        }

        navController.graph = oldGraph

    }

}