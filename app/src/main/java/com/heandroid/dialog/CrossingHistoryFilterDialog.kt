
package com.heandroid.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.DialogCrossingHistoryFilterBinding
import com.heandroid.isVisible
import com.heandroid.listener.CrossingHistoryFilterDialogListener
import com.heandroid.model.DateRangeModel
import com.heandroid.utils.Constants
import com.heandroid.utils.Constants.ALL_TRANSACTION
import com.heandroid.utils.Constants.TOLL_TRANSACTION
import com.heandroid.utils.DateUtils.currentDate
import com.heandroid.utils.DateUtils.lastPriorDate

class CrossingHistoryFilterDialog : DialogFragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private lateinit var binding : DialogCrossingHistoryFilterBinding
    private var dateRangeModel : DateRangeModel?=null
    private var listner: CrossingHistoryFilterDialogListener?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= DialogCrossingHistoryFilterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    fun setListener(listener: CrossingHistoryFilterDialogListener?)
    {
        this.listner = listener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init(){
        dateRangeModel = DateRangeModel(type = "", from = "",to="")
    }

    private fun initCtrl() {
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
        when(v?.id) {

            R.id.ivClose ->{ dismiss() }
            R.id.edFrom -> { DatePicker(binding.edFrom).show(requireActivity().supportFragmentManager,"") }
            R.id.edTo -> { DatePicker(binding.edTo).show(requireActivity().supportFragmentManager,"") }
            R.id.btnApply -> { dismiss()
                               calculateRange() }
            R.id.btnClear -> { clearSelection() }

        }
    }


    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(group?.checkedRadioButtonId) {

            R.id.rbLast30Days, R.id.rbLast90Days,R.id.rbCustom -> { dateRangeModel?.type=TOLL_TRANSACTION
                                                                    customSectionUI(false) }

            R.id.rbViewAll -> { dateRangeModel?.type=ALL_TRANSACTION
                                customSectionUI(false) }

        }
    }


    private fun calculateRange() {
        when(binding.rgFilterOption.checkedRadioButtonId){
            R.id.rbLast30Days ->{ loadRange(lastPriorDate(-30),currentDate()) }
            R.id.rbViewAll ->{ loadRange("","")  }
            R.id.rbLast90Days ->{ loadRange(lastPriorDate(-90),currentDate()) }
            R.id.custom ->{ loadRange(binding.edFrom.text.toString(),binding.edTo.text.toString()) }
        }
    }

    private fun loadRange(start : String?,end: String?){
        dateRangeModel?.run {
            from=start
            to=end
        }
        listner?.onRangedApplied(dateRangeModel)
    }

    private fun clearSelection() {
        binding.rgFilterOption.clearCheck()
        binding.edFrom.text?.clear()
        binding.edTo.text?.clear()
    }


    private fun customSectionUI(isShow: Boolean) {
        binding.llCustom.isVisible(isShow)
        binding.vCustom.isVisible(!isShow)
        binding.edFrom.text?.clear()
        binding.edTo.text?.clear()
    }

}