package com.heandroid.ui.auth.session

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
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionExpireFragment :  BaseFragment<FragmentSessionExpireBinding>(), View.OnClickListener {
    private var type : String?=null

    override fun onResume() {
        super.onResume()
        val toolbar=requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.tvContactUs).gone()

    }

    override fun init() {
        type=arguments?.getString("type")

        when(type) {

            "LOGIN"  -> { 
                binding.tvLabel.text=getString(R.string.select_the_sign_in_button_to_log_in_to_your_account)
                binding.btn.text=getString(R.string.txt_sign_in)
            }

            "SIGN IN" -> {
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
                    "LOGIN" ->{ requireActivity().startActivity(Intent(requireActivity(),AuthActivity::class.java)) }
                  //  "SIGN IN" ->{ requireActivity().startActivity(Intent(requireActivity(),ActivityHome::class.java)) }
                }
            }
        }
    }
}