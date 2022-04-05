package com.heandroid.ui.bottomnav.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.heandroid.utils.common.*

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(),FilterDialogListener, View.OnClickListener{

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
    }

    override fun observer() {
        observe(viewModel.alertLivData, ::handleAlertResponse)
    }

    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.messageList?.isNullOrEmpty() == false) {
                    setNotificationAlert(resource.data?.messageList)
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

        val hashmap: MutableMap<String? , List<AlertMessage?>?>? = HashMap()

        for(element in messageList!!){
            if(hashmap?.keys?.contains(element?.category)==true){
             var list : MutableList<AlertMessage?>? =  ArrayList<AlertMessage?>()
                list?.addAll(hashmap?.get(element?.category)!! )
                list?.add(element)
                hashmap?.put(element?.category, list)
            }else{
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
        when(v?.id){
        }
    }

    override fun onApplyCLickListener(cat: String) {
        binding.clearLlyt.visibility = View.GONE

       // binding.subTitle.text = "All Notifications"

       // setMultiPleViewsAdapter()
    }

    override fun onCancelClickedListener() {
        //TODO("Not yet implemented")
    }




}
