package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRaiseNewEnquiryBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RaiseNewEnquiryFragment : BaseFragment<FragmentRaiseNewEnquiryBinding>() {

    val viewModel: RaiseNewEnquiryViewModel by viewModels()
    private lateinit var navController: NavController

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRaiseNewEnquiryBinding =
        FragmentRaiseNewEnquiryBinding.inflate(inflater, container, false)

    override fun init() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_raise_request_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.raiseRequestRadio.visible()
            if (destination.id == R.id.enquiryCategoryFragment) {
                binding.categoryRb.isChecked = true
            } else if (destination.id == R.id.enquiryCommentsFragment) {
                binding.commentsRb.isChecked = true
            } else if (destination.id == R.id.enquirySummaryFragment) {
                binding.summaryRb.isChecked = true
            } else {
                binding.raiseRequestRadio.gone()
            }
        }


        binding.categoryRb.setOnClickListener {

        }
        binding.commentsRb.setOnClickListener {

        }
        binding.summaryRb.setOnClickListener {

        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

}