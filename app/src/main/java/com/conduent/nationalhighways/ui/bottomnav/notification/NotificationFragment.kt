package com.conduent.nationalhighways.ui.bottomnav.notification

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.notification.NotificationModel
import com.conduent.nationalhighways.databinding.FragmentNotificationBinding
import com.conduent.nationalhighways.listener.FilterDialogListener
import com.conduent.nationalhighways.listener.NotificationItemClick
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.notification.adapter.NotificationAdapterNew
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
import com.conduent.nationalhighways.utils.widgets.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), FilterDialogListener,
    View.OnClickListener, NotificationItemClick, BackPressListener {

    private var selectedNotificationsList: MutableList<AlertMessage> = mutableListOf()
    private var mAdapter: NotificationAdapterNew? = null
    private var isPrioritySelected: Boolean = true
    private var isStandardSelected: Boolean = false
    private var mLayoutManager: LinearLayoutManager? = null
    private val viewModel: NotificationViewModel by viewModels()
    private var priorityNotifications: MutableList<AlertMessage> = mutableListOf()
    private var standardNotifications: MutableList<AlertMessage> = mutableListOf()
    private var numberOfAlertsTOBeCleared = 0
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)

    private fun selectPriority() {
        isPrioritySelected = true
        isStandardSelected = false

        binding.priority.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_blue_bg)
        binding.standard.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_transparent_bg)
        binding.standard.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hyperlink_blue2
            )
        )
        binding.priority.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        initAdapter(priorityNotifications)
        enableSwipeToDeleteAndUndo(priorityNotifications)
//        viewModel.getAlertsApi(Constants.LANGUAGE)
    }

    private fun selectStandard() {
        isPrioritySelected = false
        isStandardSelected = true
        binding.priority.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_transparent_bg)
        binding.standard.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_blue_bg)
        binding.standard.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.priority.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hyperlink_blue2
            )
        )
        initAdapter(standardNotifications)
        enableSwipeToDeleteAndUndo(standardNotifications)
