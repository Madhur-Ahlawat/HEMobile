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
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.*
import com.conduent.nationalhighways.utils.onTextChanged

class CaseDetailsDartChargeFragment : BaseFragment<FragmentCaseDetailsDartChargeBinding>(),
    View.OnClickListener {
    private lateinit var accountModel: AccountTypeSelectionModel


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseDetailsDartChargeBinding.inflate(inflater, container, false)


    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_enquiry_status))
        accountModel = AccountTypeSelectionModel(false)
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
                binding.etCaseNumber.text.toString().trim().isNotEmpty() &&
                        binding.etLastName.text.toString().trim().isNotEmpty()
            )
        }
    }

    override fun observer() {}

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnContinue -> {
                    val bundle = Bundle().apply {
                        putString(Constants.CASE_NUMBER, binding.etCaseNumber.text.toString().trim())
                        putString(Constants.LAST_NAME, binding.etLastName.text.toString().trim())
                        putParcelable(Constants.CASES_PROVIDE_DETAILS_KEY,arguments?.getParcelable(Constants.CASES_PROVIDE_DETAILS_KEY))
                    }
                    findNavController().navigate(R.id.action_caseDetailsDartChargeFragment_to_caseHistoryDartChargeFragment, bundle)
                }

                R.id.btnRaiseNewQuery -> {
                    findNavController().navigate(R.id.action_caseDetailsDartChargeFragment_to_newCaseCategoryFragment)
                }
                else -> {
                }
            }
        }
    }

}