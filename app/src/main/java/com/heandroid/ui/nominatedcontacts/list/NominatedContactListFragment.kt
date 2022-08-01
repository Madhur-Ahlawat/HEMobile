package com.heandroid.ui.nominatedcontacts.list

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
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.databinding.FragmentNominatedContactListBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception


@AndroidEntryPoint
class NominatedContactListFragment : BaseFragment<FragmentNominatedContactListBinding>(),
    View.OnClickListener, NominatedContactListener {
    private val viewModel: NominatedContactListViewModel by viewModels()
    private val list: MutableList<SecondaryAccountData?> = ArrayList()

    private var filterList: MutableList<SecondaryAccountData?>? = ArrayList()
    private var selectedPosition = 0


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
        loader?.dismiss()
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
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnActive -> {
                val activeList = list.filter { it?.accountStatus.equals("ACTIVE", true) }
                setDataToUI(activeList)


                binding.btnActive.changeTextColor(R.color.white)
                binding.btnActive.changeBackgroundColor(R.color.green)
                binding.btnActive.backgroundTintList =
                    requireActivity().getColorStateList(R.color.green)

                binding.btnInvited.changeTextColor(R.color.green)
                binding.btnInvited.changeBackgroundColor(R.color.white)
                binding.btnInvited.backgroundTintList =
                    requireActivity().getColorStateList(R.color.white)
            }
            R.id.btnInvited -> {
                val invitedList = list.filter { it?.accountStatus.equals("INITIATED", true) }
                setDataToUI(invitedList)

                binding.btnActive.changeTextColor(R.color.green)
                binding.btnActive.changeBackgroundColor(R.color.white)
                binding.btnActive.backgroundTintList =
                    requireActivity().getColorStateList(R.color.white)

                binding.btnInvited.changeTextColor(R.color.white)
                binding.btnInvited.changeBackgroundColor(R.color.green)
                binding.btnInvited.backgroundTintList =
                    requireActivity().getColorStateList(R.color.green)

            }

            R.id.btnNominatedContact -> {
                if (list?.size >= 5) showError(
                    binding.root,
                    getString(R.string.str_nominated_contacts_limit_reached)
                )
                else findNavController().navigate(R.id.action_ncListFragment_to_ncFullNameFragment)
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
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {
                    if (status.data?.accessRights?.accessVo?.isNotEmpty() == true) {
                        if (status.data.accessRights.accessVo[0]?.value.equals(
                                "READ",
                                true
                            )
                        ) filterList?.get(selectedPosition)?.mPermissionLevel = "Read Only"
                        else filterList?.get(selectedPosition)?.mPermissionLevel =
                            "Amend Account, Vehicle data"
                    } else filterList?.get(selectedPosition)?.mPermissionLevel =
                        "Amend Account, Vehicle data"


                    binding.rvList.adapter?.notifyItemChanged(
                        selectedPosition,
                        list[selectedPosition]
                    )
                }
                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun handleTerminatedContactsResp(status: Resource<ResponseBody?>?) {
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {

                    showError(binding.root, "Contact Removed Successfully")
                    Log.v(
                        "ListFrag",
                        " handleTerminatedContactsResp   succes called ${status.data}   "
                    )

                    val remo = list.indexOf(filterList?.get(selectedPosition))
                    list.remove(list[remo])

                    filterList?.removeAt(selectedPosition)

                    binding.rvList.adapter?.notifyItemRemoved(
                        selectedPosition
                    )
                }
                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)
                    Log.v(
                        "ListFrag",
                        " handleTerminatedContactsResp   error called ${status.data}   "
                    )

                }
            }
        } catch (e: Exception) {
        }

    }

    private fun handleResendActivationMail(status: Resource<ResendRespModel?>?) {
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {
                    Log.v(
                        "ListFrag",
                        " handleResendActivationMail   succes called ${status.data?.message}   "
                    )
                    if (status.data?.status.equals("0")) {
                        showError(binding.root, getString(R.string.resend_success))
                    } else {
                        showError(binding.root, status.errorMsg)
                    }

                }
                is Resource.DataError -> {
                    Log.v(
                        "ListFrag",
                        " handleResendActivationMail   error called ${status.data}   "
                    )

                    showError(binding.root, status.errorMsg)
                }
            }
        } catch (e: Exception) {
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