//        viewModel.getAlertsApi(Constants.LANGUAGE)
    }

    override fun init() {
        binding.feedbackToImproveMb.movementMethod = LinkMovementMethod.getInstance()
        binding.feedbackToImproveNoMb.movementMethod = LinkMovementMethod.getInstance()

        showLoaderDialog()
        viewModel.getAlertsApi(Constants.LANGUAGE)
        selectPriority()
        setClickListeners()
        initAdapter(priorityNotifications)
        setBackPressListener(this)

    }

    private fun enableSwipeToDeleteAndUndo(notifications: MutableList<AlertMessage>) {
        selectedNotificationsList = notifications
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    val item: AlertMessage = selectedNotificationsList[position]
                    viewModel.readAlertItem(item.cscLookUpKey ?: "")
                }
            }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(binding.notificationsRecyclerview)
    }

    private fun initAdapter(notifications: MutableList<AlertMessage>) {
        mLayoutManager = LinearLayoutManager(context)
        mLayoutManager!!.orientation = RecyclerView.VERTICAL
        binding.notificationsRecyclerview.layoutManager = mLayoutManager
        mAdapter =
            NotificationAdapterNew(
                this@NotificationFragment,
                notifications, viewModel
            )
        binding.notificationsRecyclerview.adapter = mAdapter
        checkData()
    }

    private fun setClickListeners() {
        binding.btnClearNotification.setOnClickListener {
            numberOfAlertsTOBeCleared = 0
            if (isPrioritySelected) {
                priorityNotifications.forEach {
                    if (it.isSelectListItem) {
                        binding.btnClearNotification.isEnabled = false
                        binding.btnClearNotification.isFocusable = false
                        numberOfAlertsTOBeCleared++
                        viewModel.deleteAlertItem(it.cscLookUpKey ?: "")
                    }
                }
                if (numberOfAlertsTOBeCleared > 0) {
                    showLoaderDialog()
                }
            } else {
                standardNotifications.forEach {
                    if (it.isSelectListItem) {
                        numberOfAlertsTOBeCleared++
                        binding.btnClearNotification.isEnabled = false
                        binding.btnClearNotification.isFocusable = false
                        viewModel.deleteAlertItem(it.cscLookUpKey ?: "")
                    }
                }
                if (numberOfAlertsTOBeCleared > 0) {
                    showLoaderDialog()
                }
            }
        }
        binding.priority.setOnClickListener {
            selectPriority()
        }
        binding.standard.setOnClickListener {
            selectStandard()
        }
        binding.selectAll.setAccessibilityDelegate()
        binding.selectAll.setOnClickListener {
            if (binding.selectAll.isChecked) {
                selectAllNotification()
            } else {
                unSelectAllNotifications()
            }

        }
    }

    private fun selectAllNotification() {
        if (isPrioritySelected) {
            priorityNotifications.forEach { it.isSelectListItem = true }
        } else {
            standardNotifications.forEach { it.isSelectListItem = true }
        }
        mAdapter?.notifyDataSetChanged()
    }

    private fun unSelectAllNotifications() {
        if (isPrioritySelected) {
            priorityNotifications.forEach { it.isSelectListItem = false }
        } else {
            standardNotifications.forEach { it.isSelectListItem = false }

        }
        mAdapter?.notifyDataSetChanged()
    }


    override fun initCtrl() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }

    override fun observer() {
        observe(viewModel.alertLivData, ::handleAlertResponse)
        observe(viewModel.dismissAlertLiveData, ::handleDismissAlertResponse)
        observe(viewModel.readAlertAlertLiveData, ::handleReadAlertResponse)
        lifecycleScope.launch {
            viewModel.notificationCheckUncheckStateFlow.collect {
                it?.let {
                    handleNotificationChecked(it)
                    viewModel.notificationCheckUncheck.emit(null)
                }
            }
        }
    }

    private fun handleNotificationChecked(alertMessage: AlertMessage) {
        if (!alertMessage.isSelectListItem) {
            binding.selectAll.isChecked = false
        } else {
            var areAllItemsSelected = true
            if (isPrioritySelected) {
                for (i in 0 until priorityNotifications.size) {
                    if (!priorityNotifications[i].isSelectListItem) {
                        areAllItemsSelected = false
                        break
                    }
                }
            } else {
                for (i in 0 until standardNotifications.size) {
                    if (!standardNotifications[i].isSelectListItem) {
                        areAllItemsSelected = false
                        break
                    }
                }
            }


            binding.selectAll.isChecked = areAllItemsSelected

        }

    }

    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                if (!resource.data?.messageList.isNullOrEmpty()) {
                    priorityNotifications.clear()
                    standardNotifications.clear()
                    resource.data?.messageList?.forEach {
                        if (it?.isDeleted.equals("Y")) {

                        } else {
                            if (it?.category.equals(Constants.PRIORITY)) {
                                priorityNotifications.add(it!!)
                            }
                            if (it?.category.equals(Constants.STANDARD)) {
                                standardNotifications.add(it!!)
                            }
                        }
                    }

                    priorityNotifications =
                        Utils.sortAlertsDateWiseDescending(priorityNotifications)
                    standardNotifications =
                        Utils.sortAlertsDateWiseDescending(standardNotifications)

                    if (isPrioritySelected) {
                        initAdapter(priorityNotifications)
                    } else {
                        initAdapter(standardNotifications)
                    }

                    val countOfY = resource.data?.messageList?.count { it?.isViewed == "Y" }
                    val countOfN = (resource.data?.messageList?.size ?: 0).minus(countOfY ?: 0)
                    if (requireActivity() is HomeActivityMain) {
                        (requireActivity() as HomeActivityMain).setBadgeCount(countOfN)
                    }

                } else {
                    if (requireActivity() is HomeActivityMain) {
                        (requireActivity() as HomeActivityMain).setBadgeCount(0)
                    }
                }


            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else if (resource.errorModel?.errorCode.toString() == "1220") {
                    checkData()
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
                // do nothing
            }
        }
    }

    private fun checkData() {
        if (isPrioritySelected) {
            binding.includeNoData.messageTv.text =
                resources.getString(R.string.str_no_priority_notifications)
        } else {
            binding.includeNoData.messageTv.text =
                resources.getString(R.string.str_no_standard_notifications)
        }
        if (isPrioritySelected && priorityNotifications.size > 0) {
            binding.dataRl.visible()
            binding.noDataRl.gone()
            binding.includeNoData.noDataCl.gone()
        } else if (isStandardSelected && standardNotifications.size > 0) {
            binding.dataRl.visible()
            binding.noDataRl.gone()
            binding.includeNoData.noDataCl.gone()
        } else {
            binding.dataRl.gone()
            binding.noDataRl.visible()
            binding.includeNoData.noDataCl.visible()
        }
    }

    private fun handleDismissAlertResponse(resource: Resource<String?>?) {
        if (numberOfAlertsTOBeCleared > 0) {
            numberOfAlertsTOBeCleared--
        }
        if (numberOfAlertsTOBeCleared == 0) {
            dismissLoaderDialog()
            binding.btnClearNotification.isEnabled = true
            binding.btnClearNotification.isFocusable = true
        }
        when (resource) {
            is Resource.Success -> {
                unSelectAllNotifications()
                viewModel.getAlertsApi(Constants.LANGUAGE)

            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                binding.notificationsRecyclerview.gone()
            }

            else -> {
                // do nothing
            }
        }
    }

    private fun handleReadAlertResponse(resource: Resource<String?>?) {
        if (numberOfAlertsTOBeCleared > 0) {
            numberOfAlertsTOBeCleared--
        }
        if (numberOfAlertsTOBeCleared == 0) {
            dismissLoaderDialog()
            binding.btnClearNotification.isEnabled = true
            binding.btnClearNotification.isFocusable = true
        }
        when (resource) {
            is Resource.Success -> {
                unSelectAllNotifications()
                viewModel.getAlertsApi(Constants.LANGUAGE)

            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                binding.notificationsRecyclerview.gone()
            }

            else -> {
                // do nothing
            }
        }
    }


    override fun onClick(v: View?) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as HomeActivityMain).showHideToolbar(true)
    }

    override fun onApplyCLickListener(cat: String) {

    }

    override fun onCancelClickedListener() {
    }


    override fun onLongClick(notificationModel: NotificationModel, pos: Int) {
    }

    override fun onClick(notificationModel: NotificationModel, pos: Int) {
    }

    override fun onBackButtonPressed() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).backPressLogic()
        }
    }


}
