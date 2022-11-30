package com.conduent.nationalhighways.ui.auth.forgot.email

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.email.ForgotEmailModel
import com.conduent.nationalhighways.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.conduent.nationalhighways.databinding.FragmentForgotEmailBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotEmailFragment : BaseFragment<FragmentForgotEmailBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: ForgotEmailViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentForgotEmailBinding.inflate(inflater, container, false)

    override fun init() {
        sessionManager.clearAll()
        requireActivity().toolbar(getString(R.string.txt_recovery_mail_address))
        binding.model = ForgotEmailModel(enable = false, accountNumber = "")
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        AdobeAnalytics.setScreenTrack(
            "login:forgot email",
            "forgot email",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot email",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
        binding.apply {
            edtAccountNumber.addTextChangedListener { isEnable() }
            edtPostCode.addTextChangedListener { isEnable() }
            btnNext.setOnClickListener(this@ForgotEmailFragment)
            btnLogin.setOnClickListener(this@ForgotEmailFragment)
        }
    }

    override fun observer() {
        observe(viewModel.forgotEmail, ::handleForgotEmail)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btn_next -> {

                    hideKeyboard()
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    viewModel.forgotEmail(binding.model)
                }

                R.id.btn_login -> {
                    AdobeAnalytics.setActionTrack(
                        "login",
                        "login:forgot email",
                        "forgot email",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen,
                        sessionManager.getLoggedInUser()
                    )

                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private fun handleForgotEmail(status: Resource<ForgotEmailResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                binding.llEnterDetails.visibility = GONE
                binding.llUsername.visibility = VISIBLE
                loadData(status)
                AdobeAnalytics.setActionTrackError(
                    "login",
                    "login:forgot email",
                    "forgot email",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    "success",
                    sessionManager.getLoggedInUser()
                )

            }
            is Resource.DataError -> {
                AdobeAnalytics.setActionTrackError(
                    "login",
                    "login:forgot email",
                    "forgot email",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    status.errorMsg,
                    sessionManager.getLoggedInUser()
                )

                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun loadData(status: Resource.Success<ForgotEmailResponseModel?>) {
        val username = viewModel.loadUserName(status.data?.userName ?: "")
        binding.tvUsername.text = username.toString().lowercase()
    }

    private fun isEnable() {
        if (binding.edtAccountNumber.length() > 0) binding.model =
            ForgotEmailModel(
                enable = true,
                accountNumber = binding.edtAccountNumber.text.toString()
            )
        else binding.model = ForgotEmailModel(
            enable = false,
            accountNumber = binding.edtAccountNumber.text.toString()

        )
    }
}