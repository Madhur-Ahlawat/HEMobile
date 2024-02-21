package com.conduent.nationalhighways.ui.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentLogoutBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.setToolBarTitle
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogoutFragment : BaseFragment<FragmentLogoutBinding>(), View.OnClickListener {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLogoutBinding.inflate(inflater, container, false)

    override fun init() {}

    override fun initCtrl() {
        binding.apply {
            btnSignin.setOnClickListener(this@LogoutFragment)
            btnStart.setOnClickListener(this@LogoutFragment)
        }
        showToolBar(true)
        setToolBarTitle(resources.getString(R.string.str_signed_out))
    }

    override fun observer() {

    }

    override fun onClick(v: View?) {

        v?.let {
            when (v.id) {
                R.id.btnStart -> {
                    findNavController().navigate(R.id.action_logoutFragment_to_startNow)
                }

                R.id.btnSignin -> {
                    requireActivity().startNewActivityByClearingStack(LoginActivity::class.java)
                }

                else -> {
                    // do nothing
                }
            }
        }
    }

}