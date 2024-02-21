package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentGuidanceDocumentsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GuidanceDocumentsFragment : BaseFragment<FragmentGuidanceDocumentsBinding>() {
    val raise_viewModel: RaiseNewEnquiryViewModel by activityViewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGuidanceDocumentsBinding =
        FragmentGuidanceDocumentsBinding.inflate(inflater, container, false)

    override fun init() {
        binding.feedbackToImproveMb.movementMethod = LinkMovementMethod.getInstance()

        binding.contactDartChargeCv.setOnClickListener {
            when (raise_viewModel.apiState.value) {
                Constants.LIVE -> {
                    findNavController().navigate(R.id.action_guidanceDocumentsFragment_to_contactDartChargeFragment)
                }

                else -> {
                    findNavController().navigate(
                        R.id.action_guidanceanddocumentsFragment_to_serviceUnavailableFragment,
                        getBundleData(
                            raise_viewModel.apiState.value,
                            raise_viewModel.apiEndTime.value
                        )
                    )
                }
            }

        }
        binding.youtubeCl.setOnClickListener {
            val url =
                "https://www.youtube.com/c/nationalhighways?cbrd=1" // Replace with the desired URL

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)

        }
        binding.faqCl.setOnClickListener {
            val url =
                "https://nationalhighways.co.uk/help-centre/dart-charge-help-page/" // Replace with the desired URL

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)

        }

        binding.aboutServiceCv.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_aboutthisserviceFragment)
        }

        binding.understandingDartchargesCv.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_viewChargesFragment)
        }

        binding.otherwaysTopayCv.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceDocumentsFragment_to_otherwaystopayFragment)
        }

        binding.termsConditionsCv.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_termsandconditions)
        }

        binding.thirdPartySoftwareCv.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceanddocumentsFragment_to_thirdPartySoftwareFragment)
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

    private fun getBundleData(state: String?, endTime: String? = null): Bundle {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.SERVICE_TYPE, state)
        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
        if (endTime != null && endTime.replace("null", "").isNotEmpty()) {
            bundle.putString(Constants.END_TIME, endTime)
        }
        return bundle
    }


}