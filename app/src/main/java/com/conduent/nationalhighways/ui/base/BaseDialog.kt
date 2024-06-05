package com.conduent.nationalhighways.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialog<B : ViewBinding> : DialogFragment() {


    private var _binding: B? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getDialogBinding(inflater, container)
        return _binding?.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initCtrl()
        observer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to avoid memory leaks
    }

    abstract fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun init()
    abstract fun initCtrl()
    abstract fun observer()

}