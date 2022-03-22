package com.heandroid.ui.startNow.contactdartcharge

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.AccountTypeSelectionModel
import com.heandroid.databinding.FragmentCaseDetailsDartChargeBinding
import com.heandroid.databinding.FragmentDartChargeAccountTypeSelectionBinding
import com.heandroid.databinding.FragmentProvideDetailsDartChargeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.*
import com.heandroid.utils.onTextChanged

class CaseDetailsDartChargeFragment : BaseFragment<FragmentCaseDetailsDartChargeBinding>(),
    View.OnClickListener {
    private lateinit var accountModel: AccountTypeSelectionModel


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseDetailsDartChargeBinding.inflate(inflater, container, false)


    override fun init() {
        requireActivity().customToolbar(getString(R.string.check_enquiry_status))
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