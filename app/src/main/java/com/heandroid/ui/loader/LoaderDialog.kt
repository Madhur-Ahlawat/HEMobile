package com.heandroid.ui.loader

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.DialogLoaderBinding
import com.heandroid.ui.base.BaseDialog

class LoaderDialog : BaseDialog<DialogLoaderBinding>() {
    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?): DialogLoaderBinding = DialogLoaderBinding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}