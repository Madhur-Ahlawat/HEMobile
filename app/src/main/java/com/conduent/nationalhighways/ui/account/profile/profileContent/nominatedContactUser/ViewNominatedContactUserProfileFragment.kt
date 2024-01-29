package com.conduent.nationalhighways.ui.account.profile.profileContent.nominatedContactUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.nominatedcontacts.NominatedContactRes
import com.conduent.nationalhighways.data.model.nominatedcontacts.SecondaryAccountData
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentViewNominatedContactUserProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewNominatedContactUserProfileFragment :
    BaseFragment<FragmentViewNominatedContactUserProfileBinding>(), View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var ncId: String = ""
    val list: MutableList<SecondaryAccountData?> = ArrayList()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentViewNominatedContactUserProfileBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
//        (requireActivity() as ProfileActivity).showLoader()

        viewModel.accountDetail()
    }

    override fun initCtrl() {
        binding.btnEditDetail.setOnClickListener(this)
        binding.rlAccountHolder.setOnClickListener(this)
        binding.imvViewAccountHolderData.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.accountDetail, ::handleAccountDetail)
        observe(viewModel.getNominatedContactsApiVal, ::handleNominatedContactData)
    }

    private fun handleNominatedContactData(status: Resource<NominatedContactRes?>?) {
            loader?.dismiss()
//        (requireActivity() as ProfileActivity).hideLoader()

        when (status) {
            is Resource.Success -> {
                if (!status.data?.secondaryAccountDetailsType?.secondaryAccountList.isNullOrEmpty()) {
                    list.clear()
                    status.data?.secondaryAccountDetailsType?.secondaryAccountList?.let {
                        list.addAll(
                            it
                        )
                    }
                    for (item in list) {
                        if (item?.secAccountRowId.equals(ncId)) {
                            binding.nominated = item
                        }
                    }
                }
            }
            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel) ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }
            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEditDetail -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.NOMINATED_ACCOUNT_DATA, binding.nominated)
                bundle.putParcelable(Constants.DATA, binding.model)

            }

            R.id.rlAccountHolder -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA, binding.model)
                findNavController().navigate(
                    R.id.action_viewNominatedContactUserProfile_to_viewPrimaryAccountHolderProfile,
                    bundle
                )
            }

        }
    }

    private fun handleAccountDetail(status: Resource<ProfileDetailModel?>?) {
            loader?.dismiss()
//        (requireActivity() as ProfileActivity).hideLoader()

        when (status) {
            is Resource.Success -> {
                status.data?.run {
                    if (status.equals("500")) ErrorUtil.showError(binding.root, message)
                    else {
                        ncId = status.data.accountInformation?.ncId ?: ""
//                        (requireActivity() as ProfileActivity).showLoader()
                        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                        viewModel.getNominatedContacts()
                        binding.model = this
                    }
                }
            }
            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel) ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }
            else -> {
            }
        }
    }

}