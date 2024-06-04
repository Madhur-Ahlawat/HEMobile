package com.conduent.nationalhighways.ui.bottomnav.account.close_account

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.data.model.contactdartcharge.CreateNewCaseReq
import com.conduent.nationalhighways.data.model.contactdartcharge.CreateNewCaseResp
import com.conduent.nationalhighways.databinding.FragmentCloseAccountBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.startNow.contactdartcharge.ContactDartChargeViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CloseAccountFragment : BaseFragment<FragmentCloseAccountBinding>() {

    private val contactDartChargeViewModel: ContactDartChargeViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCloseAccountBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }

    override fun initCtrl() {
        binding.message1.visible()
        binding.btnCloseAccount.setOnClickListener {
            showLoaderDialog()
            val newCaseReq = CreateNewCaseReq(
                HomeActivityMain.accountDetailsData?.personalInformation?.firstName,
                HomeActivityMain.accountDetailsData?.personalInformation?.lastName,
                HomeActivityMain.accountDetailsData?.personalInformation?.emailAddress,
                HomeActivityMain.accountDetailsData?.personalInformation?.phoneCell,
                HomeActivityMain.accountDetailsData?.personalInformation?.accountNumber,
                "account Closure",
                Constants.ACCOUNT_HOLDER_REQUEST,
                Constants.ACCOUNT_CLOSURE,
                null,
                "ENU",
                HomeActivityMain.accountDetailsData?.personalInformation?.phoneCellCountryCode
                    ?: "",

                )
            contactDartChargeViewModel.createNewCase(newCaseReq)
        }
        binding.btnContinue.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun observer() {
        observe(contactDartChargeViewModel.createNewCaseVal, ::createNewCase)
    }

    private fun createNewCase(resource: Resource<CreateNewCaseResp?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    requireActivity().startNewActivityByClearingStack(CloseAccountSuccessActivity::class.java) {
                        putString(
                            Constants.EMAIL,
                            HomeActivityMain.accountDetailsData?.personalInformation?.emailAddress
                        )
                        putString(
                            Constants.ACCOUNT_SUBTYPE,
                            HomeActivityMain.accountDetailsData?.accountInformation?.accSubType
                        )

                    }
                    requireActivity().finish()
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }
    }
}