package com.heandroid.ui.account.creation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.data.model.account.AccountDetails
import com.heandroid.databinding.FragmentPersonalDetailsEntryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.toolbar

class PersonalDetailsEntryFragment : BaseFragment<FragmentPersonalDetailsEntryBinding>() {
    private lateinit var accountModel:AccountDetails
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalDetailsEntryBinding {
        return FragmentPersonalDetailsEntryBinding.inflate(inflater, container, false)
    }

    override fun init() {
        requireActivity().toolbar(getString(R.string.str_create_an_account))
        accountModel = AccountDetails()
    }

    override fun initCtrl() {
        binding.apply {
            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 3, 5)
        }
    }

    override fun observer() {
    }
}