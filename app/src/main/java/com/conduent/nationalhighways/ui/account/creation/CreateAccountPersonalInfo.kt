package com.conduent.nationalhighways.ui.account.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPersonalInfoNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPersonalInfo : BaseFragment<FragmentCreateAccountPersonalInfoNewBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPersonalInfoNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {
                val bundle = Bundle()

                findNavController().navigate(
                    R.id.action_createAccountPersonalInfo_to_createAccountPostCodeNew,
                    bundle
                )
            }
        }
    }

    override fun onRetryClick() {

    }
}