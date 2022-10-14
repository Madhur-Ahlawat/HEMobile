package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountTypeSelectionModel
import com.conduent.nationalhighways.databinding.FragmentDartChargeAccountTypeSelectionBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.extn.*

class DartChargeAccountTypeSelectionFragment :
    BaseFragment<FragmentDartChargeAccountTypeSelectionBinding>(),
    View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {

    private lateinit var accountModel: AccountTypeSelectionModel

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
                        requireActivity().openActivityWithData(AuthActivity::class.java) {
                            putInt(Constants.FROM_DART_CHARGE_FLOW, Constants.DART_CHARGE_FLOW_CODE)
                        }
                    } else {

                        findNavController().navigate(R.id.action_dartChargeAccountTypeSelectionFragment_to_provideDetailsDartChargeFragment)
                    }
                }
                else -> { }
            }
        }
    }

}