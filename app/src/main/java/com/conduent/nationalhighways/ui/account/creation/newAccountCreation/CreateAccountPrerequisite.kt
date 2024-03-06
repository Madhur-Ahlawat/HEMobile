package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPrerequisiteBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseApplication.Companion.setFlowNameAnalytics1
import com.conduent.nationalhighways.ui.base.BaseApplication.Companion.setScreenNameAnalytics1
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.setupTextAccessibilityDelegate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPrerequisite : BaseFragment<FragmentCreateAccountPrerequisiteBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPrerequisiteBinding.inflate(inflater, container, false)

    override fun init() {
        setFlowNameAnalytics1(Constants.CREATE_ACCOUNT)
        setScreenNameAnalytics1("")
        val content = SpannableString(getString(R.string.sign_in))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.txtSignIn.text = content
        binding.btnCreateAccount.setOnClickListener(this)
        NewCreateAccountRequestModel.addedVehicleList.clear()
    }

    override fun initCtrl() {
        binding.textView3.setupTextAccessibilityDelegate(binding.textView3,
            "${getString(R.string.accessibility_bullet)}${getString(R.string.an_email_address_to_receive_a_code)}"
        )
        binding.textView6.setupTextAccessibilityDelegate(binding.textView6,
            "${getString(R.string.accessibility_bullet)}${getString(R.string.a_phone_number)}"
        )
        binding.textView8.setupTextAccessibilityDelegate(binding.textView8,
            "${getString(R.string.accessibility_bullet)}${getString(R.string.a_valid_debit_or_credit_card)}"
        )
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnCreateAccount.id -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.ACCOUNT_CREATION_EMAIL_FLOW)
                findNavController().navigate(
                    R.id.action_createAccountPrerequisite_to_fragment_choose_account_type,
                    bundle
                )
            }
        }
    }

    override fun onRetryClick(apiUrl: String){

    }
}