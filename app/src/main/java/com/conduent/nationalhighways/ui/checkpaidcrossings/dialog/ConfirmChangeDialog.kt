package com.conduent.nationalhighways.ui.checkpaidcrossings.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogConfirmChangeBinding
import com.conduent.nationalhighways.ui.base.BaseDialog

class ConfirmChangeDialog : BaseDialog<DialogConfirmChangeBinding>() {

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogConfirmChangeBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        arguments?.getString(KEY_TITLE)?.let {
            binding.tvTitle.setText(
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE
            )
        }
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