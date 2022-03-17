package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.ServiceRequest
import com.heandroid.data.model.casesenquiries.CaseCategoriesModel
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.*
import java.util.ArrayList

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
        }
    }


    override fun observer() {}
    override fun onClick(it: View?) {

        when (it?.id) {

            R.id.check_enquiry_status -> {
//                findNavController().navigate(R.id.action)
            }
            R.id.go_to_start_menu ->{

            }
            else -> {
            }
        }


    }


}