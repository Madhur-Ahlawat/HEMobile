package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentUsedUnusedOptionsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidOptionsFragment : BaseFragment<FragmentUsedUnusedOptionsBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUsedUnusedOptionsBinding.inflate(inflater, container, false)

    override fun init() {

        AdobeAnalytics.setScreenTrack(
            "check crossings:select crossing type",
            "select crossing type",
            "english",
            "check crossings",
            "home",
            "check crossings:select crossing type",
            sessionManager.getLoggedInUser()
        )


    }

    override fun initCtrl() {
        binding.rlUnUsedCrossings.setOnClickListener(this)
        binding.rlUsedCrossings.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_un_used_crossings -> {
                findNavController().navigate(
                    R.id.action_checkChargesOption_to_unUsedCharges,
                    arguments
                )
            }
            R.id.rl_used_crossings -> {
                findNavController().navigate(R.id.action_checkChargesOption_to_usedCharges)
            }
        }
    }
}