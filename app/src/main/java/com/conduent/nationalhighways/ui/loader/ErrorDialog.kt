package com.conduent.nationalhighways.ui.loader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogErrorBinding
import com.conduent.nationalhighways.ui.base.BaseDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
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
        binding.tvMessage.text = arguments?.getString(Constants.DATA)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
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
            }
        }
    }
}