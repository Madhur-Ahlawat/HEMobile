package com.heandroid.ui.startNow.contactdartcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.AccountTypeSelectionModel
import com.heandroid.data.model.contactdartcharge.CaseProvideDetailsModel
import com.heandroid.databinding.FragmentCaseEnquiriesOptionsWhatBinding
import com.heandroid.databinding.FragmentDartChargeAccountTypeSelectionBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.extn.*

class CaseEnquiriesNewCheckFragment :
    BaseFragment<FragmentCaseEnquiriesOptionsWhatBinding>(),
    View.OnClickListener {

    private var mDetails: CaseProvideDetailsModel? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseEnquiriesOptionsWhatBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
            .apply {

            }
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
                }

                else -> {
                }
            }
        }
    }

}