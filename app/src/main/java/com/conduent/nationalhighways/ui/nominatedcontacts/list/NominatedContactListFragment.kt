package com.conduent.nationalhighways.ui.nominatedcontacts.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.nominatedcontacts.*
import com.conduent.nationalhighways.databinding.FragmentNominatedContactListBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject


@AndroidEntryPoint
class NominatedContactListFragment : BaseFragment<FragmentNominatedContactListBinding>(),
    View.OnClickListener, NominatedContactListener {
    private val viewModel: NominatedContactListViewModel by viewModels()
    private val list: MutableList<SecondaryAccountData?> = ArrayList()

    private var filterList: MutableList<SecondaryAccountData?>? = ArrayList()
    private var selectedPosition = 0
    @Inject
    lateinit var sessionManager: SessionManager


    private var loader: LoaderDialog? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNominatedContactListBinding =
        FragmentNominatedContactListBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.nominatedContactList()
    }

    override fun initCtrl() {
        binding.apply {
            binding.btnActive.setOnClickListener(this@NominatedContactListFragment)
            binding.btnInvited.setOnClickListener(this@NominatedContactListFragment)
            binding.btnNominatedContact.setOnClickListener(this@NominatedContactListFragment)
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.contactList, ::handleContactListResponse)
            observe(viewModel.getSecondaryRights, ::handleAccessRightResponse)
            observe(viewModel.terminateNominatedContact, ::handleTerminatedContactsResp)
            observe(viewModel.getResendActivationMail, ::handleResendActivationMail)
        }
    }


    private fun handleContactListResponse(status: Resource<NominatedContactRes?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {

                if (!status.data?.secondaryAccountDetailsType?.secondaryAccountList.isNullOrEmpty()) {
                    list.clear()
                    list.addAll(status.data?.secondaryAccountDetailsType?.secondaryAccountList!!)
                    binding.btnActive.performClick()
                } else {

                }
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnActive -> {
                val activeList = list.filter { it?.accountStatus.equals("ACTIVE", true) }
                setDataToUI(activeList)


                binding.btnActive.changeTextColor(R.color.high_lighted_text_color)
                binding.btnActive.changeBackgroundColor(R.color.dark_blue_color)
                binding.btnActive.setStrokeColorResource(R.color.dark_blue_color)
                /*binding.btnActive.backgroundTintList =
                    requireActivity().getColorStateList(R.color.dark_blue_color)*/

                binding.btnInvited.changeTextColor(R.color.unselected_tab_color)
                binding.btnInvited.setStrokeColorResource(R.color.unselected_tab_color)
                binding.btnInvited.changeBackgroundColor(android.R.color.transparent)
                /* binding.btnInvited.backgroundTintList =
                        requireActivity().getColorStateList(R.color.white)*/
            }
            R.id.btnInvited -> {
                val invitedList = list.filter { it?.accountStatus.equals("INITIATED", true)||it?.accountStatus.equals(Constants.EXPIRED,true) }
                setDataToUI(invitedList)

                binding.btnInvited.changeTextColor(R.color.high_lighted_text_color)
                binding.btnInvited.changeBackgroundColor(R.color.dark_blue_color)
                binding.btnInvited.setStrokeColorResource(R.color.dark_blue_color)
                /*binding.btnActive.backgroundTintList =
                    requireActivity().getColorStateList(R.color.dark_blue_color)*/

                binding.btnActive.changeTextColor(R.color.unselected_tab_color)
                binding.btnActive.setStrokeColorResource(R.color.unselected_tab_color)
                binding.btnActive.changeBackgroundColor(android.R.color.transparent)
                // binding.btnInvited.changeBackgroundColor(R.color.blue_color)
                /*binding.btnInvited.backgroundTintList =
                    requireActivity().getColorStateList(R.color.white)*/
            }

            R.id.btnNominatedContact -> {

                if (sessionManager.fetchAccountType()
                        .equals(
                            Constants.PERSONAL_ACCOUNT,
                            true
                        ) && sessionManager.fetchSubAccountType()
                        .equals(Constants.STANDARD, true)
                ) {
                    if (list.size >= 2) showError(
                        binding.root,
                        getString(R.string.str_nominated_contacts_limit_reached,2)
                    )
                    else findNavController().navigate(R.id.action_ncListFragment_to_ncFullNameFragment)

                }


                if (sessionManager.fetchAccountType()
                        .equals(
                            Constants.BUSINESS_ACCOUNT,
                            true)) {

                    if (list.size >= 5) showError(
                        binding.root,
                        getString(R.string.str_nominated_contacts_limit_reached,5)
                    )
                    else findNavController().navigate(R.id.action_ncListFragment_to_ncFullNameFragment)

                }

            }
        }
    }


    private fun setDataToUI(list: List<SecondaryAccountData?>?) {
        this.filterList = list?.toMutableList()
        if (this.filterList!!.size > 0) {
            binding.rvNoItemsTxt.gone()
            binding.rvList.visible()
            val nominatedAdapter = NominatedContactsAdapter(requireContext(), filterList, this)
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = nominatedAdapter
            }

        } else {
            binding.rvNoItemsTxt.visible()
            binding.rvList.gone()

        }
    }


    private fun handleAccessRightResponse(status: Resource<GetSecondaryAccessRightsResp?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.accessRights?.accessVo?.isNotEmpty() == true) {
                    if (status.data.accessRights.accessVo[0]?.value.equals(
                            "READ",
                            true
                        )
                    ) filterList?.get(selectedPosition)?.mPermissionLevel = "Limited Access"
                    else filterList?.get(selectedPosition)?.mPermissionLevel =
                        "Full Access"
                } else filterList?.get(selectedPosition)?.mPermissionLevel =
                    "Full Access"

                binding.rvList.adapter?.notifyItemChanged(
                    selectedPosition,
                    list[selectedPosition]
                )
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handleTerminatedContactsResp(status: Resource<ResponseBody?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {

                showError(binding.root, "Contact Removed Successfully")
                val remo = list.indexOf(filterList?.get(selectedPosition))
                list.remove(list[remo])

                if(filterList!!.isNotEmpty() && filterList!!.size > 0){
                    filterList?.removeAt(selectedPosition)

                    binding.rvList.adapter?.notifyItemRemoved(
                        selectedPosition
                    )
                }
                Logg.logging("AccountFragment", "filterList size ${filterList!!.size}")

                if (filterList!!.isEmpty() && filterList!!.size <= 0) {
                    findNavController().navigate(
                        R.id.action_ncListFragment_to_ncNoListFragment
                    )
                }
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }

        }

    }

    private fun handleResendActivationMail(status: Resource<ResendRespModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.status.equals("0")) {
                    showError(binding.root, getString(R.string.resend_success))
                } else {
                    showError(binding.root, status.errorMsg)
                }
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }

    }

    override fun onItemClick(
        type: String,
        data: SecondaryAccountData,
        pos: Int,
        isExpanded: Boolean
    ) {
        when (type) {

            "open" -> {
                if (isExpanded) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    selectedPosition = pos
                    data.secAccountRowId?.let { viewModel.getSecondaryRights(it) }
                }
            }

            "Resend" -> {
                Log.v("ListFrag", " onItemClick Resend called data $data  type $type   ")
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                val model = ResendActivationMail(data.secAccountRowId, "Y")
                viewModel.resendActivationMailContacts(model)

            }

            "Remove" -> {
                Log.v("ListFrag", " onItemClick Remove called data $data  type $type   ")

                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                selectedPosition = pos

                data.let {
                    if (it.secAccountRowId?.isNotEmpty() == true) {
                        val model =
                            TerminateRequestModel(
                                it.secAccountRowId,
                                Constants.STATUS_TERMINATED,
                                data.phoneNumber,
                                data.emailAddress,
                                data.firstName,
                                data.lastName
                            )
                        viewModel.terminateNominatedContact(model)
                    }

                }

            }

            "Edit" -> {
                val model = CreateAccountRequestModel(
                    firstName = data.firstName,
                    lastName = data.lastName,
                    emailId = data.emailAddress,
                    phoneNumber = data.phoneNumber,
                    accountId = data.secAccountRowId,
                    status = data.accountStatus
                )
                val bundle = Bundle()
                bundle.putBoolean("edit", true)
                bundle.putParcelable("data", model)
                findNavController().navigate(
                    R.id.action_ncListFragment_to_ncFullNameFragment,
                    bundle
                )
            }
        }
    }
}