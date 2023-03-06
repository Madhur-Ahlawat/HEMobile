package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.ConfirmOptionModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.conduent.nationalhighways.databinding.ForgotpasswordChangesBinding
import com.conduent.nationalhighways.databinding.FragmentForgotPasswordBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<ForgotpasswordChangesBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private var loader: LoaderDialog? = null
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var isCalled = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ForgotpasswordChangesBinding.inflate(inflater, container, false)

    override fun init() {
        sessionManager.clearAll()
        requireActivity().toolbar(getString(R.string.forgot_password))
        binding.model = ConfirmOptionModel(identifier = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        AdobeAnalytics.setScreenTrack(
            "login:forgot password",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
        //binding.edtPostcode.addTextChangedListener { isEnable() }
        binding.edtEmail.addTextChangedListener { isEnable() }
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.confirmOption, ::handleConfirmOptionResponse)
        }
    }

    private fun handleConfirmOptionResponse(status: Resource<ConfirmOptionResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isCalled) {
            when (status) {
                is Resource.Success -> {
                    if (status.data?.statusCode?.equals("1054") == true) {
                        showError(binding.root, status.data.message)
                    } else {
                        binding.root.post {
                            val bundle = Bundle()
                            bundle.putParcelable(Constants.OPTIONS, status.data)
                            findNavController().navigate(
                                R.id.action_forgotPasswordFragment_to_chooseOptionFragment,
                                bundle
                            )
                        }
                    }
                    AdobeAnalytics.setActionTrackError(
                        "next",
                        "login:forgot password",
                        "forgot password",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen, "success",
                        sessionManager.getLoggedInUser()
                    )

                }
                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)

                    AdobeAnalytics.setActionTrackError(
                        "next",
                        "login:forgot password",
                        "forgot password",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen, status.errorMsg,
                        sessionManager.getLoggedInUser()
                    )

                }
                else -> {
                }
            }
            isCalled = false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {

                    hideKeyboard()
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    sessionManager.saveAccountNumber(binding.edtEmail.text.toString().trim())
                    isCalled = true
                    viewModel.confirmOptionForForgot(binding.model)


            }
        }
    }

    private fun isEnable() {
        if (Utils.isEmailValid(binding.edtEmail.text.toString())) binding.model =
            ConfirmOptionModel(
                enable = true,
                identifier = binding.edtEmail.text.toString()
            )
        else binding.model = ConfirmOptionModel(
            enable = false,
            identifier = binding.edtEmail.text.toString()
        )
    }

}