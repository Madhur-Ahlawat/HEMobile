package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseProvideDetailsModel
import com.conduent.nationalhighways.databinding.FragmentCaseEnquiriesOptionsWhatBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.*
import javax.inject.Inject

class CaseEnquiriesNewCheckFragment :
    BaseFragment<FragmentCaseEnquiriesOptionsWhatBinding>(),
    View.OnClickListener {
    @Inject
    lateinit var sessionManager: SessionManager

    private var mDetails: CaseProvideDetailsModel? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseEnquiriesOptionsWhatBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
            .apply {

            }

        AdobeAnalytics.setScreenTrack(
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case case and enquiries",
            "contact dart charge",
            "english",
            "case and enquiry",
            "home",
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case and enquiries",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
        binding.apply {
            rlCheckEnquiryStatus.setOnClickListener(this@CaseEnquiriesNewCheckFragment)
            rlRaiseNewEnquiry.setOnClickListener(this@CaseEnquiriesNewCheckFragment)

        }
    }

    override fun observer() {}


    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.rl_raise_new_enquiry -> {
                    findNavController().navigate(
                        R.id.action_caseEnquiriesNewCheckFragment_to_newCaseCategoryFragment,
                        Bundle().apply {
                            putParcelable(
                                Constants.CASES_PROVIDE_DETAILS_KEY,
                                arguments?.getParcelable(Constants.CASES_PROVIDE_DETAILS_KEY)
                            )
                        })

                    AdobeAnalytics.setActionTrack(
                        "raise a new enquiry",
                        "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case case and enquiries",
                        "contact dart charge",
                        "english",
                        "case and enquiry",
                        "home",
                        sessionManager.getLoggedInUser()
                    )


                }
                R.id.rl_check_enquiry_status -> {
                    findNavController().navigate(
                        R.id.action_caseEnquiriesNewCheckFragment_to_caseDetailsDartChargeFragment,
                        Bundle().apply {
                            putParcelable(
                                Constants.CASES_PROVIDE_DETAILS_KEY,
                                arguments?.getParcelable(Constants.CASES_PROVIDE_DETAILS_KEY)
                            )

                        })

                    AdobeAnalytics.setActionTrack(
                        "check enquiry status",
                        "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case and enquiries",
                        "contact dart charge",
                        "english",
                        "case and enquiry",
                        "home",
                        sessionManager.getLoggedInUser()
                    )

                }

                else -> {
                }
            }
        }
    }

}