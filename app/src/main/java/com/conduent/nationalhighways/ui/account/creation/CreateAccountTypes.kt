package com.conduent.nationalhighways.ui.account.creation

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountEligibleLrdsBinding
import com.conduent.nationalhighways.databinding.FragmentCreateAccountTypeBinding
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
                bundle.putString(Constants.NAV_FLOW_KEY,Constants.ACCOUNT_CREATION_FLOW)
             findNavController().navigate(R.id.action_createAccountTypes_to_forgotPasswordFragment,bundle)
            }
        }
    }

    override fun onRetryClick() {

    }
}