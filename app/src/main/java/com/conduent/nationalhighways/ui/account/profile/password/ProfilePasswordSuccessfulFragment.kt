package com.conduent.nationalhighways.ui.account.profile.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentProfilePasswordSuccessfulBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.startNormalActivity
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