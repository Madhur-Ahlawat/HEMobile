package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.data.model.account.ServiceRequest
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.*

class CaseHistoryDetailsDartChargeFragment : BaseFragment<FragmentCaseHistoryDetailsDartChargeBinding>(),
    View.OnClickListener {

    private var data : ServiceRequest? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseHistoryDetailsDartChargeBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
        data = arguments?.getParcelable(Constants.DATA)
        data?.let {
            binding.data = it
        }
    }

    override fun initCtrl() {
        binding.apply {
            btnGoStart.setOnClickListener(this@CaseHistoryDetailsDartChargeFragment)
            btnRaiseNewQuery.setOnClickListener(this@CaseHistoryDetailsDartChargeFragment)
        }
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
                else -> { }
            }
        }
    }

}