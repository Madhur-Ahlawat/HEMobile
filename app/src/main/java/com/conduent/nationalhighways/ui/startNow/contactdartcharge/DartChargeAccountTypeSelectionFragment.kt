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
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DartChargeAccountTypeSelectionFragment :
    BaseFragment<FragmentDartChargeAccountTypeSelectionBinding>(),
    View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {

    private lateinit var accountModel: AccountTypeSelectionModel

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDartChargeAccountTypeSelectionBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
        accountModel = AccountTypeSelectionModel()

        AdobeAnalytics.setScreenTrack(
            "home:contact dart charge:case and enquiry:do u have dart charge account",
            "contact dart charge",
            "english",
            "case and enquiry",
            "home",
            "home:contact dart charge:case and enquiry:do u have dart charge account",
            sessionManager.getLoggedInUser()
        )

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

                        AdobeAnalytics.setActionTrack1(
                            "continue",
                            "home:contact dart charge:case and enquiry:do u have dart charge account",
                            "contact dart charge",
                            "english",
                            "case and enquiry",
                            "home",
                            "yes",
                            sessionManager.getLoggedInUser()
                        )

                        requireActivity().openActivityWithData(AuthActivity::class.java) {
                            putInt(Constants.FROM_DART_CHARGE_FLOW, Constants.DART_CHARGE_FLOW_CODE)
                        }
                    } else {
                        AdobeAnalytics.setActionTrack1(
                            "continue",
                            "home:contact dart charge:case and enquiry:do u have dart charge account",
                            "contact dart charge",
                            "english",
                            "case and enquiry",
                            "home",
                            "no",
                            sessionManager.getLoggedInUser()
                        )

                        findNavController().navigate(R.id.action_dartChargeAccountTypeSelectionFragment_to_provideDetailsDartChargeFragment)
                    }
                }
                else -> {
                }
            }
        }
    }

}