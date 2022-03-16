package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentAboutServiceBinding
import com.heandroid.databinding.FragmentContactDartChargeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.customToolbar
import com.heandroid.utils.extn.toolbar

class ContactDartChargeFragment : BaseFragment<FragmentContactDartChargeBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentContactDartChargeBinding.inflate(inflater, container, false)

    override fun init() {}

    override fun onResume() {
        super.onResume()
        requireActivity().customToolbar(getString(R.string.str_contact_dart_charge))
    }

    override fun initCtrl() {}

    override fun observer() {}
}