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

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountClosedBinding.inflate(inflater, container, false)

    override fun init() {
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
    }
}