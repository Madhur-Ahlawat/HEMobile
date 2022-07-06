package com.heandroid.ui.loader

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.DialogErrorBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.startNewActivityByClearingStack
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ErrorDialog : BaseDialog<DialogErrorBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogErrorBinding = DialogErrorBinding.inflate(inflater, container, false)

    override fun init() {
        binding.tvMessage.text = arguments?.getString("message")
    }

    override fun initCtrl() {
        binding.btnOk.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnOk -> {
                dismiss()
                if (arguments?.getString("message")?.contains("Access token expired") == true) {
                    sessionManager.clearAll()
                    requireActivity().startNewActivityByClearingStack(AuthActivity::class.java)
                }
            }
        }
    }
}