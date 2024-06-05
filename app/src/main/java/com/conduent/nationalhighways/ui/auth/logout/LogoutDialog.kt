package com.conduent.nationalhighways.ui.auth.logout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogLogoutBinding
import com.conduent.nationalhighways.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogoutDialog : BaseDialog<DialogLogoutBinding>(), View.OnClickListener {

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogLogoutBinding = DialogLogoutBinding.inflate(inflater, container, false)

    override fun init() {}

    override fun initCtrl() {
        binding.apply {
            cancelBtn.setOnClickListener(this@LogoutDialog)
            tvLogout.setOnClickListener(this@LogoutDialog)
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancel_btn -> {
                dismiss()
            }
            R.id.tvLogout -> {
                dismiss()
                mListener?.onLogOutClick()
            }
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

    companion object {
        private var mListener: OnLogOutListener? = null
        fun newInstance(
            listener: OnLogOutListener
        ): LogoutDialog {
            mListener = listener
            return LogoutDialog()
        }
    }

}