package com.conduent.nationalhighways.ui.bottomnav.account.close_account

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAccountClosedBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.startNow.contactdartcharge.ContactDartChargeViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountClosedFragment : BaseFragment<FragmentAccountClosedBinding>() {

    private val contactDartChargeViewModel: ContactDartChargeViewModel by viewModels()
    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountClosedBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.btnContinue.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun initCtrl() {
        if (HomeActivityMain.accountDetailsData?.accountInformation?.accSubType.equals(Constants.PAYG)) {
            binding?.titleNext?.visible()
            binding?.whatHappensNext?.visible()
        } else {
            binding?.titleNext?.gone()
            binding?.whatHappensNext?.gone()
        }
    }

    override fun observer() {
//        observe(contactDartChargeViewModel.createNewCaseVal, ::createNewCase)
    }

//    private fun createNewCase(resource: Resource<CreateNewCaseResp?>?) {
//        loader?.dismiss()
//        when (resource) {
//            is Resource.Success -> {
//                resource.data?.let {
//                  findNavController().navigate(R.id.action_closeAccountFragment_to_accountClosedFragment)
//                }
//            }
//
//            is Resource.DataError -> {
//                ErrorUtil.showError(binding.root, resource.errorMsg)
//            }
//
//            else -> {
//
//            }
//        }
//    }

//    private fun receipt(resource: Resource<ResponseBody?>?) {
//        if (loader?.isVisible == true) {
//            loader?.dismiss()
//        }
////        emailSuccessResponse=resource?.data
//        when (resource) {
//            is Resource.Success -> {
//                resource.data?.let {
//                    findNavController().navigate(R.id.emailRecieptSuccessFragment)
//                }
//            }
//
//            is Resource.DataError -> {
//                ErrorUtil.showError(binding.root, resource.errorMsg)
//            }
//
//            else -> {}
//        }
//
//    }
}