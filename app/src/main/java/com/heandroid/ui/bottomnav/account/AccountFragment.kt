package com.heandroid.ui.bottomnav.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.FragmentAccountBinding
import com.heandroid.ui.account.communication.CommunicationActivity
import com.heandroid.ui.account.profile.ProfileActivity
import com.heandroid.ui.auth.logout.LogoutDialog
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAccountBinding = FragmentAccountBinding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.apply {
            profile.setOnClickListener(this@AccountFragment)
            rlAccount.setOnClickListener(this@AccountFragment)
            logOutLyt.setOnClickListener(this@AccountFragment)
        }
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.profile -> { requireActivity().startNewActivity(ProfileActivity::class.java) }
            R.id.rl_account -> { requireActivity().startNewActivity(CommunicationActivity::class.java) }

            R.id.log_out_lyt ->{
               val dialog = LogoutDialog()
               val bundle = Bundle()
               bundle.putString("title",getString(R.string.logout))
               bundle.putString("desc",getString(R.string.sure_wants_logout))
               dialog.arguments=bundle
               dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
               dialog.show(requireActivity().supportFragmentManager, "")
            }

        }
    }

}