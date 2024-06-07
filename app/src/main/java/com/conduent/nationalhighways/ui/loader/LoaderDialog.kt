package com.conduent.nationalhighways.ui.loader

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.DialogLoaderBinding
import com.conduent.nationalhighways.ui.base.BaseDialog

class LoaderDialog : BaseDialog<DialogLoaderBinding>() {
    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogLoaderBinding = DialogLoaderBinding.inflate(inflater, container, false)

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onResume() {
        super.onResume()
        val view = this.requireView()
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    override fun onPause() {
        super.onPause()
        cleanupResources()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        cleanupResources()
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        cleanupResources()
    }

    private fun cleanupResources() {
        this.requireView().setOnKeyListener(null) // Remove the key listener to avoid leaks
    }
}