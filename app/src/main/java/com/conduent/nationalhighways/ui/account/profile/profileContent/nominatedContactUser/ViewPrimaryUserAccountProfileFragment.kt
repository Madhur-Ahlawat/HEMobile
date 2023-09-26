package com.conduent.nationalhighways.ui.account.profile.profileContent.nominatedContactUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentViewPrimaryUserAccountProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewPrimaryUserAccountProfileFragment :
    BaseFragment<FragmentViewPrimaryUserAccountProfileBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentViewPrimaryUserAccountProfileBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        binding.model = arguments?.getParcelable(Constants.DATA)
        viewModel.accountDetail()
    }

    override fun initCtrl() {
        binding.btnEditDetail.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.accountDetail, ::handleAccountDetail)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEditDetail -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA, binding.model)
                findNavController().navigate(
                    R.id.action_viewPrimaryAccountProfile_to_UpdatePersonalInfo,
                    bundle
                )
            }
        }
    }

    private fun handleAccountDetail(status: Resource<ProfileDetailModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.run {
                    if (status.equals("500")) ErrorUtil.showError(binding.root, message)
                    else {
                        binding.model = this
                        setProfileView()
                    }
                }
            }
            is Resource.DataError -> {
                if (status.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                }else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }
            else -> {
            }
        }
    }

    private fun setProfileView() {
        when (sessionManager.getAccountType()) {
            Constants.PERSONAL_ACCOUNT -> {

            }
            Constants.BUSINESS_ACCOUNT -> {

            }
        }
    }


}