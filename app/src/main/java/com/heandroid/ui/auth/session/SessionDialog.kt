package com.heandroid.ui.auth.session

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.DialogErrorBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.ui.landing.LandingActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Utils
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionDialog : BaseDialog<DialogErrorBinding>(), View.OnClickListener {

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogErrorBinding = DialogErrorBinding.inflate(inflater, container, false)

    override fun init() {
        binding.tvMessage.text = getString(R.string.your_session_has_expired)
    }

    override fun initCtrl() {
        binding.apply {
            btnOk.setOnClickListener(this@SessionDialog)
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnOk -> {
                dismiss()
                requireActivity().finish()
                requireActivity().startActivity(
                    Intent(context, LandingActivity::class.java)
                        .putExtra(Constants.SHOW_SCREEN, Constants.SESSION_TIME_OUT)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Constants.TYPE, Constants.LOGIN)
                )
            }
        }
    }
}