package com.heandroid.ui.auth.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.heandroid.R
import com.heandroid.data.model.auth.login.AuthResponseModel
import com.heandroid.databinding.DialogLogoutBinding
import com.heandroid.ui.base.BaseDialog
import com.heandroid.ui.landing.LandingActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LogoutDialog : BaseDialog<DialogLogoutBinding>(), View.OnClickListener {

    private val viewModel: LogoutViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    private var loader: LoaderDialog? = null

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogLogoutBinding = DialogLogoutBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.tvTitle.text = arguments?.getString("title")
        binding.tvDes.text = arguments?.getString("desc")
    }

    override fun initCtrl() {
        binding.apply {
            tvCancel.setOnClickListener(this@LogoutDialog)
            tvLogout.setOnClickListener(this@LogoutDialog)
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.logout, ::handleLogout)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCancel -> {
                dismiss()
            }
            R.id.tvLogout -> {
                binding.tvLogout.isEnabled = false
                loader?.show(requireActivity().supportFragmentManager, "loader")
                viewModel.logout()
            }
        }
    }

    private fun handleLogout(status: Resource<AuthResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                openLogoutScreen()
            }
            is Resource.DataError -> {
                dismiss()
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {  }
        }
    }

    private fun openLogoutScreen() {
        sessionManager.clearAll()
        dismiss()
        Intent(requireActivity(), LandingActivity::class.java).apply {
            putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

    }
}