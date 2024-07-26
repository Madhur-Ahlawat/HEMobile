package com.conduent.nationalhighways.ui.landing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRegisterDailyReminderBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.GeofenceUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterDailyReminderFragment : BaseFragment<FragmentRegisterDailyReminderBinding>(),
    DropDownItemSelectListener {
    private var dailyReminderList: MutableList<String> = ArrayList()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterDailyReminderBinding =
        FragmentRegisterDailyReminderBinding.inflate(inflater, container, false)

    override fun init() {
        binding.dailyReminderDv.dataSet.clear()
        dailyReminderList.add(resources.getString(R.string.str_until_10pm))
        dailyReminderList.add(resources.getString(R.string.str_1hr_after_crossing))
        dailyReminderList.add(resources.getString(R.string.str_2hr_after_crossing))
        dailyReminderList.add(resources.getString(R.string.str_3hr_after_crossing))
        dailyReminderList.add(resources.getString(R.string.str_4hr_after_crossing))
        dailyReminderList.add(resources.getString(R.string.str_5hr_after_crossing))
        dailyReminderList.add(resources.getString(R.string.str_6hr_after_crossing))
        dailyReminderList.add(resources.getString(R.string.str_7hr_after_crossing))
        dailyReminderList.add(resources.getString(R.string.str_8hr_after_crossing))
        binding.dailyReminderDv.dataSet.addAll(dailyReminderList)
        binding.dailyReminderDv.dropDownItemSelectListener = this

//        if(sessionManager.fetchStringData(SessionManager.DAILY_REMINDER_TYPE).isNotEmpty()){
//            binding.dailyReminderDv.setSelectedValue(sessionManager.fetchStringData(SessionManager.DAILY_REMINDER_TYPE))
//        }else{
            binding.dailyReminderDv.setSelectedValue(dailyReminderList[0])
//        }


        binding.continueBt.setOnClickListener {
            Utils.startLocationService(requireContext())
            GeofenceUtils.startGeofence(this.requireContext())
            sessionManager.saveStringData(SessionManager.DAILY_REMINDER_TYPE,binding.dailyReminderDv.getSelectedDescription().toString())
            val bundle= Bundle()
            bundle.putBoolean(Constants.GEO_FENCE_NOTIFICATION,true)
            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
            findNavController().navigate(R.id.action_registerDailyReminderFragment_to_reminderStatusFragment,bundle)
        }

    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {

    }

    override fun onItemSlected(position: Int, selectedItem: String) {

    }
}

