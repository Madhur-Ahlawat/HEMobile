package com.conduent.nationalhighways.ui.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.databinding.FragmentContactDartCharge2Binding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDartChargeFragment : BaseFragment<FragmentContactDartCharge2Binding>() {
    val raise_viewModel: RaiseNewEnquiryViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactDartCharge2Binding =
        FragmentContactDartCharge2Binding.inflate(inflater, container, false)

    override fun init() {
        LandingActivity.setToolBarTitle(getString(R.string.contact_dart_charge))
        LandingActivity.showToolBar(true)
        binding.getInTouchMb.setOnClickListener {
            findNavController().navigate(R.id.action_contactDartChargeFragment_to_doYouHaveDartChargeFragment)
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