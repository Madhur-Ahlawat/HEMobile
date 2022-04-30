package com.heandroid.ui.bottomnav.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.FragmentNotificationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.listener.FilterDialogListener
import com.heandroid.listener.NotificationItemClick
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), FilterDialogListener,
    View.OnClickListener, NotificationItemClick {

    private val viewModel: NotificationViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private val TAG = "NotificationFragment"

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, "")
        viewModel.getAlertsApi(Constants.LANGUAGE)
        binding.priority.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
        binding.standard.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
        binding.standard.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
        handleVisibility()
        binding.priority.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.priority.setOnClickListener {
            binding.standard.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            binding.priority.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
            binding.standard.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.priority.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            handleVisibility()
            setPriorityNotifications()
        }
        binding.standard.setOnClickListener {
            binding.priority.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            binding.standard.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
            binding.priority.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.standard.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.clearFilterLyt.visible()
            binding.clearSelectAllLyt.gone()
            binding.filterTxt.visible()

            setStandardNotifications()
        }

        binding.clearNotificationTxt.setOnClickListener {
            binding.clearSelectAllLyt.visible()
            binding.clearFilterLyt.gone()

        }

        binding.filterTxt.setOnClickListener {
            FilterDialog.newInstance(
                getString(R.string.str_sort),
                this
            ).show(requireActivity().supportFragmentManager, FilterDialog.TAG)
        }

    }


    private fun handleVisibility(){
        binding.clearSelectAllLyt.gone()
        binding.clearFilterLyt.visible()
        binding.filterTxt.gone()

    }
    var mTotalList = ArrayList<NotificationModel>()

    override fun initCtrl() {
    }

    override fun observer() {
        observe(viewModel.alertLivData, ::handleAlertResponse)
    }

    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.messageList?.isNullOrEmpty() == false) {
                    setPriorityNotifications()
                    //setNotificationAlert(resource.data?.messageList)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun setNotificationAlert(messageList: List<AlertMessage?>?) {

        val hashmap: MutableMap<String?, List<AlertMessage?>?>? = HashMap()

        for (element in messageList!!) {
            if (hashmap?.keys?.contains(element?.category) == true) {
                var list: MutableList<AlertMessage?>? = ArrayList<AlertMessage?>()
                list?.addAll(hashmap?.get(element?.category)!!)
                list?.add(element)
                hashmap?.put(element?.category, list)
            } else {
                hashmap?.put(element?.category, listOf(element))
            }
        }

//        hashmap.toSortedMap()


        val mAdapter = NotificationSectionAdapter(requireActivity(), hashmap)

        binding.notificationsRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = mAdapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    override fun onApplyCLickListener(cat: String) {

        setCategoryBasedNotifications()
    }

    override fun onCancelClickedListener() {
    }

    private fun setStandardNotifications(){

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




    var mNotificationAdapter: NotificationTypeAdapter? = null
    override fun onLongClick(notificationModel: NotificationModel, pos: Int) {
    }

    override fun onClick(notificationModel: NotificationModel, pos: Int) {
    }


}
