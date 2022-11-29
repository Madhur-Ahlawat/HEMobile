package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountTypeSelectionModel
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseProvideDetailsModel
import com.conduent.nationalhighways.databinding.FragmentProvideDetailsDartChargeBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.*
import com.conduent.nationalhighways.utils.onTextChanged

class ProvideDetailsDartChargeFragment : BaseFragment<FragmentProvideDetailsDartChargeBinding>(),
    View.OnClickListener {
    private lateinit var accountModel: AccountTypeSelectionModel


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProvideDetailsDartChargeBinding.inflate(inflater, container, false)


    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
        accountModel = AccountTypeSelectionModel(false)

        AdobeAnalytics.setScreenTrack(
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page",
            "contact dart charge",
            "english",
            "case and enquiry",
            "home",
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page",
            false
        )

    }

    override fun initCtrl() {
        binding.apply {
            model = accountModel
            btnContinue.setOnClickListener(this@ProvideDetailsDartChargeFragment)
            etFistName.onTextChanged {
                checkButton()
            }
            etLastName.onTextChanged {
                checkButton()
            }
            etEmail.onTextChanged {
                checkButton()
            }
        }
    }

    private fun checkButton() {
        binding.apply {
            model = AccountTypeSelectionModel(
                binding.etFistName.text.toString().trim().isNotEmpty() &&
                        binding.etLastName.text.toString().trim()
                            .isNotEmpty() && Utils.isEmailValid(binding.etEmail.text.toString().trim())
            )
        }
    }

    override fun observer() {}

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnContinue -> {
                    val mCaseModel = CaseProvideDetailsModel(
                        binding.etFistName.text.toString(),
                        binding.etLastName.text.toString(),
                        binding.etEmail.text.toString(),
                        binding.etTelePhone.text.toString()
                    )
                    findNavController().navigate(R.id.action_provideDetailsDartChargeFragment_to_caseEnquiriesNewCheckFragment,
                        Bundle().apply {
                            putParcelable(Constants.CASES_PROVIDE_DETAILS_KEY,mCaseModel)
                        }
                    )
                }
                else -> {
                }
            }
        }
    }

}