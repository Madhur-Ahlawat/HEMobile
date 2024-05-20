package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.databinding.FragmentEnquirySuccessBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import com.conduent.nationalhighways.utils.extn.visible

class EnquirySuccessFragment : BaseFragment<FragmentEnquirySuccessBinding>() {
    var enquiryModel: EnquiryResponseModel? = null
    var isViewCreated: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquirySuccessBinding =
        FragmentEnquirySuccessBinding.inflate(inflater, container, false)

    override fun init() {
        binding.feedbackBt.movementMethod = LinkMovementMethod.getInstance()
        enquiryModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                Constants.EnquiryResponseModel,
                EnquiryResponseModel::class.java
            )
        } else {
            arguments?.getParcelable(Constants.EnquiryResponseModel)
        }

        setData()

        binding.checkStatusBt.setOnClickListener {
            findNavController().navigate(
                R.id.action_enquirySuccessFragment_to_enquiryStatusFragment,
                getBundleData()
            )
        }
        binding.btnNext.setOnClickListener {
            if (requireActivity() is HomeActivityMain) {
                if (navFlowFrom == Constants.ACCOUNT_CONTACT_US) {
                    findNavController().navigate(
                        R.id.action_contact_us_enquirySuccessFragment_to_caseEnquiryHistoryListFragment,
                        getBundleData()
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_contact_us_enquirySuccessFragment_to_caseEnquiryHistoryListFragment,
                        getBundleData()
                    )

                }
            } else {
                requireActivity().startNormalActivityWithFinish(LandingActivity::class.java)
            }
        }
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
    }


    private fun setData() {
        binding.referenceNumberTv.text = enquiryModel?.srNumber ?: ""
        binding.referenceNumberTv.contentDescription = Utils.accessibilityForNumbers(enquiryModel?.srNumber ?: "").toString()
        binding.referenceNumberTv.contentDescription = Utils.accessibilityForNumbers(enquiryModel?.srNumber ?: "")
        binding.descTv.text =
            resources.getString(R.string.sent_email_line, enquiryModel?.email ?: "")
        if (enquiryModel?.category.toString().contains("enquiry")) {
            binding.titleTv.setText(resources.getString(R.string.str_Your_enquiry_submitted))
            binding.feedbackBt.visible()
            binding.checkStatusBt.setText(resources.getString(R.string.str_check_enquiry_status))
            binding.respondEnquiryTv.setText(resources.getString(R.string.respond_enquiry_1day))
            if (requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain).focusToolBarHome()
            }
        } else {
            binding.titleTv.setText(resources.getString(R.string.str_Your_complaint_submitted))
            binding.checkStatusBt.setText(resources.getString(R.string.str_check_complaint_status))
            binding.respondEnquiryTv.setText(resources.getString(R.string.respond_complaint_5days))
        }

        if (requireActivity() is RaiseEnquiryActivity) {
            //Non Account Holders
            binding.checkStatusBt.visible()
            binding.btnNext.text = resources.getString(R.string.back_to_main_menu)
        } else {
            //Account Holders
            binding.checkStatusBt.gone()
            binding.btnNext.text = resources.getString(R.string.str_continue)
        }
    }

    private fun getBundleData(): Bundle {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        return bundle
    }


    override fun initCtrl() {

    }

    override fun observer() {

    }

}