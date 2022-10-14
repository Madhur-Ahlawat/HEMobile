package com.conduent.nationalhighways.ui.auth.session

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogErrorBinding
import com.conduent.nationalhighways.ui.base.BaseDialog
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
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