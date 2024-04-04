package com.conduent.nationalhighways.ui.bottomnav.account

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.login.AuthResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.databinding.FragmentAccountNewBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.auth.logout.LogoutViewModel
import com.conduent.nationalhighways.ui.auth.logout.OnLogOutListener
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.DashboardUtils
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountNewBinding>(), View.OnClickListener,
    OnLogOutListener, BackPressListener {

    private val raise_viewModel: RaiseNewEnquiryViewModel by viewModels()
    private val logOutViewModel: LogoutViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var loader: LoaderDialog? = null
    private var isSecondaryUser: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager
    private var title: TextView? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountNewBinding = FragmentAccountNewBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        isSecondaryUser = sessionManager.getSecondaryUser()
        setPaymentsVisibility()
        initUI()
        binding.contactUs.visible()
        setBackPressListener(this)
        if(requireActivity() is HomeActivityMain){
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }

    private fun initUI() {
        if (arguments?.containsKey(Constants.NAV_FLOW_KEY) == true) {
            navFlowFrom = arguments?.getString(Constants.NAV_FLOW_KEY, "").toString()
        }
        title = requireActivity().findViewById(R.id.title_txt)
        binding.run {
            if (HomeActivityMain.accountDetailsData?.accountInformation?.accSubType.equals(Constants.EXEMPT_ACCOUNT)) {
                paymentManagement.gone()
            } else {
                paymentManagement.visible()
            }
            if (isSecondaryUser)
                contactUs.gone()

            if (sessionManager.fetchAccountType().equals(
                    Constants.PERSONAL_ACCOUNT,
                    true
                ) && sessionManager.fetchSubAccountType()
                    .equals(Constants.PAYG, true)
            ) {
                contactUs.gone()
            }

            if (sessionManager.fetchAccountType()
                    .equals("NonRevenue", true)
            ) {
                paymentManagement.gone()
            }

            var firstNameChar: Char = ' '
            var secondNameChar: Char = ' '
            firstNameChar = sessionManager.fetchFirstName()?.first() ?: ' '
            secondNameChar = sessionManager.fetchLastName()?.first() ?: ' '

            profilePic.text = "" + firstNameChar.toString() + secondNameChar.toString()
            tvAccountNumberValue.text = sessionManager.fetchAccountNumber()
            DashboardUtils.setAccountStatusNew(
                sessionManager.fetchAccountStatus() ?: "",
                indicatorAccountStatus,
                binding.cardIndicatorAccountStatus, 4
            )
            if (sessionManager.fetchAccountStatus().equals("SUSPENDED", true)) {
                leftIcon6.alpha = 0.5f
                valueTitle6.alpha = 0.5f
                iconArrow6.alpha = 0.5f
            } else {
                leftIcon6.alpha = 1f
                valueTitle6.alpha = 1f
                iconArrow6.alpha = 1f
            }
            valueName.text =
                Utils.capitalizeString(sessionManager.fetchFirstName()) + " " + Utils.capitalizeString(
                    sessionManager.fetchLastName()
                )

            if(requireActivity() is HomeActivityMain){
                (requireActivity() as HomeActivityMain).focusToolBarHome()
            }

        }
        if (navFlowFrom == Constants.BIOMETRIC_CHANGE) {
            HomeActivityMain.changeBottomIconColors(requireActivity(), 3)
            var bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_KEY, navFlowFrom)
            bundle.putParcelable(
                Constants.PERSONALDATA,
                HomeActivityMain.accountDetailsData?.personalInformation
            )
            findNavController().navigate(
                R.id.action_accountFragment_to_profileManagementFragment,
                bundle
            )
        }
    }

    private fun setPaymentsVisibility() {
        if (sessionManager.fetchAccountType().equals("BUSINESS", true)
            || (sessionManager.fetchSubAccountType().equals("STANDARD", true) &&
                    sessionManager.fetchAccountType().equals("PRIVATE", true))
        ) {
            binding.paymentManagement.visible()
            binding.contactUs.visible()
        } else {
            if (sessionManager.fetchSubAccountType().equals(Constants.PAYG, true) &&
                sessionManager.fetchAccountType().equals("PRIVATE", true)
            ) {
                binding.paymentManagement.visible()

            } else {
                binding.paymentManagement.gone()

            }
            binding.contactUs.gone()
        }
    }

    override fun onResume() {
        title?.text = getString(R.string.txt_my_account)
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).refreshTokenApi()
        }

        super.onResume()
    }

    override fun initCtrl() {
        binding.apply {
            profileManagement.setOnClickListener(this@AccountFragment)
            paymentManagement.setOnClickListener(this@AccountFragment)
            vehicleManagement.setOnClickListener(this@AccountFragment)
            communicationPreferences.setOnClickListener(this@AccountFragment)
            signOut.setOnClickListener(this@AccountFragment)
            closeAcount.setOnClickListener(this@AccountFragment)
//            rlCaseAndEnquiry.setOnClickListener(this@AccountFragment)
            contactUs.setOnClickListener(this@AccountFragment)
//            rlAccountStatement.setOnClickListener(this@AccountFragment)
//            rlBiometrics.setOnClickListener(this@AccountFragment)
        }

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(logOutViewModel.logout, ::handleLogout)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.profile_management -> {
                title?.text = getString(R.string.profile_management)
                findNavController().navigate(R.id.action_accountFragment_to_profileManagementFragment)

            }

            R.id.payment_management -> {

                findNavController().navigate(R.id.action_accountFragment_to_paymentMethodFragment)
                title?.text = getString(R.string.payment_management)
//                requireActivity().startNormalActivity(AccountPaymentActivity::class.java)
            }

            R.id.communication_preferences -> {
                title?.text = getString(R.string.communication_preferences)
                val bundle = Bundle()
                bundle.putString(
                    Constants.NAV_FLOW_KEY,
                    Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
                )
                findNavController().navigate(
                    R.id.action_accountFragment_to_optForSmsFragment,
                    bundle
                )
            }

            R.id.vehicle_management -> {
                title?.text = getString(R.string.vehicle_management)
                findNavController().navigate(R.id.action_accountFragment_to_vehicleManagementFragment)

            }

            R.id.close_acount -> {
                if (!sessionManager.fetchAccountStatus().equals("SUSPENDED", true)) {
                    title?.text = getString(R.string.str_close_account)
                    findNavController().navigate(R.id.action_accountFragment_to_closeAccountFragment)
                }
            }

            R.id.contact_us -> {

                raise_viewModel.enquiryModel.value = EnquiryModel()
                raise_viewModel.edit_enquiryModel.value = EnquiryModel()


                val bundle: Bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.ACCOUNT_CONTACT_US)
                findNavController().navigate(R.id.caseEnquiryHistoryListFragment, bundle)
            }

