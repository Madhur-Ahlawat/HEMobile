package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentChooseAccountTypeBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment


class ChooseAccountTypeFragment : BaseFragment<FragmentChooseAccountTypeBinding>(),
    View.OnClickListener {

    private var oldPersonalAccountValue = false
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentChooseAccountTypeBinding.inflate(inflater, container, false)


    override fun init() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.btnAccountType.isEnabled = R.id.radio_personal_account==checkedId||R.id.radio_business_account==checkedId
        }
        if(NewCreateAccountRequestModel.isEditCall) {
            oldPersonalAccountValue = NewCreateAccountRequestModel.personalAccount
            if(oldPersonalAccountValue){
                binding.radioPersonalAccount.isChecked = true
            }else{
                binding.radioBusinessAccount.isChecked = true
            }
        }


    }

    override fun initCtrl() {
        binding.btnAccountType.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnAccountType->{
                val id: Int = binding.radioGroup.checkedRadioButtonId
                NewCreateAccountRequestModel.personalAccount=false
                if (id == R.id.radio_personal_account) {
                     NewCreateAccountRequestModel.personalAccount = true
                }

                if(NewCreateAccountRequestModel.isEditCall && oldPersonalAccountValue == NewCreateAccountRequestModel.personalAccount) {
                    findNavController().popBackStack()
                }else{
                    if(NewCreateAccountRequestModel.isEditCall) {
                        NewCreateAccountRequestModel.isAccountTypeEditCall = true
                    }
                    NewCreateAccountRequestModel.isBackButtonVisible = true
                    findNavController().navigate(
                        R.id.action_fragment_choose_account_type_to_createAccountPersonalInfo
                    )
                }
            }
        }
    }

}