package com.heandroid.ui.bottomnav.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.NominatedContactRes
import com.heandroid.databinding.FragmentAccountBinding
import com.heandroid.ui.account.communication.CommunicationActivity
import com.heandroid.ui.account.profile.ProfileActivity
import com.heandroid.ui.auth.logout.LogoutDialog
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.account.payments.AccountPaymentActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.nominatedcontacts.NominatedContactActivity
import com.heandroid.ui.nominatedcontacts.list.NominatedContactListViewModel
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(), View.OnClickListener {

    private val viewModel: NominatedContactListViewModel by viewModels()
    private var loader: LoaderDialog?=null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            profile.setOnClickListener(this@AccountFragment)
            payment.setOnClickListener(this@AccountFragment)
            rlAccount.setOnClickListener(this@AccountFragment)
            logOutLyt.setOnClickListener(this@AccountFragment)
            nominatedContactsLyt.setOnClickListener(this@AccountFragment)
        }

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.contactList,::handleContactListResponse)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.profile -> {
                requireActivity().startNewActivity(ProfileActivity::class.java)
            }

            R.id.payment -> {
                requireActivity().startNewActivity(AccountPaymentActivity::class.java)
            }

            R.id.rl_account -> {
                requireActivity().startNewActivity(CommunicationActivity::class.java)
            }

            R.id.nominated_contacts_lyt -> {
                loader?.show(requireActivity().supportFragmentManager,"")
                viewModel.nominatedContactList()

            }

            R.id.log_out_lyt -> {
                val dialog = LogoutDialog()
                Bundle().run{
                    putString("title", getString(R.string.logout))
                    putString("desc", getString(R.string.sure_wants_logout))
                    dialog.arguments = this
                }
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                dialog.show(requireActivity().supportFragmentManager, "Logout Dialog")
            }

        }
    }

    private fun handleContactListResponse(status: Resource<NominatedContactRes?>?){
        try{
        loader?.dismiss()
        when(status){
            is Resource.Success -> {
               Intent(requireActivity(), NominatedContactActivity::class.java).run {
                     flags= Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                     putExtra("count",status.data?.secondaryAccountDetailsType?.secondaryAccountList?.size?:0)
                    startActivity(this)
                }

            }
            is Resource.DataError ->{
                ErrorUtil.showError(binding.root, status.errorMsg)
            }
        }}catch (e: Exception){}
    }


}