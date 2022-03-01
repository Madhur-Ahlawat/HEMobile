package com.heandroid.ui.nominatedcontacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData
import com.heandroid.databinding.FragmentNominatedContactsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.vehiclelist.VehicleListAdapter
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
                    setNominatedContactsAdapter()
                }

            includeViewContactDetailsLyt.findViewById<MaterialButton>(R.id.cancel_btn)
                .setOnClickListener {
//                    includeViewContactDetailsLyt.gone()

                }
        }

    }

    private fun setNominatedContactsAdapter() {
        val mAdapter = NominatedContactsAdapter(requireContext())
        val mList = ArrayList<SecondaryAccountData?>()
        val mdata1 = SecondaryAccountData(
            "3784r4h",
            "cheruk13@gmail.com",
            "INVITED",
            "Prasad",
            "Cheruk",
            "898686466",
            true
        )
        val mdata2 = SecondaryAccountData(
            "3784r4h",
            "cheruk13@gmail.com",
            "INVITED",
            "Prasad",
            "Cheruk",
            "898686466",
            true
        )
        val mdata3 = SecondaryAccountData(
            "3784r4h",
            "cheruk13@gmail.com",
            "INVITED",
            "Prasad",
            "Cheruk",
            "898686466",
            true
        )
        val mdata4 = SecondaryAccountData(
            "3784r4h",
            "cheruk13@gmail.com",
            "INVITED",
            "Prasad",
            "Cheruk",
            "898686466",
            true
        )

        mList.add(mdata1)
        mList.add(mdata2)
        mList.add(mdata3)
        mList.add(mdata4)
        mAdapter.setList(mList)
        binding.includeNominatedContactsListLyt.findViewById<RecyclerView>(R.id.nominated_contact_recycler_view)
            .apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter
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