package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentSessionExpireBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionExpireFragment : BaseFragment<FragmentSessionExpireBinding>(), View.OnClickListener {

    private var type: String? = null

    override fun init() {
        type = arguments?.getBundle(Constants.TYPE)?.getString(Constants.TYPE)

        when (type) {

            Constants.LOGIN -> {
               /* binding.tvLabel.text =
                    getString(R.string.select_the_sign_in_button_to_log_in_to_your_account)*/
                binding.btn.text = getString(R.string.txt_sign_in)
            }

            Constants.REFRESH_TOKEN -> {
                // to do refresh token ("Start Again" button click )
             /*   binding.tvLabel.text =
                    getString(R.string.select_the_start_now_button_to_restart_your_session)*/
                binding.btn.text = getString(R.string.start_again)
            }
        }
    }

    override fun initCtrl() {

        binding.btn.setOnClickListener(this)
    }


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSessionExpireBinding =
        FragmentSessionExpireBinding.inflate(inflater, container, false)

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn -> {
                requireActivity().finish()
                when (type) {
                    Constants.LOGIN -> {
                        requireActivity().startNewActivityByClearingStack(LoginActivity::class.java)

//                        requireActivity().startNormalActivity(LoginActivity::class.java)

                    }
                    //  "SIGN IN" ->{ requireActivity().startActivity(Intent(requireActivity(),ActivityHome::class.java)) }
                    Constants.REFRESH_TOKEN -> {// refresh token api call
                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)
                    }
                }
            }
        }
    }
}