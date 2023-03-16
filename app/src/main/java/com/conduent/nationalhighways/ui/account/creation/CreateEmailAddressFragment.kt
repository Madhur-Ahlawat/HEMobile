package com.conduent.nationalhighways.ui.account.creation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.ConfirmOptionModel
import com.conduent.nationalhighways.databinding.ForgotpasswordChangesBinding
import com.conduent.nationalhighways.databinding.FragmentCreateEmailAddressBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEmailAddressFragment : BaseFragment<FragmentCreateEmailAddressBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentCreateEmailAddressBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.edtEmail.addTextChangedListener { isEnable() }

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
    }

    private fun isEnable() {
        binding.enable = Utils.isEmailValid(binding.edtEmail.text.toString())

    }
}