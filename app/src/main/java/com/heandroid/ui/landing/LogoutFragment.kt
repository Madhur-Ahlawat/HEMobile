package com.heandroid.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentLogoutBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.setRightButtonText

class LogoutFragment : BaseFragment<FragmentLogoutBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLogoutBinding {
        return FragmentLogoutBinding.inflate(inflater, container, false)
    }

    override fun init() {
        binding.apply {
            requireActivity().setRightButtonText(getString(R.string.contact_us))
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

}