package com.heandroid.ui.account.profile.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentProfilePasswordSuccessfulBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePasswordSuccessfulFragment : BaseFragment<FragmentProfilePasswordSuccessfulBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager : SessionManager


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)= FragmentProfilePasswordSuccessfulBinding.inflate(inflater,container,false)
    override fun init() {
        binding.data=arguments?.getParcelable(Constants.DATA)
    }

    override fun initCtrl() {
        binding.btnLogin.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnLogin ->{
                sessionManager.clearAll()
                requireActivity().finish()
                requireActivity().startNormalActivity(AuthActivity::class.java)
            }
        }
    }
}