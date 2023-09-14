package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.ServiceRequest
import com.conduent.nationalhighways.databinding.*
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.DateUtils.getDateForCasesAndEnquiry
import com.conduent.nationalhighways.utils.DateUtils.getTimeForCasesAndEnquiry
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CaseHistoryDetailsDartChargeFragment :
    BaseFragment<FragmentCaseHistoryDetailsDartChargeBinding>(), View.OnClickListener {

    private var data: ServiceRequest? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseHistoryDetailsDartChargeBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_case_enquiry_status))
        data = arguments?.getParcelable(Constants.DATA)

        data?.let {
            binding.data = it
        }
        binding.tvDateValue.text =
            getDateForCasesAndEnquiry(data?.created) //"23 Sep 2022 12:00 AM" , "Jan 20, 2022, 13:45"
        binding.tvTimeValue.text = getTimeForCasesAndEnquiry(data?.created) //"23 Sep 2022 12:00 AM"
        if (sessionManager.getLoggedInUser()) {
            binding.btnGoStart.text = getString(R.string.str_go_to_account_management)
        } else {
            binding.btnGoStart.text = getString(R.string.str_go_to_start_menu)
        }
    }

    override fun initCtrl() {
        binding.apply {
            btnGoStart.setOnClickListener(this@CaseHistoryDetailsDartChargeFragment)
            btnRaiseNewQuery.setOnClickListener(this@CaseHistoryDetailsDartChargeFragment)
        }
    }

    override fun observer() {}

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnGoStart -> {
                    if (sessionManager.getLoggedInUser()) {
                        requireActivity().finish()
                    } else {
                        requireActivity().startNewActivityByClearingStack(
                            LandingActivity::class.java
                        )
                    }
                }
                R.id.btnRaiseNewQuery -> {
                    findNavController().navigate(
                        R.id.action_caseHistoryDetailsDartChargeFragment_to_newCaseCategoryFragment
                    )
                }
                else -> {
                }
            }
        }
    }

}