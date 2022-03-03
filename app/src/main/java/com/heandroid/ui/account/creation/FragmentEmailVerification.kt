package com.heandroid.ui.account.creation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentEmailVerificationBinding
import com.heandroid.ui.base.BaseFragment

class FragmentEmailVerification : BaseFragment<FragmentEmailVerificationBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEmailVerificationBinding {

        return FragmentEmailVerificationBinding.inflate(inflater, container, false)
    }

    override fun init() {
        requireActivity().title = getString(R.string.str_create_an_account)
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }
}