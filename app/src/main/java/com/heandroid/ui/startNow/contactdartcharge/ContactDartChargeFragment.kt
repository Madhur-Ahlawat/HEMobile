package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentContactDartChargeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.customToolbar

class ContactDartChargeFragment : BaseFragment<FragmentContactDartChargeBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentContactDartChargeBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_contact_dart_charge))
    }

    override fun initCtrl() {
        binding.rlCaseAndEnquiry.setOnClickListener {
            findNavController().navigate(R.id.action_contactDartCharge_to_dartChargeAccountTypeSelectionFragment)
        }
    }

    override fun observer() {}
}