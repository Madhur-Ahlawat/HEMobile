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
import com.heandroid.utils.common.*


@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), View.OnClickListener/*, FilterDialogListener,
    NotificationItemClick*/ {
    private val viewModel: NotificationViewModel by viewModels()
    private var loader: LoaderDialog?=null
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
    }

    override fun initCtrl() {
       binding.apply {

           takeAction.setOnClickListener(this@NotificationFragment)
           inOrder.setOnClickListener(this@NotificationFragment)
           others.setOnClickListener(this@NotificationFragment)
           filterTxt.setOnClickListener(this@NotificationFragment)
           clearAll.setOnClickListener(this@NotificationFragment)
       }
    }

    override fun observer() {
        observe(viewModel.alertLivData, ::handleAlertResponse)
    }

    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.messageList?.isNullOrEmpty() == false) {
                    setNotificationAlert(resource.data.messageList)
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

    private fun setNotificationAlert(messageList: List<AlertMessage>) {
        val mAdapter = NotificationAdapter(requireActivity(),messageList)
        binding.notificationsRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = mAdapter
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.take_action -> {
                binding.apply {

                    clearLlyt.visibility = View.GONE

                  //  setActionAdapter()

                    takeAction.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
                    inOrder.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
                    others.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
                    takeAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                    inOrder.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    subTitle.text = "High Priority"
                    others.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            R.id.in_order -> {
                binding.apply {
                    clearLlyt.visibility = View.GONE

                    takeAction.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
                    inOrder.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)
                    others.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
                    takeAction.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    inOrder.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    others.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    subTitle.text = "High Priority"

                  //  setInOrderAdapter()
                }
            }

            R.id.others -> {
                binding.apply {

                    clearLlyt.visibility = View.GONE

                    takeAction.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
                    inOrder.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_unselected_bg)
                    others.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.text_selected_bg)

                    takeAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    inOrder.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    others.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    subTitle.text = "General Notifications"

                   // setOthersAdapter()

                }
            }

            R.id.filter_txt -> {
                binding.apply {
                 //   FilterDialog.newInstance(getString(R.string.str_sort), this)
                 //       .show(requireActivity().supportFragmentManager, FilterDialog.TAG)
                }
            }

            R.id.clear_all -> {

            }

        }
    }


/*
    private fun setUpViews() {

        setActionAdapter()

        dataBinding.takeAction.setOnClickListener {
            dataBinding.clearLlyt.visibility = View.GONE

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
            dataBinding.subTitle.text = "High Priority"
            dataBinding.others.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        }

        dataBinding.inOrder.setOnClickListener {
            dataBinding.clearLlyt.visibility = View.GONE

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
            dataBinding.subTitle.text = "High Priority"

            setInOrderAdapter()
        }

        dataBinding.others.setOnClickListener {

            dataBinding.clearLlyt.visibility = View.GONE

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
            dataBinding.subTitle.text = "General Notifications"

            setOthersAdapter()
        }
        dataBinding.filterTxt.setOnClickListener {

            FilterDialog.newInstance(
                getString(R.string.str_sort),
                this
            ).show(requireActivity().supportFragmentManager, FilterDialog.TAG)

        }
        dataBinding.clearAll.setOnClickListener {

        }

    }
*/


/*
    private fun setActionAdapter() {


        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            1,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All", true
        )
        val mModel2 = NotificationModel(
            1,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModel3 = NotificationModel(
            1,
            "The purpose of lorem ipsum is to create a natural looking block of text (sentence, paragraph, page, etc.) that doesn't distract from the layout. A practice not without controversy,",
            "High Priority",
            "16 Dec 11:20",
            "Update card",
            "View All", true
        )

        notificationList.add(mModel1)
        notificationList.add(mModel2)
        notificationList.add(mModel3)


//        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")

//        val result = notificationList.sortedByDescending {
//
//           // LocalDate.parse(it.date, dateTimeFormatter)
//        }

        mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter?.setList(notificationList)
        mNotificationAdapter?.setListener(this)
        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())


        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter


    }
*/

