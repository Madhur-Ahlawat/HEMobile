package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.NotificationAdapter
import com.heandroid.adapter.VehicleListAdapter
import com.heandroid.databinding.FragmentNotificationBinding
import com.heandroid.model.NotificationModel
import com.heandroid.model.VehicleDetailsModel

class NotificationFragment : BaseFragment() {
    private lateinit var dataBinding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_notification,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()

    }


    private fun setUpViews() {

        setActionAdapter()

        dataBinding.takeAction.setOnClickListener {

            setActionAdapter()

            dataBinding.takeAction.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
            dataBinding.inOrder.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            dataBinding.others.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            dataBinding.takeAction.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            dataBinding.inOrder.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            dataBinding.others.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        }

        dataBinding.inOrder.setOnClickListener {
            dataBinding.takeAction.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            dataBinding.inOrder.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
            dataBinding.others.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            dataBinding.takeAction.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            dataBinding.inOrder.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            dataBinding.others.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            setOthersAdapter()
        }

        dataBinding.others.setOnClickListener {

            dataBinding.takeAction.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            dataBinding.inOrder.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
            dataBinding.others.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
            dataBinding.takeAction.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            dataBinding.inOrder.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            dataBinding.others.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            setOthersAdapter()
        }
    }


    private fun setActionAdapter() {


        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            1,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )
        val mModel2 = NotificationModel(
            1,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )
        val mModel3 = NotificationModel(
            1,
            "The purpose of lorem ipsum is to create a natural looking block of text (sentence, paragraph, page, etc.) that doesn't distract from the layout. A practice not without controversy,",
            "High Priority",
            "16 Dec 11:20",
            "Update card",
            "View All"
        )

        notificationList.add(mModel1)
        notificationList.add(mModel2)
        notificationList.add(mModel3)
        val mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter.setList(notificationList)
        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        dataBinding.notificationsRecyclerview.setHasFixedSize(true)
        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter


    }


    private fun setOthersAdapter() {


        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            2,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )
        val mModel2 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )
        val mModel3 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )
        val mModel4 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )
        val mModel5 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )
        val mModel6 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )
        val mModel7 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )
        val mModel8 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )

        notificationList.add(mModel1)
        notificationList.add(mModel2)
        notificationList.add(mModel3)
        notificationList.add(mModel4)
        notificationList.add(mModel5)
        notificationList.add(mModel6)
        notificationList.add(mModel7)
        notificationList.add(mModel8)
        val mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter.setList(notificationList)
        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        dataBinding.notificationsRecyclerview.setHasFixedSize(true)
        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter


    }


    private fun setInOrderAdapter() {

        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            1,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )
        val mModel2 = NotificationModel(
            1,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )

        notificationList.add(mModel1)
        notificationList.add(mModel2)
        val mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter.setList(notificationList)
        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        dataBinding.notificationsRecyclerview.setHasFixedSize(true)
        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter


    }


    private fun setHeaderAdapter() {


        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            1,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )
        val mModel2 = NotificationModel(
            1,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All"
        )

        notificationList.add(mModel1)
        notificationList.add(mModel2)
        val mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter.setList(notificationList)
        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        dataBinding.notificationsRecyclerview.setHasFixedSize(true)
        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter


    }


}
