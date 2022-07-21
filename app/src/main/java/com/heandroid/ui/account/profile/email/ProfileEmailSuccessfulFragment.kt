package com.heandroid.ui.account.profile.email

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.heandroid.R
import com.heandroid.databinding.FragmentProfileEmailSuccessfulBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.startNormalActivity
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
        requireActivity().findViewById<AppCompatTextView>(R.id.tvYourDetailLabel).gone()
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