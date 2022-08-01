package com.heandroid.ui.auth.logout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.heandroid.R
import com.heandroid.databinding.DialogLogoutBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.common.*
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
            tvCancel.setOnClickListener(this@LogoutDialog)
            tvLogout.setOnClickListener(this@LogoutDialog)
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCancel -> {
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