package com.conduent.nationalhighways.ui.loader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogRetryBinding
import com.conduent.nationalhighways.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetryDialog(val api_URL: String) : BaseDialog<DialogRetryBinding>(), View.OnClickListener {

    var listener: OnRetryClickListener? = null

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DialogRetryBinding.inflate(inflater, container, false)

    override fun init() {

    }

    override fun initCtrl() {
        binding.retryBtn.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.retryBtn -> {
                dismiss()
                listener?.onRetryClick(apiUrl =api_URL )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnRetryClickListener
        } catch (e: ClassCastException) {
        }
    }
}