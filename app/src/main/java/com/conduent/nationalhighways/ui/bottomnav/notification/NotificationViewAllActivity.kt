package com.conduent.nationalhighways.ui.bottomnav.notification

import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityViewallNotificationBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.notification.adapter.NotificationViewAllAdapter
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationViewAllActivity : BaseActivity<ActivityViewallNotificationBinding>(),
    LogoutListener {

    private lateinit var binding: ActivityViewallNotificationBinding
    private var mList = ArrayList<AlertMessage>()
    private val viewModel: NotificationViewAllViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    override fun initViewBinding() {
        binding = ActivityViewallNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCtrl()
        setView()
    }

    override fun observeViewModel() {
    }

    private fun initCtrl() {
        binding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.idToolBarLyt.titleTxt.text = getString(R.string.txt_notification)
    }

    private fun handleAlertDeleteResponse(resource: Resource<String?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                if (resource.data != null)
                    showToast("Item deleted")
                else
                    showToast("Item not deleted")
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
                // do nothing
            }
        }
    }

    private fun setView() {
        mList =
            intent?.getParcelableArrayListExtra<AlertMessage?>("list") as ArrayList<AlertMessage>

        val mAdapter = NotificationViewAllAdapter(this, mList)
        // mAdapter.setListener(this)

        binding.categoryTitle.text = mList[0].category

        binding.clearSelected.visibility = View.GONE

        binding.notificationsAllRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        binding.selectAll.setOnClickListener {

            if (binding.selectAll.text == "Deselect all") {
                mAdapter.updateHighlight(mList, false)
                binding.selectAll.text = getString(R.string.select_all)
            } else {
                mAdapter.updateHighlight(mList, true)
                binding.selectAll.text = getString(R.string.deselect_all)
            }
        }

        binding.clearSelected.setOnClickListener {
            apiInitialization()
        }

        // mNotificationAdapter.
    }

    private fun apiInitialization() {
        observe(viewModel.alertLivData, ::handleAlertDeleteResponse)
    }

    override fun onStart() {
        super.onStart()
        loadSession()
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
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

}