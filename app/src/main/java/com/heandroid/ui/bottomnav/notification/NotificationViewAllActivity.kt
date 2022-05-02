package com.heandroid.ui.bottomnav.notification

import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.databinding.ActivityViewallNotificationBinding
import com.heandroid.listener.NotificationItemClick
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.components.ViewWithFragmentComponent

class NotificationViewAllActivity : BaseActivity<ActivityViewallNotificationBinding>()/*, NotificationItemClick*/ {

    private var mType: Int? = null
    private lateinit var binding: ActivityViewallNotificationBinding
    private var mNotificationAdapter: NotificationViewAllAdapter? = null
    private var mList = ArrayList<AlertMessage>()
    private var loader: LoaderDialog?=null
    private val viewModel: NotificationViewAllViewModel by viewModels()

    override fun initViewBinding() {
        binding = ActivityViewallNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCtrl()
        setView()
    }

    override fun observeViewModel() {
       // observe(viewModel.alertLivData, ::handleAlertDeleteResponse)
    }

    private fun initCtrl() {
        binding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.idToolBarLyt.titleTxt.text = "Notifications"
    }

    private fun handleAlertDeleteResponse(resource: Resource<String?>?){
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if(resource.data != null)
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

    private fun setView(){
         mList = intent?.getParcelableArrayListExtra<AlertMessage?>("list") as ArrayList<AlertMessage>

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
                binding.selectAll.text = "Select all"
            } else {
                mAdapter.updateHighlight(mList, true)
                binding.selectAll.text = "Deselect all"
            }
        }

        binding.clearSelected.setOnClickListener{
            apiInitialization()
        }

       // mNotificationAdapter.
    }

    private fun apiInitialization(){
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        //loader?.show(requireActivity().supportFragmentManager, "")
//        viewModel.deleteAlertItem()
        observe(viewModel.alertLivData, ::handleAlertDeleteResponse)
    }

//    override fun onClick(notificationModel: AlertMessage?, pos: Int) {
//    }
//
//    override fun onLongClick(notificationModel: AlertMessage?, pos: Int) {
//        notificationModel?.isSelectListItem = true
//        mList[pos] = notificationModel!!
//        mNotificationAdapter?.notifyItemChanged(pos)
//    }
}