package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CaseCreatedSuccessfullyFragment : BaseFragment<FragmentRaiseNewEnquirySuccessBinding>(),
    View.OnClickListener {
    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRaiseNewEnquirySuccessBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
    }

    override fun initCtrl() {
        binding.apply {
            checkEnquiryStatus.setOnClickListener(this@CaseCreatedSuccessfullyFragment)
            goToStartMenu.setOnClickListener(this@CaseCreatedSuccessfullyFragment)
            rlCaseNoVal.text = arguments?.getString(Constants.CASE_NUMBER)
            rlDateVal.text = Utils.currentDateAndTime()
            emailConformationTxt.text  = getString(R.string.str_email_conformation,arguments?.getString(Constants.LAST_NAME))
            if(sessionManager.getLoggedInUser()){
              checkEnquiryStatus.gone()
                goToStartMenu.visible()
                goToStartMenu.text = getString(R.string.str_go_to_account_management)
            }else{
                checkEnquiryStatus.visible()
                goToStartMenu.visible()
                goToStartMenu.text = getString(R.string.str_go_to_start_menu)


            }
        }
    }

    override fun observer() {}
    override fun onClick(it: View?) {

        when (it?.id) {

            R.id.check_enquiry_status -> {
//                findNavController().navigate(R.id.action)
                if(sessionManager.getLoggedInUser()){
                    requireActivity().finish()
                }else{
                    findNavController().navigate(
                        R.id.action_CaseCreatedSuccessfullyFragment_to_caseHistoryDartChargeFragment,
                        arguments
                    )

                }

            }
            R.id.go_to_start_menu -> {
                if(sessionManager.getLoggedInUser()) {
                    requireActivity().finish()
//                    requireActivity().startNormalActivity(HomeActivityMain::class.java)
                }
            }
            else -> {
            }
        }


    }



}