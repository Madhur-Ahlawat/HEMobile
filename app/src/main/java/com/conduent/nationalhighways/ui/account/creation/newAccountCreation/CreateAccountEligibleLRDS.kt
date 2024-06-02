package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountEligibleLrdsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountEligibleLRDS : BaseFragment<FragmentCreateAccountEligibleLrdsBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountEligibleLrdsBinding.inflate(inflater, container, false)

    override fun init() {

    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
        binding.continueWithoutApplying.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.continueWithoutApplying.id -> {
                val bundle = Bundle()
                bundle.putString(NAV_FLOW_KEY,navFlowCall)
                when(navFlowCall){

                    EDIT_SUMMARY -> {
                        findNavController().navigate(
                            R.id.action_createAccountEligibleLRDS_to_accountSummaryFragment,bundle
                        )
                    }
                    else ->{
                        findNavController().navigate(
                            R.id.action_createAccountEligibleLRDS_to_createAccountTypes,bundle
                        )
                    }

                }


            }
            binding.btnContinue.id -> {

                val url =
                    "https://pay-dartford-crossing-charge.service.gov.uk/account-setup/account-start"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)


            }
        }
    }


    override fun onRetryClick(apiUrl: String) {

    }
}