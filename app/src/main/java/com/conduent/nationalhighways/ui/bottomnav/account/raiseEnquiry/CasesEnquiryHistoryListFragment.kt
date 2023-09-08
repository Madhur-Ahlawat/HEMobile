package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryListResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.databinding.FragmentCasesEnquiryHistoryListBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.adapter.CasesEnquiryListAdapter
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.listener.ItemClickListener
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.viewcharges.TollRateAdapter
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify

@AndroidEntryPoint
class CasesEnquiryHistoryListFragment : BaseFragment<FragmentCasesEnquiryHistoryListBinding>(),ItemClickListener {

    lateinit var adapter: CasesEnquiryListAdapter
    lateinit var viewModel: RaiseNewEnquiryViewModel
    private var loader: LoaderDialog? = null
    var caseEnquiryList: ArrayList<ServiceRequest> = ArrayList()
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCasesEnquiryHistoryListBinding =
        FragmentCasesEnquiryHistoryListBinding.inflate(inflater, container, false)

    override fun init() {
        viewModel.getAccountSRList()
    }

    private fun initAdapter() {
//        adapter = CasesEnquiryListAdapter(caseEnquiryList)
//        binding.casesEnquiryRv.adapter = adapter
//        binding.casesEnquiryRv.layoutManager = LinearLayoutManager(requireActivity())
//        adapter.notifyDataSetChanged()
    }

    override fun initCtrl() {

    }

    override fun observer() {
        viewModel = ViewModelProvider(requireActivity()).get(
            RaiseNewEnquiryViewModel::class.java
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(
            requireActivity().supportFragmentManager,
            Constants.LOADER_DIALOG
        )

        observe(viewModel.getAccountSRList, ::getAccountSRListResponse)
    }

    private fun getAccountSRListResponse(resource: Resource<EnquiryListResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                caseEnquiryList = resource.data?.serviceRequestList?.serviceRequest ?: ArrayList()
                binding.casesEnquiryRv.apply {
                    layoutManager = LinearLayoutManager(this@CasesEnquiryHistoryListFragment.requireActivity())
                    adapter = CasesEnquiryListAdapter(this@CasesEnquiryHistoryListFragment, caseEnquiryList)
                }
//                initAdapter()
            }

            is Resource.DataError -> {

            }

            else -> {

            }
        }

    }

    override fun onItemClick(details: ServiceRequest?, pos: Int) {
        val bundle:Bundle=Bundle()
        bundle.putString(Constants.REFERENCE_ID,details?.id?:"")
        findNavController().navigate(R.id.action_caseEnquiryHistoryListFragment_to_casesEnquiryDetailsFragment,bundle)
    }

}