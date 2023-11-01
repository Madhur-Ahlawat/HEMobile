package com.conduent.nationalhighways.ui.auth.forgot.password

import android.content.Intent
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentForgotResetBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_ADDRESS_CHANGED
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.REMOVE_VEHICLE
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ResetForgotPassword : BaseFragment<FragmentForgotResetBinding>(), View.OnClickListener {

    private var title: TextView? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotResetBinding = FragmentForgotResetBinding.inflate(inflater, container, false)

    @Inject
    lateinit var sessionManager: SessionManager
    override fun init() {
        binding?.feedbackBt?.setMovementMethod(LinkMovementMethod.getInstance())

        binding.btnSubmit.setOnClickListener(this)
        title = requireActivity().findViewById(R.id.title_txt)
        when (navFlowCall) {

            Constants.FORGOT_PASSWORD_FLOW -> {
                binding.subTitle.gone()
            }

            REMOVE_VEHICLE -> {
                binding.subTitle.gone()
                binding.title.text = getString(R.string.vehicle_deleted)
                binding.deleteTitle.gone()
                binding.cardViewPlateNumber.visible()
                val data = navData as VehicleResponse?
                binding.vehiclePlateNumber.text = data?.plateInfo?.number.toString()
                binding.btnSubmit.text = getString(R.string.str_continue)
            }

            PROFILE_MANAGEMENT -> {

                if (arguments?.getString(Constants.NAV_FLOW_FROM)
                        .equals(Constants.PROFILE_MANAGEMENT_EMAIL_CHANGE)
                ) {
                    title?.text = getString(R.string.profile_email_address)
                    val data = navData as PersonalInformation?
                    binding.title.text = getString(R.string.email_address_change_successful)
                    binding.subTitle.text = Html.fromHtml(
                        getString(
                            R.string.you_will_receive_a_confirmation_email,
                            data?.emailAddress
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                    sessionManager.clearAll()
                    binding.btnSubmit.text = getString(R.string.sign_in)
                } else {
                    title?.text = getString(R.string.profile_name)
                    val data = navData as PersonalInformation?
                    binding.title.text = getString(R.string.name_change_successful)
                    binding.subTitle.text = Html.fromHtml(
                        getString(
                            R.string.you_will_receive_a_confirmation_email,
                            data?.emailAddress
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                    binding.btnSubmit.text = getString(R.string.str_continue)
                }

            }

            PROFILE_MANAGEMENT_ADDRESS_CHANGED -> {
                title?.text = getString(R.string.profile_address)
                val data = navData as PersonalInformation?
                binding.title.text = getString(R.string.address_change_successful)
                binding.subTitle.text = Html.fromHtml(
                    getString(
                        R.string.you_will_receive_a_confirmation_email,
                        data?.emailAddress
                    ), Html.FROM_HTML_MODE_COMPACT
                )
                binding.btnSubmit.text = getString(R.string.str_continue)
            }

            PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                title?.text = getString(R.string.communication_preferences)
                binding.title.text = getString(R.string.communication_change_successful)
                binding.subTitle.gone()
                binding.btnSubmit.text = getString(R.string.str_continue)
            }

            PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                title?.text = getString(R.string.profile_mobile_number)
                val data = navData as PersonalInformation?
                val isMobileNumber = arguments?.getBoolean(Constants.IS_MOBILE_NUMBER, true)
                if (isMobileNumber == false) {
                    binding.title.text = getString(R.string.phone_number_change_successful)
                    HomeActivityMain.setTitle(getString(R.string.profile_phone_number))
                } else {
                    binding.title.text = getString(R.string.mobile_change_successful)
                    HomeActivityMain.setTitle(getString(R.string.profile_mobile_number))
                }
                binding.subTitle.text = Html.fromHtml(
                    getString(
                        R.string.you_will_receive_a_confirmation_email,
                        data?.emailAddress
                    ), Html.FROM_HTML_MODE_COMPACT
                )
                binding.btnSubmit.text = getString(R.string.str_continue)
            }

            PROFILE_MANAGEMENT_2FA_CHANGE -> {
                title?.text = getString(R.string.profile_2fa)
                val data = navData as PersonalInformation?
                binding.title.text = getString(R.string.two_factor_change_successful)
                binding.subTitle.text = Html.fromHtml(
                    getString(
                        R.string.you_will_receive_a_confirmation_email,
                        data?.emailAddress
                    ), Html.FROM_HTML_MODE_COMPACT
                )
                binding.btnSubmit.text = getString(R.string.str_continue)
            }
        }


    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {

                when (navFlowCall) {

                    REMOVE_VEHICLE -> {
                        findNavController().popBackStack()
                    }

                    PROFILE_MANAGEMENT_2FA_CHANGE, PROFILE_MANAGEMENT_MOBILE_CHANGE, PROFILE_MANAGEMENT,
                    PROFILE_MANAGEMENT_ADDRESS_CHANGED, PROFILE_MANAGEMENT -> {
                        if (arguments?.getString(Constants.NAV_FLOW_FROM)
                                .equals(Constants.PROFILE_MANAGEMENT_EMAIL_CHANGE)
                        ) {
                            Intent(requireActivity(), LandingActivity::class.java).apply {
                                putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(this)
                            }
                            requireActivity().finish()
                        } else {
                            findNavController().navigate(R.id.action_resetFragment_to_profileManagementFragment)
                        }

                    }

                    PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        findNavController().navigate(R.id.action_resetFragment_to_accountManagementFragment)
                    }

                    else -> {
                        /* AdobeAnalytics.setActionTrack(
                             "submit",
                             "login:forgot password:choose options:otp:new password set:password reset success",
                             "forgot password",
                             "english",
                             "login",
                             (requireActivity() as AuthActivity).previousScreen,
                             sessionManager.getLoggedInUser()
                         )*/

                        requireActivity().startNormalActivity(LoginActivity::class.java)
                        requireActivity().finish()

                    }
                }
            }
        }
    }

    private fun getSpannedText(text: String): Spanned? {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    }
}