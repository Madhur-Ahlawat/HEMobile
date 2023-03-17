package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.text.TextUtils
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
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.databinding.ForgotpasswordChangesBinding
import com.conduent.nationalhighways.databinding.FragmentForgotPasswordBinding
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
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
     private lateinit var  navFlow:String// create account , forgot password
    private var isViewCreated:Boolean=false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ForgotpasswordChangesBinding.inflate(inflater, container, false)

    override fun init() {
        sessionManager.clearAll()
        requireActivity().toolbar(getString(R.string.forgot_password))
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()


       // binding.model = ConfirmOptionModel(identifier = "", enable = false)
        binding.email=""
        binding.isValid=false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        /*AdobeAnalytics.setScreenTrack(
            "login:forgot password",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password",
            sessionManager.getLoggedInUser()
        )*/

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
        if (!isViewCreated){
            observe(viewModel.otp, ::handleOTPResponse)

        }

        isViewCreated=true
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
                            bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
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

    private fun handleOTPResponse(status: Resource<SecurityCodeResponseModel?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (status) {
            is Resource.Success -> {
                val bundle = Bundle()
                bundle.putParcelable("data", RequestOTPModel(Constants.EMAIL,binding.email))

                bundle.putParcelable("response", status.data)


                bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                findNavController().navigate(
                    R.id.action_forgotPasswordFragment_to_otpFragment,
                    bundle
                )

                AdobeAnalytics.setActionTrack2(
                    "continue",
                    "login:forgot password:choose options",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen, Constants.EMAIL,
                    sessionManager.getLoggedInUser()
                )



            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {

                    hideKeyboard()

                if (navFlow==Constants.FORGOT_PASSWORD_FLOW){
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    sessionManager.saveAccountNumber(binding.email.toString().trim())
                    isCalled = true
                    viewModel.confirmOptionForForgot(binding.email.toString())

                }else{
                    hitApi()

                }


            }
        }
    }


    private fun isEnable() {
        binding.isValid=Utils.isEmailValid(binding.edtEmail.text.toString())
        /*if (Utils.isEmailValid(binding.edtEmail.text.toString())) binding.model =
            ConfirmOptionModel(
                enable = true,
                identifier = binding.edtEmail.text.toString()
            )
        else binding.model = ConfirmOptionModel(
            enable = false,
            identifier = binding.edtEmail.text.toString()
        )*/
    }

    private fun hitApi(){
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.requestOTP(RequestOTPModel(optionType = Constants.EMAIL, optionValue = binding.email))
    }

}