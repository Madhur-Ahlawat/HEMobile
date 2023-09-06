package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountTypeSelectionModel
import com.conduent.nationalhighways.databinding.FragmentCaseDetailsDartChargeBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.*
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CaseDetailsDartChargeFragment : BaseFragment<FragmentCaseDetailsDartChargeBinding>(),
    View.OnClickListener {
    private lateinit var accountModel: AccountTypeSelectionModel

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseDetailsDartChargeBinding.inflate(inflater, container, false)


    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_enquiry_status))
        accountModel = AccountTypeSelectionModel(false)

        AdobeAnalytics.setScreenTrack(
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case case and enquiries:case number entry",
            "contact dart charge",
            "english",
            "case and enquiry",
            "home",
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case case and enquiries:case number entry",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
        binding.apply {
            model = accountModel
            btnContinue.setOnClickListener(this@CaseDetailsDartChargeFragment)
            btnRaiseNewQuery.setOnClickListener(this@CaseDetailsDartChargeFragment)
            etCaseNumber.onTextChanged {
                checkButton()
            }
            etLastName.onTextChanged {
                checkButton()
            }
        }
    }

    private fun checkButton() {
        binding.apply {
            model = AccountTypeSelectionModel(
                binding.etCaseNumber.getText().toString().trim().isNotEmpty() &&
                        binding.etLastName.getText().toString().trim().isNotEmpty()
            )
        }
    }

    override fun observer() {}

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnContinue -> {

                    AdobeAnalytics.setActionTrack(
                        "continue",
                        "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case and enquiries:case number entry",
                        "contact dart charge",
                        "english",
                        "case and enquiry",
                        "home",
                        sessionManager.getLoggedInUser()
                    )

                    val bundle = Bundle().apply {
                        putString(Constants.CASE_NUMBER, binding.etCaseNumber.getText().toString().trim())
                        putString(Constants.LAST_NAME, binding.etLastName.getText().toString().trim())
                        putParcelable(Constants.CASES_PROVIDE_DETAILS_KEY,arguments?.getParcelable(Constants.CASES_PROVIDE_DETAILS_KEY))
                    }
                    findNavController().navigate(R.id.action_caseDetailsDartChargeFragment_to_caseHistoryDartChargeFragment, bundle)
                }

                R.id.btnRaiseNewQuery -> {

                    AdobeAnalytics.setActionTrack(
                        "raise new enquiry",
                        "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case and enquiries:case number entry",
                        "contact dart charge",
                        "english",
                        "case and enquiry",
                        "home",
                        sessionManager.getLoggedInUser()
                    )

                    findNavController().navigate(R.id.action_caseDetailsDartChargeFragment_to_newCaseCategoryFragment)
                }
                else -> {
                }
            }
        }
    }

}