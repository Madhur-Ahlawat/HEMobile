package com.conduent.nationalhighways.ui.bottomnav.account

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
import android.view.ViewGroup
import android.view.ViewTreeObserver
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

    private val raiseViewmodel: RaiseNewEnquiryViewModel by viewModels()
    private val logOutViewModel: LogoutViewModel by viewModels()
    private var isSecondaryUser: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountNewBinding = FragmentAccountNewBinding.inflate(inflater, container, false)

    override fun init() {
        isSecondaryUser = sessionManager.getSecondaryUser()
        setPaymentsVisibility()
        initUI()
        binding.contactUs.visible()
        setBackPressListener(this)
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
        binding.profileManagement.contentDescription = getString(R.string.profile_management)
        binding.communicationPreferences.contentDescription =
            getString(R.string.communication_preferences)
        binding.paymentManagement.contentDescription = getString(R.string.payment_management)
        binding.vehicleManagement.contentDescription = getString(R.string.vehicle_management)
        binding.contactUs.contentDescription = getString(R.string.contact_us)
        binding.closeAcount.contentDescription = getString(R.string.str_close_account)
        binding.signOut.contentDescription = getString(R.string.sign_out)
        val builder = Utils.accessibilityForNumbers(sessionManager.fetchAccountNumber().toString())


        binding.headerParent.contentDescription =
            getString(R.string.name) + ", " + Utils.capitalizeString(sessionManager.fetchFirstName()) + "\n" + Utils.capitalizeString(
                sessionManager.fetchLastName()
            ) + ", " + getString(R.string.account_number) + ", " + builder + "\n" + getString(
                R.string.account_status
            ) + ", " + binding.indicatorAccountStatus.text.toString()

        binding.header.contentDescription =
            getString(R.string.name) + ", " + Utils.capitalizeString(sessionManager.fetchFirstName()) + ", " + Utils.capitalizeString(
                sessionManager.fetchLastName()
            ) + ", " + getString(R.string.account_number) + ", " + builder + ", " + getString(
                R.string.account_status
            ) + ", " + binding.indicatorAccountStatus.text.toString()

        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }

    private fun initUI() {
        if (arguments?.containsKey(Constants.NAV_FLOW_KEY) == true) {
            navFlowFrom = arguments?.getString(Constants.NAV_FLOW_KEY, "").toString()
        }
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

            val firstNameChar = sessionManager.fetchFirstName()?.first() ?: ' '
            val secondNameChar = sessionManager.fetchLastName()?.first() ?: ' '

            profilePic.text = resources.getString(
                R.string.concatenate_two_strings,
                firstNameChar.toString(),
                secondNameChar.toString()
            )
            profilePic.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            profilePic.isScreenReaderFocusable = false
            tvAccountNumberValue.text = sessionManager.fetchAccountNumber()
            tvAccountNumberValueLargefont.text = sessionManager.fetchAccountNumber()
            DashboardUtils.setAccountStatusNew(
                sessionManager.fetchAccountStatus() ?: "",
                indicatorAccountStatus,
                binding.cardIndicatorAccountStatus, 4
            )
            DashboardUtils.setAccountStatusNew(
                sessionManager.fetchAccountStatus() ?: "",
                indicatorAccountStatusLargefont,
                binding.cardIndicatorAccountStatusLargefont, 4
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
                resources.getString(
                    R.string.concatenate_two_strings_with_space,
                    Utils.capitalizeString(sessionManager.fetchFirstName()), Utils.capitalizeString(
                        sessionManager.fetchLastName()
                    )
                )

            if (requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain).focusToolBarHome()
            }

        }
        if (navFlowFrom == Constants.BIOMETRIC_CHANGE) {
            if(requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain).changeBottomIconColors(requireActivity(), 3)
            }
            val bundle = Bundle()
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

        binding.tvAccountStatusHeading.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.tvAccountStatusHeading.viewTreeObserver.removeOnGlobalLayoutListener(this)
                binding.indicatorAccountStatus.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.indicatorAccountStatus.viewTreeObserver.removeOnGlobalLayoutListener(
                            this
                        )
                        val accountNumberLinesCount = binding.tvAccountStatusHeading.lineCount
                        val indicatorAccountStatusLineCount =
                            binding.indicatorAccountStatus.lineCount

                        if (accountNumberLinesCount > 2 || indicatorAccountStatusLineCount >= 2) {
                            binding.llAccountNumberLargefont.visible()
                            binding.llAccountStatusLargefont.visible()

                            binding.llAccountNumber.gone()
                            binding.llAccountStatus.gone()
                        } else {
                            binding.llAccountNumberLargefont.gone()
                            binding.llAccountStatusLargefont.gone()
                            binding.llAccountNumber.visible()
                            binding.llAccountStatus.visible()
                        }
                    }
                })
            }
        })
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
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).setTitle(getString(R.string.txt_my_account))
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
                findNavController().navigate(R.id.action_accountFragment_to_profileManagementFragment)
            }

            R.id.payment_management -> {
                findNavController().navigate(R.id.action_accountFragment_to_paymentMethodFragment)
                if (requireActivity() is HomeActivityMain) {
                    (requireActivity() as HomeActivityMain).setTitle(getString(R.string.payment_management))
                }
            }

            R.id.communication_preferences -> {
                if (requireActivity() is HomeActivityMain) {
                    (requireActivity() as HomeActivityMain).setTitle(getString(R.string.communication_preferences))
                }
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
                if (requireActivity() is HomeActivityMain) {
                    (requireActivity() as HomeActivityMain).setTitle(getString(R.string.vehicle_management))
                }
                findNavController().navigate(R.id.action_accountFragment_to_vehicleManagementFragment)
            }

            R.id.close_acount -> {
                if (!sessionManager.fetchAccountStatus().equals("SUSPENDED", true)) {
                    if (requireActivity() is HomeActivityMain) {
                        (requireActivity() as HomeActivityMain).setTitle(getString(R.string.str_close_account))
                    }
                    findNavController().navigate(R.id.action_accountFragment_to_closeAccountFragment)
                }
            }

            R.id.contact_us -> {

                raiseViewmodel.enquiryModel.value = EnquiryModel()
                raiseViewmodel.edit_enquiryModel.value = EnquiryModel()


                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.ACCOUNT_CONTACT_US)
                findNavController().navigate(R.id.caseEnquiryHistoryListFragment, bundle)
            }

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
        dismissLoaderDialog()
    }


    private fun handleLogout(status: Resource<AuthResponseModel?>?) {
        dismissLoaderDialog()
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
        Utils.redirectToSignOutPage(requireActivity())
    }

    override fun onLogOutClick() {
        showLoaderDialog()
        logOutViewModel.logout()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }


    }

    override fun onBackButtonPressed() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).backPressLogic()
        }
    }


}