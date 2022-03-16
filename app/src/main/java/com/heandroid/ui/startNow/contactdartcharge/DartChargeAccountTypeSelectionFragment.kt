package com.heandroid.ui.startNow.contactdartcharge

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
import com.heandroid.databinding.FragmentDartChargeAccountTypeSelectionBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.extn.*

class DartChargeAccountTypeSelectionFragment :
    BaseFragment<FragmentDartChargeAccountTypeSelectionBinding>(),
    View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {
    private lateinit var accountModel: AccountTypeSelectionModel
    private var accountSelection: String = Constants.MAIN_ACCOUNT
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDartChargeAccountTypeSelectionBinding.inflate(inflater, container, false)


    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
        accountModel = AccountTypeSelectionModel()
    }

    override fun initCtrl() {
        binding.apply {
            model = accountModel
            btnContinue.setOnClickListener(this@DartChargeAccountTypeSelectionFragment)
            rgAccount.setOnCheckedChangeListener(this@DartChargeAccountTypeSelectionFragment)
        }
    }

    override fun observer() {}

    override fun onCheckedChanged(rg: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.yes -> {
                binding.model = AccountTypeSelectionModel(true)
            }
            R.id.no -> {
                binding.model = AccountTypeSelectionModel(true)
            }
        }
    }


    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnContinue -> {
                    if (binding.yes.isChecked) {
                        Logg.logging("TestBase", " Yes Clicked ")
                        requireActivity().openActivityWithData(AuthActivity::class.java){
                            putInt(Constants.FROM_DART_CHARGE_FLOW,Constants.DART_CHARGE_FLOW_CODE)
                        }
                    } else {
//                        findNavController()
                        requireContext().showToast("no")
                    }
                }
                else -> {
                }
            }
        }
    }

}