package com.heandroid.ui.account.profile.profileContent.payGAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.databinding.FragmentViewNominatedContactUserProfileBinding
import com.heandroid.databinding.FragmentViewPaygAccountUserBinding
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ViewPayGAccountUserProfileFragment :  BaseFragment<FragmentViewPaygAccountUserBinding> (),View.OnClickListener{
    private val viewModel : ProfileViewModel by viewModels()
    // private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )=FragmentViewPaygAccountUserBinding.inflate(inflater, container, false)

    override fun init() {
//        loader = LoaderDialog()
//        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
//        loader?.show(requireActivity().supportFragmentManager,"")
        viewModel.accountDetail()
    }

    override fun initCtrl() {
        binding.btnEditDetail.setOnClickListener(this)
        binding.rlAdditionalDetails.setOnClickListener(this)
        binding.imvViewAdditionalData.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.accountDetail,::handleAccountDetail)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnEditDetail -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA,binding.model)
                findNavController().navigate(R.id.action_viewPaygAccountProfile_to_UpdatePersonalInfo,bundle)
            }

            R.id.rlAdditionalDetails,
            R.id.imvViewAdditionalData->{
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA,binding.model)
                findNavController().navigate(R.id.action_viewPaygAccountProfile_to_viewAdditionalDetails,bundle)
            }

        }
    }

    private fun handleAccountDetail(status: Resource<ProfileDetailModel?>?){
        try {
            // loader?.dismiss()
            when(status){
                is  Resource.Success -> {
                    status.data?.run {
                        if(status?.equals("500")) ErrorUtil.showError(binding.root, message)
                        else
                        {binding.model= this
                            setProfileView()}
                    }
                }
                is  Resource.DataError ->{
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }
        }catch (e: Exception){}
    }

    private fun setProfileView() {

        when(sessionManager.getAccountType())
        {
            Constants.PERSONAL_ACCOUNT->{

            }

            Constants.BUSINESS_ACCOUNT->{

            }
        }
    }


}