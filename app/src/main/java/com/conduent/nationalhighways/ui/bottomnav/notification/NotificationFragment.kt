package com.conduent.nationalhighways.ui.bottomnav.notification

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
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
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.SwipeToDeleteCallback
import com.google.android.material.snackbar.Snackbar
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
    private var loader: LoaderDialog? = null
    private var priority_notifications: MutableList<AlertMessage> = mutableListOf()
    private var standard_notifications: MutableList<AlertMessage> = mutableListOf()
    private var numberOfAlertsTOBeCleared = 0
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)

    fun selectPriority() {
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
        initAdapter(priority_notifications)
        enableSwipeToDeleteAndUndo(priority_notifications)
//        viewModel.getAlertsApi(Constants.LANGUAGE)
    }

    fun selectStandard() {
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
        initAdapter(standard_notifications)
        enableSwipeToDeleteAndUndo(standard_notifications)
//        viewModel.getAlertsApi(Constants.LANGUAGE)
    }

    override fun init() {
        binding.feedbackToImproveMb.movementMethod = LinkMovementMethod.getInstance()
        binding.feedbackToImproveNoMb.movementMethod = LinkMovementMethod.getInstance()

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getAlertsApi(Constants.LANGUAGE)
        selectPriority()
        setClickListeners()
        initAdapter(priority_notifications)
        setBackPressListener(this)
//        binding.filterTxt.setOnClickListener {
//            FilterDialog.newInstance(
//                getString(R.string.str_sort),
//                this
//            ).show(requireActivity().supportFragmentManager, FilterDialog.TAG)
//        }

    }
    private fun enableSwipeToDeleteAndUndo(notifications: MutableList<AlertMessage>) {
        selectedNotificationsList = notifications
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val item: AlertMessage = selectedNotificationsList.get(position)
                viewModel.readAlertItem(item.cscLookUpKey ?: "")            }
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
                priority_notifications.forEach {
                    if (it.isSelectListItem == true) {
                        binding.btnClearNotification.isEnabled = false
                        binding.btnClearNotification.isFocusable = false
                        numberOfAlertsTOBeCleared++
                        viewModel.deleteAlertItem(it.cscLookUpKey ?: "")
                    }
                }
                if (numberOfAlertsTOBeCleared > 0) {
                    if (!loader!!.isVisible) {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                    }
                }
            } else {
                standard_notifications.forEach {
                    if (it.isSelectListItem == true) {
                        numberOfAlertsTOBeCleared++
                        binding.btnClearNotification.isEnabled = false
                        binding.btnClearNotification.isFocusable = false
                        viewModel.deleteAlertItem(it.cscLookUpKey ?: "")
                    }
                }
                if (numberOfAlertsTOBeCleared > 0) {
                    if (!loader!!.isVisible) {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                    }
                }
            }
        }
        binding.priority.setOnClickListener {
            selectPriority()
        }
        binding.standard.setOnClickListener {
            selectStandard()
//            setStandardNotifications()
        }
        binding.selectAll.setOnClickListener {
            if (binding.selectAll.isChecked) {
                selectAllNotification()
            } else {
                unSelectAllNotifications()
            }
        }
    }

    fun selectAllNotification() {
        if (isPrioritySelected) {
            priority_notifications.forEach { it.isSelectListItem = true }
        } else {
            standard_notifications.forEach { it.isSelectListItem = true }
        }
        mAdapter?.notifyDataSetChanged()
    }

    fun unSelectAllNotifications() {
        if (isPrioritySelected) {
            priority_notifications.forEach { it.isSelectListItem = false }
        } else {
            standard_notifications.forEach { it.isSelectListItem = false }

        }
        mAdapter?.notifyDataSetChanged()
    }


    private fun handleVisibility() {
//        binding.clearSelectAllLyt.gone()
//        binding.clearFilterLyt.visible()
//        binding.filterTxt.gone()

    }

    private var mTotalList = ArrayList<NotificationModel>()

    override fun initCtrl() {
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                for (i in 0..(priority_notifications.size - 1)) {
                    if (!priority_notifications.get(i).isSelectListItem) {
                        areAllItemsSelected = false
                        break
                    }
                }
            } else {
                for (i in 0..(standard_notifications.size - 1)) {
                    if (!standard_notifications.get(i).isSelectListItem) {
                        areAllItemsSelected = false
                        break
                    }
                }
            }


            if (!areAllItemsSelected) {
                binding.selectAll.isChecked = areAllItemsSelected
            } else {
                binding.selectAll.isChecked = areAllItemsSelected
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.messageList.isNullOrEmpty() == false) {
                    priority_notifications.clear()
                    standard_notifications.clear()
                    resource.data?.messageList?.forEach {
                        if (it?.isDeleted.equals("Y")) {

                        } else {
                            if (it?.category.equals(Constants.PRIORITY)) {
                                priority_notifications.add(it!!)
                            }
                            if (it?.category.equals(Constants.STANDARD)) {
                                standard_notifications.add(it!!)
                            }
                        }
                    }

                    priority_notifications =
                        Utils.sortAlertsDateWiseDescending(priority_notifications)
                    standard_notifications =
                        Utils.sortAlertsDateWiseDescending(standard_notifications)

                    if (isPrioritySelected) {
                        initAdapter(priority_notifications)
                    } else {
                        initAdapter(standard_notifications)
                    }


                }
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else if(resource.errorModel?.errorCode.toString().equals("1220")){
                    checkData()
                }else{
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
        if (isPrioritySelected && priority_notifications.orEmpty().size > 0) {
            binding.dataRl.visible()
            binding.noDataRl.gone()
            binding.includeNoData.noDataCl.gone()
        } else if (isStandardSelected && standard_notifications.orEmpty().size > 0) {
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
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
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
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
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
        when (v?.id) {
        }
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