/*
    private fun setOthersAdapter() {


        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            2,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All", true
        )
        val mModel2 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModel3 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModel4 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModel5 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModel6 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModel7 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModel8 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )

        notificationList.add(mModel1)
        notificationList.add(mModel2)
        notificationList.add(mModel3)
        notificationList.add(mModel4)
        notificationList.add(mModel5)
        notificationList.add(mModel6)
        notificationList.add(mModel7)
        notificationList.add(mModel8)
        mTotalList.clear()
        mTotalList = notificationList

        mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter?.setList(mTotalList)
        mNotificationAdapter?.setListener(this)
        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        dataBinding.notificationsRecyclerview.setHasFixedSize(true)
        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter


    }
*/


/*
    private fun setMultiPleViewsAdapter() {

        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            0,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All"
        )


        val mModel2 = NotificationModel(
            1,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All", true
        )


        val mModel3 = NotificationModel(
            1,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )


        val mModel4 = NotificationModel(
            0,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Top-up Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )


        val mModel5 = NotificationModel(
            2,
            " occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Top-up Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )


        val mModel6 = NotificationModel(
            2,
            "culpa occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Top-up Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )


        val mModel7 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Top-up Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )


        val mModel8 = NotificationModel(
            0,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )


        val mModel9 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModel10 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModel11 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModel12 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Payment Status",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", true
        )
        val mModel13 = NotificationModel(
            0,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
        val mModel14 = NotificationModel(
            2,
            "proident culpa occaecat cupidatat non , sunt in culpa qui officia deserunt mollit anim id est laborum",
            "General Notifications",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )
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


        notificationList.add(mModel1)
        notificationList.add(mModel2)
        notificationList.add(mModel3)
        notificationList.add(mModel4)
        notificationList.add(mModel5)
        notificationList.add(mModel6)
        notificationList.add(mModel7)
        notificationList.add(mModel8)
        notificationList.add(mModel9)
        notificationList.add(mModel10)
        notificationList.add(mModel11)
        notificationList.add(mModel12)
        notificationList.add(mModel13)
        notificationList.add(mModel14)
        notificationList.add(mModel15)
        notificationList.add(mModel16)
        notificationList.add(mModel17)

        mTotalList.clear()
        mTotalList = notificationList

        mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter?.setList(mTotalList)
        mNotificationAdapter?.setListener(this)
        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        dataBinding.notificationsRecyclerview.setHasFixedSize(true)
        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter

    }
*/

/*
    private fun setInOrderAdapter() {

        val notificationList = ArrayList<NotificationModel>()

        val mModel1 = NotificationModel(
            2,
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur",
            "High Priority",
            "19 Dec 12:34",
            "Top Up",
            "View All", true
        )
        val mModel2 = NotificationModel(
            2,
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "High Priority",
            "16 Dec 11:20",
            "Nominate Contact",
            "View All", false
        )

        notificationList.add(mModel1)
        notificationList.add(mModel2)

        mTotalList.clear()
        mTotalList = notificationList

        mNotificationAdapter = NotificationAdapter(requireActivity())
        mNotificationAdapter?.setList(mTotalList)
        mNotificationAdapter?.setListener(this)

        dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        dataBinding.notificationsRecyclerview.setHasFixedSize(true)
        dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter


    }
*/

    /*  var mTotalList = ArrayList<NotificationModel>()

      var mNotificationAdapter: NotificationAdapter? = null
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
          mTotalList.clear()
          mTotalList = notificationList
          mNotificationAdapter = NotificationAdapter(requireActivity())
          mNotificationAdapter?.setList(mTotalList)
          mNotificationAdapter?.setListener(this)
          dataBinding.notificationsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
          dataBinding.notificationsRecyclerview.setHasFixedSize(true)
          dataBinding.notificationsRecyclerview.adapter = mNotificationAdapter

      }
  */
/*
    override fun onApplyCLickListener(cat: String) {
        dataBinding.clearLlyt.visibility = View.GONE

        dataBinding.subTitle.text = "All Notifications"

        setMultiPleViewsAdapter()
    }
*/

    /*  override fun onCancelClickedListener() {
      }
  */
/*
    override fun onLongClick(notificationModel: NotificationModel, pos: Int) {

        dataBinding.clearLlyt.visibility = View.VISIBLE

        notificationModel.iSel = true
        mTotalList[pos] = notificationModel

        mNotificationAdapter?.notifyItemChanged(pos)


    }
*/


}
