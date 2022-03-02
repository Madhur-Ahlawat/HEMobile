package com.heandroid.ui.auth.logout

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.heandroid.R
import com.heandroid.data.model.auth.login.AuthResponseModel
import com.heandroid.databinding.DialogLogoutBinding
import com.heandroid.ui.auth.session.SessionActivity
import com.heandroid.ui.base.BaseDialog
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LogoutDialog : BaseDialog<DialogLogoutBinding>(), View.OnClickListener {

    private val viewModel: LogoutViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?): DialogLogoutBinding = DialogLogoutBinding.inflate(inflater,container,false)

    override fun init() {
        binding.tvTitle.text=arguments?.getString("title")
        binding.tvDes.text=arguments?.getString("desc")
    }

    override fun initCtrl() {
        binding.apply {
            tvCancel.setOnClickListener(this@LogoutDialog)
            tvLogout.setOnClickListener(this@LogoutDialog)
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.logout,::handleLogout)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvCancel ->{ dismiss() }
            R.id.tvLogout -> {
                binding.tvLogout.isEnabled=false
                viewModel.logout()
            }
        }
    }

    private fun handleLogout(status: Resource<AuthResponseModel?>?) {
        binding.tvLogout.isEnabled=true
        when (status) {
            is Resource.Success -> { dismiss()

                requireActivity().finish()
                sessionManager.clearAll()
                Intent(requireActivity(), SessionActivity::class.java).apply {
                    putExtra("screen","signout")
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(this)
                }

            }
            is Resource.DataError -> {
                dismiss()
                Toast.makeText(requireContext(),"",Toast.LENGTH_LONG).show()
            }
        }
    }
}