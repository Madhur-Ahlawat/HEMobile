package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountTypesBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.viewcharges.ViewChargesActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountTypes : BaseFragment<FragmentCreateAccountTypesBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountTypesBinding.inflate(inflater, container, false)

    override fun init() {
        binding.crossingCharges.setOnClickListener(this)
    }

    override fun initCtrl() {
        binding.prePayCard.setOnClickListener(this)
        binding.payCard.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.prePayCard -> {
                NewCreateAccountRequestModel.prePay = true
                handleNavigation()
            }
            R.id.payCard -> {
                NewCreateAccountRequestModel.prePay=false
                handleNavigation()
            }
            R.id.crossingCharges -> {
                val openURL = Intent(requireContext(),ViewChargesActivity::class.java)
                startActivity(openURL)
            }
        }
    }

    private fun handleNavigation() {
        when(navFlowCall){

            EDIT_SUMMARY -> {findNavController().popBackStack()}
            else -> {val bundle=Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,Constants.ACCOUNT_CREATION_EMAIL_FLOW)
                findNavController().navigate(
                    R.id.action_createAccountTypes_to_forgotPasswordFragment,
                    bundle
                )}

        }
    }

    override fun onRetryClick() {

    }
}