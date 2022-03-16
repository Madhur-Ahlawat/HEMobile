package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.ServiceRequest
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.*
import java.util.ArrayList

class CaseHistoryDartChargeFragment : BaseFragment<FragmentCaseHistoryDartChargeBinding>(),
    View.OnClickListener {

    private val serviceRequestList: List<ServiceRequest> =
        ArrayList()
    private lateinit var mAdapter: CaseHistoryAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseHistoryDartChargeBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
        getCaseHistoryData()
    }

    override fun initCtrl() {
        binding.apply {
            btnGoStart.setOnClickListener(this@CaseHistoryDartChargeFragment)
            btnRaiseNewQuery.setOnClickListener(this@CaseHistoryDartChargeFragment)
        }
    }

    private fun getCaseHistoryData() {
        val data1 = ServiceRequest("I-230495",
        "Jan 20, 2022, 13:45",
        "Closed",
        "c",
        "s",
        "This is the description of the request",
        null, null,
         "Jan 20, 2022, 13:45", "response")
        val data2 = ServiceRequest("I-2345",
            "Feb 22, 2022, 13:45",
            "Open",
            "c",
            "s",
            "This is the description of the request",
            null, null,
            "Jan 20, 2022, 13:45","response")
        val list = arrayListOf(data1, data2)
        mAdapter = CaseHistoryAdapter(this, list)
        binding.rvCaseHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCaseHistory.setHasFixedSize(true)
        binding.rvCaseHistory.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    override fun observer() { }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnGoStart -> {
                    requireActivity().finish()
                }
                R.id.btnRaiseNewQuery -> {

                }
                else -> {
                }
            }
        }
    }

}