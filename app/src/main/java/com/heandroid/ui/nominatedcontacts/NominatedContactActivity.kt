package com.heandroid.ui.nominatedcontacts

import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.NominatedContactRes
import com.heandroid.databinding.ActivityNominatedContactsBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.nominatedcontacts.invitation.NominatedInvitationViewModel
import com.heandroid.ui.nominatedcontacts.list.NominatedContactListViewModel
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NominatedContactActivity : BaseActivity<ActivityNominatedContactsBinding>(), LogoutListener {

    private lateinit var binding: ActivityNominatedContactsBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager : SessionManager



    override fun initViewBinding() {
        binding = ActivityNominatedContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_nominated_contacts))

        navController = findNavController(R.id.fragmentContainerView)
        val oldGraph=navController.graph
        if(intent.getIntExtra("count",0)>0) oldGraph.startDestination=R.id.ncListFragment
        else oldGraph.startDestination=R.id.ncNoListFragment
        navController.graph=oldGraph
    }

    override fun observeViewModel() {

    }

    override fun onStart() {
        super.onStart()
        loadsession()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadsession()
    }

    private fun loadsession(){
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }
}