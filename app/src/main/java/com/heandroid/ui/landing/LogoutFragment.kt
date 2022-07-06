package com.heandroid.ui.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.databinding.FragmentLogoutBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.setRightButtonText
import com.heandroid.utils.extn.startNewActivityByClearingStack
import com.heandroid.utils.extn.visible

class LogoutFragment : BaseFragment<FragmentLogoutBinding>(), View.OnClickListener {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLogoutBinding {
        return FragmentLogoutBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is LandingActivity) {
            val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
            toolbar.findViewById<TextView>(R.id.btn_login).visible()
            requireActivity().setRightButtonText(getString(R.string.contact_us))
        }

    }

    override fun init() {}

    override fun initCtrl() {
        binding.apply {
            btnSignin.setOnClickListener(this@LogoutFragment)
            btnStart.setOnClickListener(this@LogoutFragment)
        }
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
                    requireActivity().startNewActivityByClearingStack(AuthActivity::class.java)
                }

                else -> {
                    // do nothing
                }
            }
        }
    }

}