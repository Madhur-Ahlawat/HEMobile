package com.heandroid.ui.account.profile.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentProfilePasswordSuccessfulBinding
import com.heandroid.ui.account.profile.ProfileActivity
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePasswordSuccessfulFragment : BaseFragment<FragmentProfilePasswordSuccessfulBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private var updatePinFlow = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfilePasswordSuccessfulBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is ProfileActivity)
            requireActivity().findViewById<AppCompatTextView>(R.id.tvYourDetailLabel).gone()
        binding.data = arguments?.getParcelable(Constants.DATA)
        updatePinFlow = arguments?.getBoolean(Constants.UPDATE_PIN_FLOW, false) == true
        if (updatePinFlow) {
            binding.tvVerification.text = getString(R.string.update_pin_success)
            binding.btnLogin.text = getString(R.string.str_account_summary)
        }
    }

    override fun initCtrl() {
        binding.btnLogin.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                if (updatePinFlow) {
                    findNavController().navigate(R.id.action_updatePasswordSuccessfulFragment_to_viewProfile)
                } else {
                    sessionManager.clearAll()
                    requireActivity().finish()
                    requireActivity().startNormalActivity(AuthActivity::class.java)
                }
            }
        }
    }
}