package com.heandroid.ui.bottomnav.notification.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.DialogFilterBinding
import com.heandroid.listener.FilterDialogListener

class FilterDialog : DialogFragment() {
    private lateinit var dataBinding: DialogFilterBinding

    companion object {

        const val TAG = "FilterDialog"

        private const val KEY_TITLE = "KEY_TITLE"
        private var mListener: FilterDialogListener? = null

        fun newInstance(title: String, listener: FilterDialogListener): FilterDialog {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            val fragment = FilterDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter, container, false)
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        setupClickListeners()
    }

    private var selOption = ""

    private fun setupClickListeners() {

        dataBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                R.id.by_time_radio_btn -> {
                    Log.v(TAG, "checked by_time_radio_btn id called")
                    selOption = "time"
                }

                R.id.by_category_radio_btn -> {
                    Log.v(TAG, "checked by_category_radio_btn id called")
                    selOption = "category"

                }
            }
        }

        dataBinding.btnClear.setOnClickListener {
            dismiss()
            mListener!!.onCancelClickedListener()
        }
        dataBinding.imvCancel.setOnClickListener {
            dismiss()
            mListener!!.onCancelClickedListener()
        }
        dataBinding.btnApply.setOnClickListener {
            dismiss()
            mListener!!.onApplyCLickListener(selOption)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dialog_background
            )
        )
    }


}