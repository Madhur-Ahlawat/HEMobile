package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaseCreatedSuccessfullyFragment : BaseFragment<FragmentRaiseNewEnquirySuccessBinding>(),
    View.OnClickListener {


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
            rlDateVal.text = "April 1 2022 03:34"
        }
    }

    override fun observer() {}
    override fun onClick(it: View?) {

        when (it?.id) {

            R.id.check_enquiry_status -> {
//                findNavController().navigate(R.id.action)
                findNavController().navigate(
                    R.id.action_CaseCreatedSuccessfullyFragment_to_caseHistoryDartChargeFragment,
                    arguments
                )
            }
            R.id.go_to_start_menu -> {
                requireActivity().finish()
            }
            else -> {
            }
        }


    }


}