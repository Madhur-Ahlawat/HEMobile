package com.conduent.nationalhighways.ui.bottomnav.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.notification.NotificationModel
import com.conduent.nationalhighways.databinding.FragmentNotificationBinding
import com.conduent.nationalhighways.listener.FilterDialogListener
import com.conduent.nationalhighways.listener.NotificationItemClick
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.notification.adapter.NotificationAdapterNew
import com.conduent.nationalhighways.ui.bottomnav.notification.adapter.NotificationSectionAdapter
import com.conduent.nationalhighways.ui.bottomnav.notification.adapter.NotificationTypeAdapter
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), FilterDialogListener,
    View.OnClickListener, NotificationItemClick {

    private var mAdapter: NotificationAdapterNew?=null
    private var isPrioritySelected: Boolean=true
    private var isStandardSelected: Boolean=false
    private var mLayoutManager: LinearLayoutManager?=null
    private val viewModel: NotificationViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var notifications : MutableList<AlertMessage?>?= mutableListOf()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)

    fun selectPriority() {
        isPrioritySelected=true
        isStandardSelected=false

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
        viewModel.getAlertsApi(Constants.LANGUAGE)
    }

    fun selectStandard() {
        isPrioritySelected=false
        isStandardSelected=true
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
        viewModel.getAlertsApi(Constants.LANGUAGE)
    }
    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getAlertsApi(Constants.LANGUAGE)
        selectPriority()
        setClickListeners()
        initAdapter()

