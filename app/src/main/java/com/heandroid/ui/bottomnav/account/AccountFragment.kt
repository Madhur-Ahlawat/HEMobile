package com.heandroid.ui.bottomnav.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.heandroid.R
import com.heandroid.data.model.auth.login.AuthResponseModel
import com.heandroid.data.model.nominatedcontacts.NominatedContactRes
import com.heandroid.databinding.FragmentAccountBinding
import com.heandroid.ui.account.communication.CommunicationActivity
import com.heandroid.ui.account.profile.ProfileActivity
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.auth.logout.LogoutDialog
import com.heandroid.ui.auth.logout.LogoutViewModel
import com.heandroid.ui.auth.logout.OnLogOutListener
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.account.accountstatements.AccountStatementActivity
import com.heandroid.ui.bottomnav.account.payments.AccountPaymentActivity
import com.heandroid.ui.landing.LandingActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.nominatedcontacts.NominatedContactActivity
import com.heandroid.ui.nominatedcontacts.list.NominatedContactListViewModel
import com.heandroid.ui.startNow.contactdartcharge.ContactDartChargeActivity
import com.heandroid.ui.vehicle.addvehicle.dialog.AddVehicleDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.openActivityWithData
import com.heandroid.utils.extn.openActivityWithDataBack
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(), View.OnClickListener,
    OnLogOutListener {

    private val viewModel: NominatedContactListViewModel by viewModels()
    private val logOutViewModel: LogoutViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isSecondaryUser: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        isSecondaryUser = sessionManager.getSecondaryUser()

        if (sessionManager.fetchAccountType()
                .equals(Constants.PERSONAL_ACCOUNT, true) && sessionManager.fetchSubAccountType()
                .equals(Constants.PAYG, true)
        ) {
            binding.nominatedContactsLyt.gone()
        }

        if (sessionManager.fetchAccountType()
                .equals("NonRevenue", true)
        ) {
            binding.payment.gone()
        }

        if (isSecondaryUser)
            binding.nominatedContactsLyt.gone()

    }

    override fun initCtrl() {
        binding.apply {
            profile.setOnClickListener(this@AccountFragment)
            payment.setOnClickListener(this@AccountFragment)
            rlAccount.setOnClickListener(this@AccountFragment)
            logOutLyt.setOnClickListener(this@AccountFragment)
            rlCaseAndEnquiry.setOnClickListener(this@AccountFragment)
            nominatedContactsLyt.setOnClickListener(this@AccountFragment)
            rlAccountStatement.setOnClickListener(this@AccountFragment)
        }

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.contactList, ::handleContactListResponse)
            observe(logOutViewModel.logout, ::handleLogout)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.profile -> {
                requireActivity().startNormalActivity(ProfileActivity::class.java)
            }

            R.id.payment -> {
                requireActivity().startNormalActivity(AccountPaymentActivity::class.java)
            }

            R.id.rl_account -> {
                requireActivity().startNormalActivity(CommunicationActivity::class.java)
            }

            R.id.nominated_contacts_lyt -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.nominatedContactList()

            }

            R.id.rl_case_and_enquiry -> {
                requireActivity().openActivityWithDataBack(ContactDartChargeActivity::class.java) {
                    putInt(
                        Constants.FROM_LOGIN_TO_CASES,
                        Constants.FROM_LOGIN_TO_CASES_VALUE
                    )
                }
            }

            R.id.rl_account_statement -> {
                requireActivity().startNormalActivity(AccountStatementActivity::class.java)
            }

            R.id.log_out_lyt -> {
                LogoutDialog.newInstance(
                    this
                ).show(childFragmentManager, Constants.LOGOUT_DIALOG)
            }

        }
    }

    private fun handleContactListResponse(status: Resource<NominatedContactRes?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {

                Logg.logging("AccountFragment", "nominated count data ${status.data}")

                Logg.logging(
                    "AccountFragment",
                    "nominated count ${status.data?.secondaryAccountDetailsType?.secondaryAccountList?.size}"
                )
                Intent(requireActivity(), NominatedContactActivity::class.java).run {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(
                        "count",
                        status.data?.secondaryAccountDetailsType?.secondaryAccountList?.size
                            ?: 0
                    )
                    startActivity(this)
                }

            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
            else -> {
            }
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
        Intent(requireActivity(), LandingActivity::class.java).apply {
            putExtra(Constants.SHOW_SCREEN, Constants.LOGOUT_SCREEN)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    override fun onLogOutClick() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        logOutViewModel.logout()
    }

}