package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRaiseNewEnquiryBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.common.Constants
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
        Log.e("TAG", "init: backButton " + backButton)

        binding.toolBarLyt.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_raise_request_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.raiseRequestRadio.visible()
            when (destination.id) {
                R.id.enquiryCategoryFragment -> binding.categoryRb.isChecked = true
                R.id.enquiryCommentsFragment -> binding.commentsRb.isChecked = true
                R.id.enquirySummaryFragment -> binding.summaryRb.isChecked = true
                else -> binding.raiseRequestRadio.gone()
            }

            when (destination.id) {
                R.id.enquiryCategoryFragment,
                R.id.enquiryCommentsFragment,
                R.id.enquiryContactDetailsFragment,
                R.id.enquirySummaryFragment,
                R.id.enquirySuccessFragment -> {
                    binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
                }

                else -> {
                    binding.toolBarLyt.titleTxt.text = getString(R.string.str_cases_and_enquiries)
                }
            }

            when (destination.id) {


                R.id.enquirySuccessFragment -> {
                    binding.toolBarLyt.backButton.gone()
                }

                R.id.enquiryCategoryFragment -> {
                    if (backButton) {
                        binding.toolBarLyt.backButton.visible()
                    } else {
                        binding.toolBarLyt.backButton.gone()
                    }
                }

                else -> {

                    binding.toolBarLyt.backButton.visible()
                }
            }


        }

        backClick()
    }

    private fun backClick() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.enquirySuccessFragment || backButton == false) {
                    //hide back action
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

}