package com.heandroid.ui.nominatedcontacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.databinding.FragmentNominatedContactsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import okhttp3.internal.filterList

@AndroidEntryPoint
class NominatedContactsFrag : BaseFragment<FragmentNominatedContactsBinding>(),
    NominatedContactListener {

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

    private var fullNameValue: String = ""
    private var mailValue: String = ""
    private var mobileNumberValue = ""

    private var readWrite: Boolean? = null
    private var permissionValue = ""
    private var accountId = ""
    private var lastNameValue = ""
    private var isEditClicked = false
    private var contactStatus = ""

    override fun initCtrl() {
        binding.apply {

            nominateContactBtn.setOnClickListener {

                binding.includeNominatedContactsListLyt.nominatedContactsListContainer.gone()
                youCanNominateTxt.gone()
                inviteNominatedContactTitle.visible()
                nominateContactBtn.gone()
                fullNameAndContactSubLyt.visible()
                tfFullName.visible()
                tfLastName.visible()
                nextBtn.visible()
                fullName.setStyleBold()
                fullName.setUnderLineTxt(getString(R.string.str_full_name))

            }

            nextBtn.setOnClickListener {

                if (!isEditClicked) {
                    if (edtFullName.text.toString().isNotEmpty() && edtLastName.text.toString()
                            .isNotEmpty()
                    ) {

                        fullNameValue = edtFullName.text.toString()
                        lastNameValue = edtLastName.text.toString()

                        tfEmail.visible()
                        tfMobileNo.visible()
                        tfFullName.gone()
                        tfLastName.gone()
                        fullName.text = getString(R.string.str_full_name)
                        fullName.setStyleNormal()
                        contact.setStyleBold()
                        contact.setUnderLineTxt(getString(R.string.str_contact))

                        if (edtEmail.text.toString().isNotEmpty()) {
                            mailValue = edtEmail.text.toString()
                            if (edtNumber.text.toString().isNotEmpty())
                                mobileNumberValue = edtNumber.text.toString()

                            setData()
                            hideViews()
                            val secBody = SecondaryAccountBody(
                                fullNameValue,
                                lastNameValue,
                                mailValue,
                                mobileNumberValue,
                                ""
                            )
                            nominatedContactsViewModel.createAccount(secBody)
                            loader?.show(requireActivity().supportFragmentManager, "TAG")
                            Logg.logging("TESTSTR", "testess create account started")

                            includeViewContactDetailsLyt.nominatedAccessRightsContainer.visible()
                        } else {
                            requireContext().showToast("Please enter email id correctly")
                        }

                    } else {
                        requireContext().showToast("Please enter nominee full name")
                    }
                } else {
                    mobileNumberValue = edtNumber.text.toString()
                    mailValue = edtEmail.text.toString()

                    val mUpdateAccount = UpdateSecAccountDetails(
                        accountId,
                        contactStatus,
                        mobileNumberValue,
                        mailValue
                    )
                    loader?.show(requireActivity().supportFragmentManager, "Update")
                    nominatedContactsViewModel.updateSecondaryAccountData(mUpdateAccount)
                    hideViews()
                    includeViewContactDetailsLyt.nominatedAccessRightsContainer.visible()
                    binding.includeViewContactDetailsLyt.emailAddressStr.text = mailValue
                    binding.includeViewContactDetailsLyt.mobileNumberStr.text = mobileNumberValue

                }

            }
            includeViewContactDetailsLyt.readWriteRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    readWrite = true
                    includeViewContactDetailsLyt.readOnlyRadioBtn.isChecked = false
                }

            }

            includeViewContactDetailsLyt.editContactDetails.setOnClickListener {

            }

            includeViewContactDetailsLyt.readOnlyRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    readWrite = false
                    includeViewContactDetailsLyt.readWriteRadioBtn.isChecked = false
                }

            }

            includeViewContactDetailsLyt.inviteBtn
                .setOnClickListener {
                    includeViewContactDetailsLyt.nominatedAccessRightsContainer.gone()
                    includeNominatedContactsListLyt.nominatedContactsListContainer.visible()

                    if (readWrite == null) {

                        ErrorUtil.showError(root, getString(R.string.str_pls_select_permissions))

                    } else {
                        permissionValue = if (readWrite!!) {
                            "READ-WRITE"
                        } else {
                            "READ"
                        }
                        if (accountId.isNotEmpty()) {
                            val mPermissionsList = mutableListOf<UpdateSecPermissions>()
                            val updatep1 = UpdateSecPermissions("Address", permissionValue)
                            val updatep2 = UpdateSecPermissions("CompanyName", permissionValue)
                            val updatep3 = UpdateSecPermissions("Telephone", permissionValue)
                            val updatep4 = UpdateSecPermissions("VRM", permissionValue)
                            mPermissionsList.add(updatep1)
                            mPermissionsList.add(updatep2)
                            mPermissionsList.add(updatep3)
                            mPermissionsList.add(updatep4)
                            val secondaryAccessRights =
                                UpdateSecAccessRightsReq(
                                    "updateSecAccessRights",
                                    accountId,
                                    mPermissionsList
                                )
                            nominatedContactsViewModel.updateSecondaryAccessRightsData(
                                secondaryAccessRights
                            )
                        } else {
                            requireActivity().showToast("Missing")
                        }
                    }

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
                    ErrorUtil.showError(
                        root,
                        getString(R.string.str_nominated_contacts_limit_reached)
                    )
                } else {
                    lytEditInput.visible()
                    binding.tfFullName.visible()
                    nominateContactBtn.performClick()
                }

            }

        }

    }

    private val mList = ArrayList<SecondaryAccountData?>()
    private val mFilteredList = ArrayList<SecondaryAccountData?>()
    lateinit var mAdapter: NominatedContactsAdapter
    private fun setNominatedContactsAdapter(mTempList: ArrayList<SecondaryAccountData?>) {
        if (mTempList.isNotEmpty()) {
            binding.includeNominatedContactsListLyt.nominatedContactsListContainer.visible()
            binding.includeNominatedContactsListLyt.nominatedContactRecyclerView.visible()
            binding.includeNominatedContactsListLyt.noContacts.gone()
            if (mFilteredList.isNotEmpty())
                mFilteredList.clear()

            mFilteredList.addAll(mTempList)
            mAdapter = NominatedContactsAdapter(requireContext(), this)
            mAdapter.setList(mFilteredList)
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
                        binding.idNominateContactTitleLyt.visible()
                        binding.youCanNominateTxt.visible()
                        binding.lytEditInput.visible()
                        binding.inviteNominatedContactTitle.gone()
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
        Logg.logging("TESTSTR", "testess createAccount called")

        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                Logg.logging("TESTSTR", "testess createAccount called status ${status.data}")
                if (status.data!!.message.equals("SUCCESS", true)) {
                    accountId = status.data.secondaryAccountId

                    ErrorUtil.showError(
                        binding.root,
                        "Account created successfully, Please set permissions for the account"
                    )

                } else {
                    ErrorUtil.showError(binding.root, status.data.message)
                    binding.includeNominatedContactsListLyt.nominatedContactsListContainer.visible()
                    binding.includeNominatedContactsListLyt.nominatedContactRecyclerView.visible()
                    binding.includeViewContactDetailsLyt.nominatedAccessRightsContainer.gone()
                    hideViews()

                }

            }
            is Resource.DataError -> {

            }
            else -> {

            }
        }
    }

    private var selPos = -1

    private

    fun getSecondaryAccountsRes(status: Resource<GetSecondaryAccessRightsResp?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                Logg.logging(
                    "TESTSTR",
                    "testess errror getSecondaryAccountsRes sucess  ${status.data}"
                )
                Logg.logging(
                    "TESTSTR",
                    "testess errror getSecondaryAccountsRes mFilteredList.size  ${mFilteredList.size}"
                )
                Logg.logging(
                    "TESTSTR",
                    "testess errror getSecondaryAccountsRes selPos  $selPos"
                )
                if (mFilteredList.size > 0 && selPos >= 0 && selPos < mFilteredList.size) {

                    if (status.data!!.accessRights.accessVo.size > 0 && status.data!!.accessRights.accessVo[0].value.equals(
                            "READ",
                            true
                        )
                    )
                        mFilteredList[selPos]!!.mPermissionLevel = "Read Only"
                    else
                        mFilteredList[selPos]!!.mPermissionLevel = "Amend Account, Vehicle data"

                    mAdapter?.apply {
                        notifyItemChanged(selPos)
                    }


                }

            }
            is Resource.DataError -> {
                Logg.logging(
                    "TESTSTR",
                    "testess errror getSecondaryAccountsRes errormsg ${status.errorMsg}"
                )

            }
            else -> {

            }
        }
    }

    private fun updateSecondaryAccountRes(status: Resource<ResponseBody?>) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                Logg.logging(
                    "TESTSTR",
                    "testess errror updateSecondaryAccessRightsRes  ${status.data}"
                )
                ErrorUtil.showError(
                    binding.root,
                    "Updated details successfully, Please set permissions for the account"
                )

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
                Logg.logging(
                    "TESTSTR",
                    "testess errror updateSecondaryAccessRightsRes  ${status.data}"
                )
                loader?.show(requireActivity().supportFragmentManager, "")
                nominatedContactsViewModel.nominatedContactListFetch()

            }
            is Resource.DataError -> {
                Logg.logging(
                    "TESTSTR",
                    "testess errror updateSecondaryAccessRightsRes ${status.errorMsg}"
                )

            }
            else -> {

            }
        }
    }

    private fun setData() {

        binding.apply {
            includeViewContactDetailsLyt.fullNameStr.text = edtFullName.text.toString()
            includeViewContactDetailsLyt.emailAddressStr.text = edtEmail.text.toString()
            includeViewContactDetailsLyt.mobileNumberStr.text = edtNumber.text.toString()
        }
    }

    override fun onItemClick(
        type: String,
        data: SecondaryAccountData,
        pos: Int,
        isExpanded: Boolean
    ) {
        Logg.logging(
            "TESTSTR",
            "testess createAccount activity called type $type  expamded $isExpanded rowID ${data.secAccountRowId}"
        )

        when (type) {

            "open" -> {

                if (isExpanded) {
                    selPos = pos
                    loader?.show(requireActivity().supportFragmentManager, "Load")
                    nominatedContactsViewModel.getSecondaryRightsData(data.secAccountRowId)
                }
            }

            "Resend" -> {

            }
            "Remove" -> {

            }
            "Edit" -> {
                binding.apply {

                    includeNominatedContactsListLyt.nominatedContactsListContainer.gone()
                    isEditClicked = true
                    tfEmail.visible()
                    fullNameAndContactSubLyt.visible()
                    tfMobileNo.visible()
                    tfFullName.gone()
                    tfLastName.gone()
                    nextBtn.visible()
                    fullName.text = getString(R.string.str_full_name)
                    fullName.setStyleNormal()
                    contact.setStyleBold()
                    contact.setUnderLineTxt(getString(R.string.str_contact))
                    binding.idNominateContactTitleLyt.visible()
                    binding.youCanNominateTxt.gone()
                    binding.lytEditInput.visible()
                    binding.includeViewContactDetailsLyt.fullNameStr.text =
                        "${data.firstName}${data.lastName}"
                    binding.inviteNominatedContactTitle.visible()
                    edtEmail.setText(data.emailAddress)
                    edtNumber.setText(data.phoneNumber)
                    accountId = data.secAccountRowId
                    contactStatus = data.accountStatus

                    readWrite =
                        !(data.mPermissionLevel!!.isNotEmpty() && data.mPermissionLevel.equals(
                            "READ",
                            true
                        ))
                    includeViewContactDetailsLyt.readWriteRadioBtn.isChecked = readWrite!!
                    includeViewContactDetailsLyt.readOnlyRadioBtn.isChecked = readWrite!!
                }
            }
        }
    }

}