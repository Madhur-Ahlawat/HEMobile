package com.heandroid.ui.checkpaidcrossings.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.heandroid.R
import com.heandroid.databinding.DialogConfirmChangeBinding
import com.heandroid.databinding.DialogDeleteVehicleGroupBinding
import com.heandroid.ui.base.BaseDialog

class ConfirmChangeDialog : BaseDialog<DialogConfirmChangeBinding>() {

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogConfirmChangeBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun initCtrl() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            dismiss()
            mListener?.onConfirmClick()
        }
    }

    override fun observer() {}

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private var mListener: ConfirmChangeListener? = null

        fun newInstance(
            title: String,
            listener: ConfirmChangeListener
        ): ConfirmChangeDialog {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            val fragment = ConfirmChangeDialog()
            fragment.arguments = args
            return fragment
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