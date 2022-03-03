package com.heandroid.ui.nominatedcontacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.GetSecondaryAccessRightsResp
import com.heandroid.data.model.nominatedcontacts.NominatedContactRes
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountResp
import com.heandroid.databinding.FragmentNominatedContactsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import okhttp3.internal.filterList

@AndroidEntryPoint
class NominatedContactsFrag : BaseFragment<FragmentNominatedContactsBinding>() {

    private val nominatedContactsViewModel: NominatedContactsViewModel by viewModels()
    private var loader: LoaderDialog? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNominatedContactsBinding {
        return FragmentNominatedContactsBinding.inflate(inflater, container, false)
    }

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, "")
        nominatedContactsViewModel.nominatedContactListFetch()

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
                        includeViewContactDetailsLyt.nominatedAccessRightsContainer.visible()
                    } else {
                        requireContext().showToast("Please enter email id correctly")
                    }

                } else {
                    requireContext().showToast("Please enter nominee full name")
                }

            }

            includeViewContactDetailsLyt.inviteBtn
                .setOnClickListener {
                    includeViewContactDetailsLyt.nominatedAccessRightsContainer.gone()
                    includeNominatedContactsListLyt.nominatedContactsListContainer.visible()

                }

            includeViewContactDetailsLyt.cancelBtn
                .setOnClickListener {
                    includeViewContactDetailsLyt.nominatedAccessRightsContainer.gone()
                    includeNominatedContactsListLyt.nominatedContactsListContainer.gone()
                }

            includeNominatedContactsListLyt.activeTxtLst.setOnClickListener {
                Logg.logging("TESTSTR", "testess onclick called")

                includeNominatedContactsListLyt.activeTxtLst.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.text_selected_bg)
                includeNominatedContactsListLyt.invitedTxtList.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.text_unselected_bg)
                includeNominatedContactsListLyt.activeTxtLst.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                includeNominatedContactsListLyt.invitedTxtList.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                val activeList = mList.filter {
                    it?.accountStatus != "INITIATED"
                }

                setNominatedContactsAdapter(activeList as ArrayList<SecondaryAccountData?>)

            }

            includeNominatedContactsListLyt.invitedTxtList.setOnClickListener {

                includeNominatedContactsListLyt.invitedTxtList.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.text_selected_bg)
                includeNominatedContactsListLyt.activeTxtLst.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.text_unselected_bg)
                includeNominatedContactsListLyt.invitedTxtList.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                includeNominatedContactsListLyt.activeTxtLst.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                val initiatedList = mList.filter {
                    it?.accountStatus.equals("INITIATED", true)
                }
                setNominatedContactsAdapter(initiatedList as ArrayList<SecondaryAccountData?>)

            }
            includeNominatedContactsListLyt.nominateBtnList.setOnClickListener {

                if (mList.isNotEmpty() && mList.size > 5) {
                    requireActivity().showToast(getString(R.string.str_nominated_contacts_limit_reached))
                } else {
                    nominateContactBtn.performClick()
                }

            }

        }

    }

    private val mList = ArrayList<SecondaryAccountData?>()
    lateinit var mAdapter: NominatedContactsAdapter
    private fun setNominatedContactsAdapter(mTempList: ArrayList<SecondaryAccountData?>) {
        if (mTempList.isNotEmpty()) {
            binding.includeNominatedContactsListLyt.nominatedContactsListContainer.visible()
            binding.includeNominatedContactsListLyt.nominatedContactRecyclerView.visible()
            binding.includeNominatedContactsListLyt.noContacts.gone()

            mAdapter = NominatedContactsAdapter(requireContext())
            mAdapter.setList(mTempList)
            binding.includeNominatedContactsListLyt.nominatedContactRecyclerView
                .apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = mAdapter
                }
        } else {
            binding.includeNominatedContactsListLyt.nominatedContactsListContainer.visible()
            binding.includeNominatedContactsListLyt.nominatedContactRecyclerView.gone()
            binding.includeNominatedContactsListLyt.noContacts.visible()

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
        observe(nominatedContactsViewModel.nominatedContactListLiveData, ::fetchListResponse)
        observe(nominatedContactsViewModel.createAccountLiveData, ::createAccount)
        observe(nominatedContactsViewModel.getSecondaryRightsLiveData, ::getSecondaryAccountsRes)
        observe(
            nominatedContactsViewModel.updateSecondaryAccountLiveData,
            ::updateSecondaryAccountRes
        )
        observe(
            nominatedContactsViewModel.updateSecondaryAccessRightsLivedata,
            ::updateSecondaryAccessRightsRes
        )
    }

    private fun fetchListResponse(status: Resource<NominatedContactRes?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode == "0") {

                    if (status.data.secondaryAccountDetailsType?.secondaryAccountList!!.size > 0) {

                        if (mList.isNotEmpty())
                            mList.clear()

                        mList.addAll(status.data.secondaryAccountDetailsType.secondaryAccountList)

                        binding.includeNominatedContactsListLyt.activeTxtLst.performClick()

                    } else {
                        binding.nominateContactBtn.performClick()
                    }

                } else {

                }
                Logg.logging("TESTSTR", "testess mList $mList")
            }
            is Resource.DataError -> {
                Logg.logging("TESTSTR", "testess errror ${status.data}")
            }
            else -> {

            }
        }
    }

    private fun createAccount(status: Resource<SecondaryAccountResp?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {

            }
            is Resource.DataError -> {

            }
            else -> {

            }
        }
    }

    private fun getSecondaryAccountsRes(status: Resource<GetSecondaryAccessRightsResp?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {

            }
            is Resource.DataError -> {

            }
            else -> {

            }
        }
    }

    private fun updateSecondaryAccountRes(status: Resource<ResponseBody?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {

            }
            is Resource.DataError -> {

            }
            else -> {

            }
        }
    }


    private fun updateSecondaryAccessRightsRes(status: Resource<ResponseBody?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {

            }
            is Resource.DataError -> {

            }
            else -> {

            }
        }
    }


}