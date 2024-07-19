package com.conduent.nationalhighways.ui.inActive

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.databinding.FragmentInActiveNotSureBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InActiveNotSureFragment : BaseFragment<FragmentInActiveNotSureBinding>() {
    private var accountInformation: AccountInformation? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInActiveNotSureBinding =
        FragmentInActiveNotSureBinding.inflate(inflater, container, false)


    override fun init() {
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation = arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }
        val accountToBeClosedDate = Utils.convertOneFormatDateToAnotherFormat(
            accountInformation?.accountToBeClosedDate,
            "MM/dd/yyyy hh:mm:ss", "dd MMM yyyy"
        )


        binding.titleTv.text =
            resources.getString(R.string.str_account_close_on, accountToBeClosedDate)
        binding.btnContinue.setOnClickListener {
            requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
            }
        }
        if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

}