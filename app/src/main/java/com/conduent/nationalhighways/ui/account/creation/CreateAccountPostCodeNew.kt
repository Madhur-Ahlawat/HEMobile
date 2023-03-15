package com.conduent.nationalhighways.ui.account.creation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPostCodeNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPostCodeNew : BaseFragment<FragmentCreateAccountPostCodeNewBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPostCodeNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.btnFindAddress.setOnClickListener(this)
        binding.btnEnterAddressManually.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnEnterAddressManually.id -> {

            }
            binding.btnFindAddress.id -> {

            }
        }
    }

    override fun onRetryClick() {

    }
}