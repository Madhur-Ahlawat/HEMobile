package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentGuidanceDocumentsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GuidanceDocumentsFragment : BaseFragment<FragmentGuidanceDocumentsBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGuidanceDocumentsBinding =
        FragmentGuidanceDocumentsBinding.inflate(inflater, container, false)

    override fun init() {
        binding.contactDartChargeCv.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceDocumentsFragment_to_contactDartChargeFragment)
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

}