//        binding.filterTxt.setOnClickListener {
//            FilterDialog.newInstance(
//                getString(R.string.str_sort),
//                this
//            ).show(requireActivity().supportFragmentManager, FilterDialog.TAG)
//        }

    }

    private fun initAdapter() {
        mLayoutManager = LinearLayoutManager(context)
        mLayoutManager!!.orientation=RecyclerView.VERTICAL
        binding.notificationsRecyclerview.layoutManager=mLayoutManager
        mAdapter=NotificationAdapterNew(requireActivity() as HomeActivityMain,notifications)
        binding.notificationsRecyclerview.adapter=mAdapter
    }

    private fun setClickListeners() {
        binding.btnClearNotification.setOnClickListener {
            notifications?.forEach {
                if(it?.isSelectListItem == true){
                    viewModel.deleteAlertItem(it.cscLookUpKey?:"")
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
            if(binding.selectAll.isChecked){
                selectAllNotification()
            }
            else{
                unSelectAllNotifications()
            }
        }
    }

    fun selectAllNotification() {
        notifications?.forEach { it?.isSelectListItem=true }
        mAdapter?.notifyDataSetChanged()
    }

    fun unSelectAllNotifications() {
        notifications?.forEach { it?.isSelectListItem=false }
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

    override fun observer() {
        observe(viewModel.alertLivData, ::handleAlertResponse)
        observe(viewModel.dismissAlertLiveData, ::handleDismissAlertResponse)
    }

    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.messageList.isNullOrEmpty() == false) {
                    notifications?.clear()
                    if (isPrioritySelected) {
                        resource.data?.messageList?.forEach {
                            if (it?.category.equals(Constants.PRIORITY)) {
                                notifications?.add(it)
                            }
                        }
                    } else if (isStandardSelected) {
                        resource.data?.messageList?.forEach {
                            if (it?.category.equals(Constants.STANDARD)) {
                                notifications?.add(it)
                            }
                        }
                    }
                    mAdapter?.notifyDataSetChanged()
                    checkData()
//                    setPriorityNotifications()
//                    setNotificationAlert(resource.data?.messageList)

                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
//                binding.noNotificationsTxt.visible()
                checkData()
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
        if (notifications.orEmpty().size > 0) {
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
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                notifications
//                if (resource.data?.messageList?.isNullOrEmpty() == false) {
//                    notifications?.clear()
//                    if(isPrioritySelected!!){
//                        resource.data?.messageList.forEach {
//                            if(it!!.category.equals(Constants.PRIORITY)){
//                                notifications!!.add(it)
//                            }
//                        }
//                    }
//                    else if(isStandardSelected!!){
//                        resource.data?.messageList.forEach {
//                            if(it!!.category.equals(Constants.STANDARD)){
//                                notifications!!.add(it)
//                            }
//                        }
//                    }
//                    mAdapter!!.notifyDataSetChanged()
////                    setPriorityNotifications()
////                    setNotificationAlert(resource.data?.messageList)
//
//                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                binding.notificationsRecyclerview.gone()
//                binding.noNotificationsTxt.visible()

            }

            else -> {
                // do nothing
            }
        }
    }

    private fun setNotificationAlert(messageList: List<AlertMessage?>?) {

        val hashmap: MutableMap<String?, List<AlertMessage?>?> = HashMap()

        for (element in messageList!!) {
            if (hashmap.keys.contains(element?.category)) {
                val list: MutableList<AlertMessage?> = ArrayList()
                hashmap[element?.category]?.let { list.addAll(it) }
                list.add(element)
                hashmap[element?.category] = list
            } else {
                hashmap[element?.category] = listOf(element)
            }
        }

//        hashmap.toSortedMap()
        if (hashmap.isNotEmpty()) {
            binding.notificationsRecyclerview.visible()
//            binding.noNotificationsTxt.gone()

            val mAdapter = NotificationSectionAdapter(requireActivity(), hashmap)

            binding.notificationsRecyclerview.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = mAdapter
            }
        } else {
            binding.notificationsRecyclerview.gone()
//            binding.noNotificationsTxt.visible()
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

//        setCategoryBasedNotifications()
    }

    override fun onCancelClickedListener() {
    }

    private fun setStandardNotifications() {

        val notificationList = ArrayList<NotificationModel>()


        val mModel15 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModel16 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModel17 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )


        notificationList.add(mModel17)
        notificationList.add(mModel16)
        notificationList.add(mModel15)


        mTotalList.clear()
        mTotalList = notificationList

        mNotificationAdapter = NotificationTypeAdapter(requireActivity())
        mNotificationAdapter?.setList(mTotalList)
        mNotificationAdapter?.setListener(this)
        binding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.notificationsRecyclerview.setHasFixedSize(true)
        binding.notificationsRecyclerview.adapter = mNotificationAdapter


    }

    private fun setPriorityNotifications() {

        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            1,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )


        val mModel3 = NotificationModel(
            1,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Priority",
            "30 Jan 11:20",
            "Nominate Contact",
            "View All", false
        )

        val mModel6 = NotificationModel(
            1,
            "culpa occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Priority",
            "16 Dec 11:20",
            "Update card",
            "View All", false
        )

        notificationList.add(mModel1)
        notificationList.add(mModel3)
        notificationList.add(mModel6)


        mTotalList.clear()
        mTotalList = notificationList

        mNotificationAdapter = NotificationTypeAdapter(requireActivity())
        mNotificationAdapter?.setList(mTotalList)
        mNotificationAdapter?.setListener(this)
        binding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.notificationsRecyclerview.setHasFixedSize(true)
        binding.notificationsRecyclerview.adapter = mNotificationAdapter

    }

    private fun setCategoryBasedNotifications() {

        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            0,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "Top-up status",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )
        val mModell1 = NotificationModel(
            2,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "Top-up status",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )
        val mModelll1 = NotificationModel(
            2,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "Top-up status",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )


        val mModel3 = NotificationModel(
            0,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment status",
            "30 Jan 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModell3 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment status",
            "30 Jan 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModelll3 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment status",
            "30 Jan 11:20",
            "Nominate Contact",
            "View All", false
        )

        val mModel7 = NotificationModel(
            0,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )


        val mModel8 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModell8 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )

        notificationList.add(mModel1)
        notificationList.add(mModell1)
        notificationList.add(mModelll1)
        notificationList.add(mModel3)
        notificationList.add(mModell3)
        notificationList.add(mModelll3)
        notificationList.add(mModel7)
        notificationList.add(mModel8)
        notificationList.add(mModell8)


        mTotalList.clear()
        mTotalList = notificationList

        mNotificationAdapter = NotificationTypeAdapter(requireActivity())
        mNotificationAdapter?.setList(mTotalList)
        mNotificationAdapter?.setListener(this)
        binding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.notificationsRecyclerview.setHasFixedSize(true)
        binding.notificationsRecyclerview.adapter = mNotificationAdapter

    }

    private var mNotificationAdapter: NotificationTypeAdapter? = null
    override fun onLongClick(notificationModel: NotificationModel, pos: Int) {
    }

    override fun onClick(notificationModel: NotificationModel, pos: Int) {
    }


}
