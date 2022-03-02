package com.heandroid.ui.nominatedcontacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.heandroid.R
import com.heandroid.databinding.FragmentNominatedContactsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.*

class NominatedContactsFrag : BaseFragment<FragmentNominatedContactsBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNominatedContactsBinding {
        return FragmentNominatedContactsBinding.inflate(inflater, container, false)
    }

    override fun init() {

    }

    override fun initCtrl() {

        binding.apply {

            nominateContactBtn.setOnClickListener {

                youCanNominateTxt.gone()
                inviteNominatedContactTitle.visible()
                nominateContactBtn.gone()
                fullNameAndContactSubLyt.visible()
                tfFullName.visible()
                nextBtn.visible()
                fullName.setStyleBold()
                fullName.setUnderLineTxt(getString(R.string.str_full_name))

            }

            nextBtn.setOnClickListener {

                if (edtFullName.text.toString().isNotEmpty()) {

                    tfEmail.visible()
                    tfMobileNo.visible()
                    tfFullName.gone()
                    fullName.text = getString(R.string.str_full_name)
                    fullName.setStyleNormal()
                    contact.setStyleBold()
                    contact.setUnderLineTxt(getString(R.string.str_contact))

                    if (edtEmail.text.toString().isNotEmpty()) {
                        hideViews()
                        includeViewContactDetailsLyt.visible()
                    } else {
                        requireContext().showToast("Please enter email id correctly")
                    }

                } else {
                    requireContext().showToast("Please enter nominee full name")
                }

            }

            includeViewContactDetailsLyt.findViewById<MaterialButton>(R.id.invite_btn)
                .setOnClickListener {
                    includeViewContactDetailsLyt.gone()
                    includeNominatedContactsListLyt.visible()
                }

            includeViewContactDetailsLyt.findViewById<MaterialButton>(R.id.cancel_btn)
                .setOnClickListener {
//                    includeViewContactDetailsLyt.gone()

                }
        }

    }

    private fun hideViews() {
        binding.apply {
            youCanNominateTxt.gone()
            inviteNominatedContactTitle.gone()
            nominateContactBtn.gone()
            fullNameAndContactSubLyt.gone()
            tfFullName.gone()
            nextBtn.gone()
            tfEmail.gone()
            tfMobileNo.gone()
            nextBtn.gone()

        }


    }

    override fun observer() {
    }


}