package com.heandroid.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.DialogDownloadFormatSelectionBinding
import com.heandroid.listener.DownloadFilterDialogListener
import com.heandroid.model.DateRangeModel
import com.heandroid.utils.Constants

class DownloadFormatSelectionFilterDialog : DialogFragment(), View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {

    private lateinit var binding: DialogDownloadFormatSelectionBinding
    private var listener: DownloadFilterDialogListener? = null
    private var selectionType: String = Constants.PDF

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogDownloadFormatSelectionBinding.inflate(inflater, container, false)
        return binding.root
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init() {
        binding.rbPdf.isChecked = true
    }


    private fun initCtrl() {
        binding.apply {
            ivClose.setOnClickListener(this@DownloadFormatSelectionFilterDialog)
            rgFilterOption.setOnCheckedChangeListener(this@DownloadFormatSelectionFilterDialog)
            btnApply.setOnClickListener(this@DownloadFormatSelectionFilterDialog)
            btnClear.setOnClickListener(this@DownloadFormatSelectionFilterDialog)
        }
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