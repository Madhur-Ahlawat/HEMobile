package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentChooseAccountTypeBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY


class ChooseAccountTypeFragment : BaseFragment<FragmentChooseAccountTypeBinding>(),
    View.OnClickListener {
    val bundle = Bundle()
    private var oldPersonalAccountValue = false
    private var isViewCreated: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChooseAccountTypeBinding.inflate(inflater, container, false)


    override fun init() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.btnAccountType.isEnabled =
                R.id.radio_personal_account == checkedId || R.id.radio_business_account == checkedId
        }

        when (navFlowCall) {

            EDIT_SUMMARY -> {
                if (!isViewCreated) {
                    oldPersonalAccountValue = NewCreateAccountRequestModel.personalAccount
                }
                if (oldPersonalAccountValue) {
                    binding.radioPersonalAccount.isChecked = true
                } else {
                    binding.radioBusinessAccount.isChecked = true
                }
            }
        }
        isViewCreated = true


    }

    override fun initCtrl() {
        binding.btnAccountType.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnAccountType -> {
                val id: Int = binding.radioGroup.checkedRadioButtonId

                if (id == R.id.radio_personal_account) {
                    NewCreateAccountRequestModel.personalAccount = true
                    NewCreateAccountRequestModel.companyName = ""
                } else {
                    NewCreateAccountRequestModel.prePay = true
                    NewCreateAccountRequestModel.personalAccount = false
                }

                val editCall = navFlowCall.equals(EDIT_SUMMARY, true)
                if (editCall && oldPersonalAccountValue == NewCreateAccountRequestModel.personalAccount) {
                    findNavController().popBackStack()
                } else {
                    val bundle = Bundle()
                    if (editCall) {
                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.EDIT_ACCOUNT_TYPE)
                    } else {
                        bundle.putString(
                            Constants.NAV_FLOW_KEY,
                            Constants.ACCOUNT_CREATION_EMAIL_FLOW
                        )
                    }

                    findNavController().navigate(
                        R.id.action_fragment_choose_account_type_to_createAccountPersonalInfo,
                        bundle
                    )
                }

            }
        }
    }
}