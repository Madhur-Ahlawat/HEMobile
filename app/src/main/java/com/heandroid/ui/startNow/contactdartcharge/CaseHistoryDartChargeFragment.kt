package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryRequest
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryResponse
import com.heandroid.data.model.contactdartcharge.ServiceRequest
import com.heandroid.databinding.*
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.logging.Logger
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CaseHistoryDartChargeFragment : BaseFragment<FragmentCaseHistoryDartChargeBinding>(),
    View.OnClickListener {

    private lateinit var mAdapter: CaseHistoryAdapter
    private var loader: LoaderDialog? = null
    private val viewModel: ContactDartChargeViewModel by viewModels()
    private var caseNumber: String? = null
    private var lastName: String? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseHistoryDartChargeBinding.inflate(inflater, container, false)

    override fun init() {
        caseNumber = arguments?.getString(Constants.CASE_NUMBER)
        lastName = arguments?.getString(Constants.LAST_NAME)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
        Logg.logging("CaseHistoryDartChargeFragment","mValue ${(requireActivity() as ContactDartChargeActivity).mValue}")
        if ((requireActivity() as ContactDartChargeActivity).mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            binding.btnGoStart.gone()
            getCaseHistoryData()
        } else {
            getCaseHistoryApiData()
            binding.btnGoStart.visible()

        }

    }

    private fun getCaseHistoryApiData() {
        loader?.show(requireActivity().supportFragmentManager, "")
        lastName?.let { lastNa ->
            caseNumber?.let { caseNum ->
                val request = CaseEnquiryHistoryRequest(
                    caseNum, lastNa
                )
                viewModel.getCaseHistoryData(request)
            }

        }

    }

    override fun initCtrl() {
        binding.apply {
            btnGoStart.setOnClickListener(this@CaseHistoryDartChargeFragment)
            btnRaiseNewQuery.setOnClickListener(this@CaseHistoryDartChargeFragment)
        }
    }

    private fun getCaseHistoryData() {
        val data1 = ServiceRequest(
            "I-230495",
            "Jan 20, 2022, 13:45",
            "Closed",
            "c",
            "s",
            "This is the description of the request",
            null, null,
            "Jan 20, 2022, 13:45", "response"
        )
        val data2 = ServiceRequest(
            "I-2345",
            "Feb 22, 2022, 13:45",
            "Open",
            "c",
            "s",
            "This is the description of the request",
            null, null,
            "Jan 20, 2022, 13:45", "response"
        )
        val data3 = ServiceRequest(
            "I-2343455",
            "Feb 22, 2022, 13:45",
            "Submitted",
            "c",
            "s",
            "This is the description of the request",
            null, null,
            "Jan 20, 2022, 13:45", "response"
        )
        val list = arrayListOf(data1, data2, data3)
        showDataInView(list)
    }

    override fun observer() {
        observe(viewModel.caseHistoryApiVal, ::handleCaseHistoryListData)
    }

    private fun handleCaseHistoryListData(resource: Resource<CaseEnquiryHistoryResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.serviceRequestList?.serviceRequest?.let {
                    if (it.isNotEmpty()) {
                        binding.emptyDataMessage.gone()
                        showDataInView(it)
                    } else {
                    //    getCaseHistoryData()
                        binding.emptyDataMessage.visible()
                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun showDataInView(list: List<ServiceRequest>) {
        mAdapter = CaseHistoryAdapter(this, list)
        binding.rvCaseHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCaseHistory.setHasFixedSize(true)
        binding.rvCaseHistory.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnGoStart -> {
                    requireActivity().finish()
                }
                R.id.btnRaiseNewQuery -> {
                    findNavController().navigate(R.id.action_caseHistoryDartChargeFragment_to_newCaseCategoryFragment)
                }
                else -> {
                }
            }
        }
    }

}