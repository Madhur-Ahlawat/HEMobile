package com.heandroid.ui.nominatedcontacts.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentNoNomiatedContactBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoNominatedContactFragment : BaseFragment<FragmentNoNomiatedContactBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNoNomiatedContactBinding =
        FragmentNoNomiatedContactBinding.inflate(inflater, container, false)

    override fun init() {
        binding.btnNotimateContact.setOnClickListener {
            findNavController().navigate(
                R.id.action_ncNoListFragment_to_ncFullNameFragment
            )

        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}