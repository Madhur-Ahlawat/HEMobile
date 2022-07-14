package com.heandroid.ui.account.profile

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.heandroid.R
import com.heandroid.databinding.ActivityProfileBinding
import com.heandroid.ui.base.BaseActivity
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

    private var accountType: String?= Constants.PERSONAL_ACCOUNT
    private var isSecondaryUser: Boolean = false
    private lateinit var binding: ActivityProfileBinding

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var navController: NavController

    override fun initViewBinding() {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_account_management))
        navController = findNavController(R.id.fragmentContainerView)
        accountType = sessionManager.getAccountType()
        isSecondaryUser = sessionManager.getSecondaryUser()
        setFragmentInView()
    }

    override fun observeViewModel() {
    }

    override fun onStart() {
        super.onStart()
        loadSession()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession(){
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }
    private fun setFragmentInView() {
        val navGraph = navController.graph

        when  {
          ! isSecondaryUser && accountType== Constants.PERSONAL_ACCOUNT -> {// oldGraph.startDestination = R.id.viewProfile }
              navGraph.setStartDestination(R.id.viewProfile)
              val startDestId = R.id.viewProfile
              navGraph.apply {
                  setStartDestination(startDestId);
              }

          }
           !isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT ->
           {
               navGraph.setStartDestination(R.id.viewBusinessAccountProfileFragment)
           }

            isSecondaryUser && accountType ==Constants.PERSONAL_ACCOUNT->{
                navGraph.setStartDestination(R.id.viewNominatedUserAccountProfileFragment)
            }
            isSecondaryUser && accountType == Constants.BUSINESS_ACCOUNT->{
                Log.d("nomi","nated")
                navGraph.setStartDestination(R.id.viewNominatedUserAccountProfileFragment)
            }
         else->{   navGraph.setStartDestination(R.id.viewPayGAccountProfileFragment) }
        }
        navController.graph = navGraph
    }


   }