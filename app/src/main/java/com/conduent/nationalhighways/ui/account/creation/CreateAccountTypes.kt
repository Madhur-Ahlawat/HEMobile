package com.conduent.nationalhighways.ui.account.creation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountTypesBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountTypes : BaseFragment<FragmentCreateAccountTypesBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountTypesBinding.inflate(inflater, container, false)

    override fun init() {
        binding.txtCrossingInfo.setOnClickListener(this)
    }

    override fun initCtrl() {
        binding.viewPrePay.cardview.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.viewPrePay -> {
                val bundle=Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,Constants.ACCOUNT_CREATION_EMAIL_FLOW)
                findNavController().navigate(R.id.action_createAccountTypes_to_forgotPasswordFragment,bundle)
/*
               // findNavController().navigate(R.id.action_createAccountTypes_to_optForSmsFragment,bundle)
*/
            }
            R.id.viewPayAsYouGo -> {
                val bundle=Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,Constants.ACCOUNT_CREATION_EMAIL_FLOW)
                findNavController().navigate(R.id.action_createAccountTypes_to_forgotPasswordFragment,bundle)
                //findNavController().navigate(R.id.action_createAccountTypes_to_optForSmsFragment,bundle)
            }
            R.id.txtCrossingInfo -> {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse("https://www.gov.uk/pay-dartford-crossing-charge/charges-fines/")
                startActivity(openURL)
            }
        }
    }

    override fun onRetryClick() {

    }
}