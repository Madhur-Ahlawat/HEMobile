package com.heandroid.ui.account.creation

import android.graphics.Typeface
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
import com.heandroid.databinding.FragmentAccountTypeSelectionBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible

class AccountTypeSelectionFragment : BaseFragment<FragmentAccountTypeSelectionBinding>(),
    View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {
    private lateinit var accountModel: AccountTypeSelectionModel
    private var accountSelection: String = Constants.MAIN_ACCOUNT
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountTypeSelectionBinding {
        return FragmentAccountTypeSelectionBinding.inflate(inflater, container, false)
    }

    override fun init() {
        requireActivity().toolbar(getString(R.string.str_create_an_account))
        accountModel = AccountTypeSelectionModel()
    }

    override fun initCtrl() {
        binding.apply {
            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 2, 5)
            model = accountModel
            btnAction.setOnClickListener(this@AccountTypeSelectionFragment)
            rgMainAccount.setOnCheckedChangeListener(this@AccountTypeSelectionFragment)
            rgPersonalSubAccount.setOnCheckedChangeListener(this@AccountTypeSelectionFragment)
        }
    }

    override fun observer() {
    }

    override fun onCheckedChanged(rg: RadioGroup?, checkedId: Int) {

        when (checkedId) {
            R.id.rb_personal_act -> {
                accountModel.mainAccountType = Constants.PERSONAL_ACCOUNT
                binding.tvPersonalDesc.visible()
               // binding.tvBusinessDesc.gone()
            }
            R.id.rb_business_act -> {
                accountModel.mainAccountType = Constants.BUSINESS_ACCOUNT
                binding.tvBusinessDesc.visible()
               // binding.tvPersonalDesc.gone()
            }
            R.id.rb_prepay -> {
                accountModel.subAccountType = Constants.PRE_PAY_ACCOUNT
                binding.tvPrepayDesc.visible()
               // binding.tvPaygDesc.gone()
            }
            R.id.rb_payg_act -> {
                accountModel.subAccountType = Constants.PAYG_ACCOUNT
                binding.tvPaygDesc.visible()
              //  binding.tvPrepayDesc.gone()
            }
        }
        enableBtn()
    }

    private fun enableBtn() {
        when (accountSelection) {
            Constants.MAIN_ACCOUNT -> {
                accountModel.enable = !TextUtils.isEmpty(accountModel.mainAccountType)
                binding.model?.enable =  !TextUtils.isEmpty(accountModel.mainAccountType)
                binding.btnAction.isEnabled=true
            }
            Constants.SUB_ACCOUNT -> {
                accountModel.enable = !TextUtils.isEmpty(accountModel.subAccountType)
                binding.model?.enable =  !TextUtils.isEmpty(accountModel.subAccountType)
                binding.btnAction.isEnabled=true
            }
            else -> {
                accountModel.enable = false
                 binding.model?.enable=false
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btn_action -> {
                    if (accountSelection == Constants.MAIN_ACCOUNT) {
                        makeSubAccountViewVisible()

                    } else if (accountSelection == Constants.SUB_ACCOUNT) {
                        showNextStep()
                    }


                }
            }
        }

    }

    private fun showNextStep() {
        // show step 3 to enter personal details
        findNavController().navigate(R.id.actionAccountSelectionType_to_PersonalDetailsEntry)
    }

    private fun makeSubAccountViewVisible() {

        if (accountModel.mainAccountType == Constants.PERSONAL_ACCOUNT
            && TextUtils.isEmpty(accountModel.subAccountType)
        ) {
            setPersonalSubAccountSelectionView()
        } else if (accountModel.mainAccountType == Constants.BUSINESS_ACCOUNT) {

        }
    }

    private fun setPersonalSubAccountSelectionView() {
        binding.btnAction.isEnabled = !TextUtils.isEmpty(accountModel.subAccountType)
        accountSelection = Constants.SUB_ACCOUNT
        binding.apply {
            rlMainAccountType.gone()
            rlPersonalAccountType.visible()
        }
    }
}