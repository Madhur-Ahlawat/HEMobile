package com.heandroid.ui.vehicle.crossinghistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioGroup
import com.heandroid.R
import com.heandroid.databinding.DialogDownloadFormatSelectionBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.common.Constants

class DownloadFormatSelectionFilterDialog : BaseDialog<DialogDownloadFormatSelectionBinding>(),
    View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {

    private var listener: DownloadFilterDialogListener? = null
    private var selectionType: String = Constants.PDF

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogDownloadFormatSelectionBinding.inflate(inflater, container, false)

    override fun init() {
        binding.rbPdf.isChecked = true
    }

    override fun initCtrl() {
        binding.apply {
            ivClose.setOnClickListener(this@DownloadFormatSelectionFilterDialog)
            rgFilterOption.setOnCheckedChangeListener(this@DownloadFormatSelectionFilterDialog)
            btnApply.setOnClickListener(this@DownloadFormatSelectionFilterDialog)
            btnClear.setOnClickListener(this@DownloadFormatSelectionFilterDialog)
        }
    }

    override fun observer() {
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    fun setListener(
        listener: DownloadFilterDialogListener?
    ) {
        this.listener = listener
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                dismiss()
                listener?.let {
                    it.onCancelClicked()
                }
            }

            R.id.btnApply -> {
                dismiss()
                listener?.let {
                    it.onOkClickedListener(selectionType)
                }
            }

            R.id.btnClear -> {
                dismiss()
                listener?.let {
                    it.onCancelClicked()
                }
            }

        }
    }


    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {

            R.id.rb_pdf -> {
                selectionType = Constants.PDF
            }
            R.id.rb_spread_sheet -> {
                selectionType = Constants.SPREAD_SHEET
            }

        }
    }


}