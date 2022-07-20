package com.heandroid.ui.vehicle.crossinghistory.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioGroup
import com.heandroid.R
import com.heandroid.data.model.vehicle.DateRangeModel
import com.heandroid.databinding.DialogCrossingHistoryFilterBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.DatePicker
import com.heandroid.utils.DateUtils.convertDateToMonth
import com.heandroid.utils.DateUtils.currentDate
import com.heandroid.utils.DateUtils.lastPriorDate
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.ALL_TRANSACTION
import com.heandroid.utils.common.Constants.TOLL_TRANSACTION
import com.heandroid.utils.extn.isVisible
import com.heandroid.utils.onTextChanged

class CrossingHistoryFilterDialog : BaseDialog<DialogCrossingHistoryFilterBinding>(),
    View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private var dateRangeModel: DateRangeModel? = null
    private var listner: CrossingHistoryFilterDialogListener? = null

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogCrossingHistoryFilterBinding.inflate(inflater, container, false)

    override fun init() {
        loadFilter()
    }

    override fun initCtrl() {
        binding.apply {
            ivClose.setOnClickListener(this@CrossingHistoryFilterDialog)
            rgFilterOption.setOnCheckedChangeListener(this@CrossingHistoryFilterDialog)
            edFrom.setOnClickListener(this@CrossingHistoryFilterDialog)
            edTo.setOnClickListener(this@CrossingHistoryFilterDialog)
            btnApply.setOnClickListener(this@CrossingHistoryFilterDialog)
            btnClear.setOnClickListener(this@CrossingHistoryFilterDialog)
        }
    }

    override fun observer() {
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    fun setDateWithListener(
        model: DateRangeModel?,
        listener: CrossingHistoryFilterDialogListener?
    ) {
        this.listner = listener
        this.dateRangeModel = model
    }

    private fun loadFilter() {
        when (dateRangeModel?.title) {
            getString(R.string.last_30_days) -> {
                binding.rbLast30Days.isChecked = true
                setBtnEnable()
            }
            getString(R.string.last_90_days) -> {
                binding.rbLast90Days.isChecked = true
                setBtnEnable()
            }
            getString(R.string.view_all) -> {
                binding.rbViewAll.isChecked = true
                setBtnEnable()
            }
            getString(R.string.custom) -> {
                binding.rbCustom.isChecked = true
                binding.llCustom.isVisible(true)
                binding.vCustom.isVisible(false)
                binding.edFrom.setText(dateRangeModel?.from ?: "")
                binding.edTo.setText(dateRangeModel?.to ?: "")
                checkButton()
            }
            else -> {
                binding.rbViewAll.isChecked = true
                setBtnEnable()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                dismiss()
            }
            R.id.edFrom -> {
                DatePicker(binding.edFrom).show(requireActivity().supportFragmentManager, Constants.DATE_PICKER_DIALOG)
            }
            R.id.edTo -> {
                DatePicker(binding.edTo).show(requireActivity().supportFragmentManager, Constants.DATE_PICKER_DIALOG)
            }
            R.id.btnApply -> {
                dismiss()
                calculateRange()
            }

            R.id.btnClear -> {
                dismiss()
                clearSelection()
            }
        }
    }


    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {

            R.id.rbLast30Days -> {
                dateRangeModel?.title = getString(R.string.last_30_days)
                dateRangeModel?.type = TOLL_TRANSACTION
                customSectionUI(false)
                setBtnEnable()
            }
            R.id.rbLast90Days -> {
                dateRangeModel?.title = getString(R.string.last_90_days)
                dateRangeModel?.type = TOLL_TRANSACTION
                customSectionUI(false)
                setBtnEnable()
            }

            R.id.rbCustom -> {
                dateRangeModel?.title = getString(R.string.custom)
                dateRangeModel?.type = TOLL_TRANSACTION
                customSectionUI(true)
                checkButton()
                binding.edFrom.onTextChanged {
                    checkButton()
                }
                binding.edTo.onTextChanged {
                    checkButton()
                }
            }

            R.id.rbViewAll -> {
                dateRangeModel?.title = getString(R.string.view_all)
                dateRangeModel?.type = ALL_TRANSACTION
                customSectionUI(false)
                setBtnEnable()
            }

        }
    }


    private fun checkButton() {
        if (binding.edFrom.text.toString().trim().isBlank() ||
            binding.edTo.text.toString().trim().isBlank()){
            setButtonDisable()
        } else {
            setBtnEnable()
        }
    }

    private fun setBtnEnable() {
        binding.model = true
    }

    private fun setButtonDisable() {
        binding.model = false
    }


    private fun calculateRange() {
        when (binding.rgFilterOption.checkedRadioButtonId) {
            R.id.rbLast30Days -> {
                loadRange(lastPriorDate(-30), currentDate())
            }
            R.id.rbViewAll -> {
                loadRange("", "")
            }
            R.id.rbLast90Days -> {
                loadRange(lastPriorDate(-90), currentDate())
            }
            R.id.rbCustom -> {
                loadRange(convertDateToMonth(binding.edFrom.text.toString().trim()),
                    convertDateToMonth(binding.edTo.text.toString().trim()))
            }
        }
    }

    private fun loadRange(start: String?, end: String?) {
        dateRangeModel?.run {
            from = start
            to = end
        }
        listner?.onRangedApplied(dateRangeModel)
    }

    private fun clearSelection() {
        binding.rgFilterOption.clearCheck()
        binding.edFrom.text?.clear()
        binding.edTo.text?.clear()
        dateRangeModel = DateRangeModel(type = ALL_TRANSACTION, from = "", to = "", title = "")
        listner?.onClearRange(dateRangeModel)
    }


    private fun customSectionUI(isShow: Boolean) {
        binding.llCustom.isVisible(isShow)
        binding.vCustom.isVisible(!isShow)
        binding.edFrom.text?.clear()
        binding.edTo.text?.clear()
    }

}