package com.heandroid.ui.loader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.DialogErrorBinding
import com.heandroid.databinding.DialogRetryBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.startNewActivityByClearingStack
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RetryDialog : BaseDialog<DialogRetryBinding>(), View.OnClickListener {

    var listener: OnRetryClickListener? = null

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DialogRetryBinding.inflate(inflater, container, false)

    override fun init() {}

    override fun initCtrl() {
        binding.retryBtn.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.retryBtn -> {
                dismiss()
                listener?.onRetryClick()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as OnRetryClickListener
    }
}