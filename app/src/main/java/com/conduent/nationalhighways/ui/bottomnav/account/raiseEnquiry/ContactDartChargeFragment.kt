package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentContactDartCharge2Binding
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDartChargeFragment : BaseFragment<FragmentContactDartCharge2Binding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactDartCharge2Binding =
        FragmentContactDartCharge2Binding.inflate(inflater, container, false)

    override fun init() {
        binding.getInTouchMb.setOnClickListener {
            findNavController().navigate(R.id.action_contactDartChargeFragment_to_dartChargeAccountTypeEnquiryFragment  )
        }
        binding.checkEnquiryStatusMb.setOnClickListener {
            findNavController().navigate(R.id.action_contactDartChargeFragment_to_enquiryStatusFragment)
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

}