package com.conduent.nationalhighways.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRegisterDailyReminderBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDailyReminderFragment : BaseFragment<FragmentRegisterDailyReminderBinding>(),
    DropDownItemSelectListener {
    private var dailyReminderList: MutableList<String> = ArrayList()

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
        binding.dailyReminderDv.setSelectedValue(dailyReminderList[0])


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

