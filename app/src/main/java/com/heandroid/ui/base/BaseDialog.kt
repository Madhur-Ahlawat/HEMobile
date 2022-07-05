package com.heandroid.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialog<B: ViewBinding> : DialogFragment() {

    protected lateinit var binding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=getDialogBinding(inflater, container)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
        observer()
    }

    abstract fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun init()
    abstract fun initCtrl()
    abstract fun observer()

}