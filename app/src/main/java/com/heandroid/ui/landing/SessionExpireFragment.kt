package com.heandroid.ui.landing

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.databinding.FragmentSessionExpireBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionExpireFragment :  BaseFragment<FragmentSessionExpireBinding>(), View.OnClickListener {
    private var type : String?=null

    override fun onResume() {
        super.onResume()
        val toolbar=requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
        toolbar.findViewById<TextView>(R.id.btn_login).gone()
    }

    override fun init() {
        type=arguments?.getString(Constants.TYPE)

        when(type) {

            Constants.LOGIN  -> {
                // to do login again
                binding.tvLabel.text=getString(R.string.select_the_sign_in_button_to_log_in_to_your_account)
                binding.btn.text=getString(R.string.txt_sign_in)
            }

            Constants.REFRESH_TOKEN -> {
                // to do refresh token ("Start Again" button click )
                binding.tvLabel.text=getString(R.string.select_the_start_now_button_to_restart_your_session)
                binding.btn.text=getString(R.string.start_again)
            }
        }
    }
    override fun initCtrl(){

        binding.btn.setOnClickListener(this)
    }


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSessionExpireBinding = FragmentSessionExpireBinding.inflate(inflater,container)

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn -> {
                requireActivity().finish()
                when(type) {
                    Constants.LOGIN ->{ requireActivity().startActivity(Intent(requireActivity(),AuthActivity::class.java)) }
                  //  "SIGN IN" ->{ requireActivity().startActivity(Intent(requireActivity(),ActivityHome::class.java)) }
                    Constants.REFRESH_TOKEN->{// refresh token api call
                        requireActivity().finish()
                        requireActivity().startActivity(Intent(requireActivity(),HomeActivityMain::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    }
                }
            }
        }
    }
}