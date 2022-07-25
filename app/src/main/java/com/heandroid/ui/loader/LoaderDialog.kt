package com.heandroid.ui.loader

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.DialogLoaderBinding
import com.heandroid.ui.base.BaseDialog

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
        this.requireView().isFocusableInTouchMode = true
        this.requireView().requestFocus()
        this.requireView().setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }
}