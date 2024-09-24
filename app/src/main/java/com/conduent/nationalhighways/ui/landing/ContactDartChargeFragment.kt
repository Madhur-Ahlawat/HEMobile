package com.conduent.nationalhighways.ui.landing

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.databinding.FragmentContactDartCharge2Binding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
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
        binding.getInTouchMb.setOnClickListener {
            raise_viewModel.enquiryModel.value = EnquiryModel()
            raise_viewModel.edit_enquiryModel.value = EnquiryModel()
            findNavController().navigate(R.id.action_contactDartChargeFragment_to_dartChargeAccountTypeEnquiryFragment)
        }
        binding.checkEnquiryStatusMb.setOnClickListener {
            findNavController().navigate(R.id.action_contactDartChargeFragment_to_enquiryStatusFragment)
        }
        if (requireActivity() is RaiseEnquiryActivity) {
            (requireActivity() as RaiseEnquiryActivity).focusToolBarRaiseEnquiry()
        }
    }

    override fun initCtrl() {
        binding.findOutTv?.setMovementMethod(LinkMovementMethod.getInstance())
        binding?.feedbackToImproveMb?.setMovementMethod(LinkMovementMethod.getInstance())


    }

    override fun observer() {
    }

}