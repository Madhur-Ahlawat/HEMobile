package com.heandroid.ui.account.profile.profileContent.nominatedContactUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.NominatedContactRes
import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.databinding.FragmentViewNominatedContactUserProfileBinding
import com.heandroid.ui.account.profile.ProfileActivity
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.nominatedcontacts.list.NominatedContactListViewModel
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject


@AndroidEntryPoint
class ViewNominatedContactUserProfileFragment  : BaseFragment<FragmentViewNominatedContactUserProfileBinding>(), View.OnClickListener{

    private val viewModel : ProfileViewModel by viewModels()
    private val nominatedcontactViewModel : NominatedContactListViewModel by viewModels()
    private var loader: LoaderDialog? = null
   val list: MutableList<SecondaryAccountData?> = ArrayList()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentViewNominatedContactUserProfileBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager,"")
        viewModel.accountDetail()
        nominatedcontactViewModel.nominatedContactList()
       // (requireActivity() as ProfileActivity).setHeaderTitle("Your details")
    }

    override fun initCtrl() {
        binding.btnEditDetail.setOnClickListener(this)
        binding.rlAccountHolder.setOnClickListener(this)
        binding.imvViewAccountHolderData.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.accountDetail,::handleAccountDetail)
        observe(nominatedcontactViewModel.contactList ,::handleNominatedContactData)
    }

    private fun handleNominatedContactData(status: Resource<NominatedContactRes?>?) {
        when (status) {
            is Resource.Success -> {

                if (!status.data?.secondaryAccountDetailsType?.secondaryAccountList.isNullOrEmpty()) {
                    list.clear()
                    list.addAll(status.data?.secondaryAccountDetailsType?.secondaryAccountList!!)
                   // list.filter { x -> x. ==   }
                } else {

                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnEditDetail -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA,binding.model)
                findNavController().navigate(R.id.action_viewNominatedUserAccountProfile_to_UpdatePersonalInfo,bundle)
            }

            R.id.rlAccountHolder->{
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA,binding.model)
                findNavController().navigate(R.id.action_viewNominatedContactUserProfile_to_viewPrimaryAccountHolderProfile,bundle)
            }

        }
    }

    private fun handleAccountDetail(status: Resource<ProfileDetailModel?>?){
        try {
            loader?.dismiss()
            when(status){
                is  Resource.Success -> {
                    status.data?.run {
                        if(status?.equals("500") == true) ErrorUtil.showError(binding.root, message)
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