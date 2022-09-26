package com.heandroid.ui.nominatedcontacts.invitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.databinding.FragmentNominatedInvitationAccessRightBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.Exception

@AndroidEntryPoint
class NominatedInvitationAccessRightFragment :
    BaseFragment<FragmentNominatedInvitationAccessRightBinding>(), View.OnClickListener {

    private val viewModel: NominatedInvitationViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var accountId: String? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNominatedInvitationAccessRightBinding =
        FragmentNominatedInvitationAccessRightBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.model = arguments?.getParcelable("data")
    }

    override fun initCtrl() {
        binding.apply {
            btnInvite.setOnClickListener(this@NominatedInvitationAccessRightFragment)
            btnCancel.setOnClickListener(this@NominatedInvitationAccessRightFragment)
            tvEditContact.setOnClickListener(this@NominatedInvitationAccessRightFragment)
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.createAccount, ::handleCreateAccountResponse)
            observe(viewModel.updateAccessRight, ::handleUpdateAccessResponse)
            observe(viewModel.updateAccount, ::handleUpdateAccountResponse)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnInvite -> {
                if (binding.rbRead.isChecked or binding.rbWrite.isChecked) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                    if (arguments?.getBoolean("edit") == true) viewModel.updateSecondaryAccountData(
                        arguments?.getParcelable(
                            "data"
                        )
                    )
                    else {
                        val request =
                            arguments?.getParcelable<CreateAccountRequestModel>("data")?.apply {
                                accessType = if (binding.rbWrite.isChecked) "READ-WRITE" else "READ"
                            }
                        viewModel.createAccount(request)
                    }

                } else {
                    showError(binding.root, "Please select access right")
                }

            }
            R.id.btnCancel -> {

                findNavController().popBackStack(R.id.ncFullNameFragment, true)
            }
            R.id.tvEditContact -> {
                val bundle = Bundle()
                bundle.putBoolean("edit", false)
                bundle.putBoolean("FromNormalEdit", true)
                bundle.putParcelable("data", binding.model)

                findNavController().navigate(
                    R.id.action_ncAcceessRightFragment_to_ncFullNameFragment,
                    bundle
                )

            }
        }
    }

    private fun updateAccessRight() {
        if (accountId?.isNotEmpty() == true) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            val permission = if (binding.rbRead.isChecked) "READ" else "READ-WRITE"
            val list = mutableListOf<UpdatePermissionModel?>()
            list.add(UpdatePermissionModel("Address", permission))
            list.add(UpdatePermissionModel("CompanyName", permission))
            list.add(UpdatePermissionModel("Telephone", permission))
            list.add(UpdatePermissionModel("VRM", permission))
            val updateAccessRightModel =
                UpdateAccessRightModel("updateSecAccessRights", accountId, list)
            viewModel.updateAccessRight(updateAccessRightModel)
        } else {
            requireActivity().showToast("Account creation failed")
        }
    }


    private fun handleCreateAccountResponse(status: Resource<CreateAccountResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode.equals("1224")) {
                    findNavController().navigate(R.id.action_ncAcceessRightFragment_to_ncListFragment)
                    showError(binding.root, status.data?.message)
                } else {
                    accountId = status.data?.secondaryAccountId ?: ""
                    updateAccessRight()

                }

            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }

        }

    }

    private fun handleUpdateAccountResponse(status: Resource<ResponseBody?>?) {
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {
                    accountId =
                        arguments?.getParcelable<CreateAccountRequestModel>("data")?.accountId
                    showError(binding.root, "Successfully updated account details")

                }
                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)
                }
            }
        } catch (e: Exception) {
        }

    }


    private fun handleUpdateAccessResponse(status: Resource<ResponseBody?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                findNavController().navigate(R.id.action_ncAcceessRightFragment_to_ncListFragment)
                showError(binding.root, "Successfully updated Access details")

            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }

        }

    }

}