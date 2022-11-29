package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentContactDartChargeBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.startNow.guidancedocuments.GuidanceAndDocumentsActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.extn.customToolbar

class ContactDartChargeFragment : BaseFragment<FragmentContactDartChargeBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentContactDartChargeBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_contact_dart_charge))

        AdobeAnalytics.setScreenTrack(
            "contact dart charge",
            "contact dart charge",
            "english",
            "contact dart charge",
            "dart charge",
            "contact dart charge",
            false
        )

    }

    override fun initCtrl() {
        binding.rlCaseAndEnquiry.setOnClickListener {

            AdobeAnalytics.setActionTrack(
                "make an enquiry",
                "contact dart charge : case and enquiry",
                "case and enquiry",
                "english",
                "contact dart charge",
                "contact dart charge",
                false
            )

            findNavController().navigate(R.id.action_contactDartCharge_to_dartChargeAccountTypeSelectionFragment)
        }
        binding.rlGuidanceDocuments.setOnClickListener {
            startActivity(Intent(requireContext(), GuidanceAndDocumentsActivity::class.java))
        }
    }

    override fun observer() {}
}