//            R.id.rl_account_statement -> {
//                requireActivity().startNormalActivity(AccountStatementActivity::class.java)
//            }
//            R.id.rl_biometrics->{
//                requireActivity().openActivityWithDataBack(BiometricActivity::class.java) {
//                    putInt(
//                        Constants.FROM_LOGIN_TO_BIOMETRIC,
//                        Constants.FROM_ACCOUNT_TO_BIOMETRIC_VALUE
//                    )
//                }
//            }

            R.id.sign_out -> {
                if (sessionManager.fetchTouchIdEnabled()) {
                    displayCustomMessage(
                        resources.getString(R.string.str_signout_your_account),
                        resources.getString(R.string.str_signout_your_account_desc),
                        resources.getString(R.string.str_continue),
                        resources.getString(R.string.str_cancel),
                        object : DialogPositiveBtnListener {
                            override fun positiveBtnClick(dialog: DialogInterface) {
                                logOutViewModel.logout()
                            }
                        },
                        object : DialogNegativeBtnListener {
                            override fun negativeBtnClick(dialog: DialogInterface) {
                            }
                        },
                        View.VISIBLE,
                        cancelButtonColor = requireActivity().resources.getColor(
                            R.color.hyperlink_blue2,
                            null
                        ),
                        typeFace = Typeface.createFromAsset(
                            requireActivity().assets,
                            "open_sans_semibold.ttf"
                        )
                    )
                } else {
                    logOutViewModel.logout()
                }


            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

    }


    private fun handleLogout(status: Resource<AuthResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                logOutOfAccount()
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    private fun logOutOfAccount() {
        sessionManager.clearAll()
        sessionManager.saveBooleanData(SessionManager.LOGGED_OUT_FROM_DASHBOARD, false)
        Utils.redirectToSignoutPage(requireActivity())

//        Intent(requireActivity(), LoginActivity::class.java).apply {
//            putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
//            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(this)
//        }
    }

    override fun onLogOutClick() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        logOutViewModel.logout()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as HomeActivityMain).showHideToolbar(true)


    }

    override fun onBackButtonPressed() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).backPressLogic()
        }
    }


}