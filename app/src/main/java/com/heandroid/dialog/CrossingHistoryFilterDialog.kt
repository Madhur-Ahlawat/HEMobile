
package com.heandroid.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.DialogCrossingHistoryFilterBinding
import com.heandroid.isVisible
import com.heandroid.model.DateRangeModel
import com.heandroid.utils.DateUtils
import com.heandroid.utils.DateUtils.calculateDays
import com.heandroid.utils.DateUtils.currentDate
import com.heandroid.utils.DateUtils.getRangeBetweenDate
import com.heandroid.utils.DateUtils.lastPriorDate
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.dialog_crossing_history_filter.*
import java.util.*

class CrossingHistoryFilterDialog : DialogFragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private lateinit var binding : DialogCrossingHistoryFilterBinding
    private var dateRangeModel : DateRangeModel?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= DialogCrossingHistoryFilterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init(){
        dateRangeModel = DateRangeModel(type = "", from = "",to="")
    }

    private fun initCtrl(){
        binding.apply {
            ivClose.setOnClickListener(this@CrossingHistoryFilterDialog)
            rgFilterOption.setOnCheckedChangeListener(this@CrossingHistoryFilterDialog)
            edFrom.setOnClickListener(this@CrossingHistoryFilterDialog)
            edTo.setOnClickListener(this@CrossingHistoryFilterDialog)
            btnApply.setOnClickListener(this@CrossingHistoryFilterDialog)
            btnClear.setOnClickListener(this@CrossingHistoryFilterDialog)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivClose ->{ dismiss() }
            R.id.edFrom -> { DatePicker(binding.edFrom).show(requireActivity().supportFragmentManager,"") }
            R.id.edTo -> { DatePicker(binding.edTo).show(requireActivity().supportFragmentManager,"") }
            R.id.btnApply -> { dismiss()
                               calculateRange() }
            R.id.btnClear -> { clearSelection() }
        }
    }

    private fun calculateRange() {
        when(dateRangeModel?.type){
            getString(R.string.last_30_days) ->{ loadRange(lastPriorDate(-30),currentDate()) }
            getString(R.string.view_all) ->{ loadRange("","") }
            getString(R.string.last_90_days) ->{ loadRange(lastPriorDate(-90),currentDate()) }
            getString(R.string.custom) ->{ loadRange(edFrom.text.toString(),edTo.text.toString()) }

        }
    }

    private fun loadRange(start : String?,end: String?){
        dateRangeModel?.run {
            from=start
            to=end
        }

        var listOfDateRange = getRangeBetweenDate(start,end)

    }

    private fun clearSelection() {
        binding.rgFilterOption.clearCheck()
        binding.edFrom.text?.clear()
        binding.edTo.text?.clear()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(group?.checkedRadioButtonId) {

            R.id.rbLast30Days -> { dateRangeModel?.type=binding.rbLast30Days.text.toString()
                                   customSectionUI(false) }

            R.id.rbViewAll -> { dateRangeModel?.type=binding.rbViewAll.text.toString()
                                customSectionUI(false) }

            R.id.rbLast90Days -> { dateRangeModel?.type=binding.rbLast90Days.text.toString()
                                   customSectionUI(false) }

            R.id.rbCustom -> { dateRangeModel?.type=binding.rbCustom.text.toString()
                               customSectionUI(true) }

        }
    }

    private fun customSectionUI(isShow: Boolean) {
        binding.llCustom.isVisible(isShow)
        binding.vCustom.isVisible(!isShow)
        binding.edFrom.text?.clear()
        binding.edTo.text?.clear()
    }

}