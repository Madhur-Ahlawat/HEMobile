package com.conduent.nationalhighways.ui.account.profile.email

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentProfileEmailSuccessfulBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileEmailSuccessfulFragment : BaseFragment<FragmentProfileEmailSuccessfulBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileEmailSuccessfulBinding.inflate(inflater, container, false)

    override fun init() {
        binding.data = arguments?.getParcelable(Constants.DATA)
    }

    override fun initCtrl() {
        binding.btnLogin.setOnClickListener(this)
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                sessionManager.clearAll()
                requireActivity().finish()
                requireActivity().startNormalActivity(AuthActivity::class.java)
            }
        }
    }
}