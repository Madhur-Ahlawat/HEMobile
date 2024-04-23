package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryListResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.databinding.FragmentCasesEnquiryHistoryListBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.adapter.CasesEnquiryListAdapter
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.listener.ItemClickListener
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseAPIViewModel
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CasesEnquiryHistoryListFragment : BaseFragment<FragmentCasesEnquiryHistoryListBinding>(),
    ItemClickListener, BackPressListener {

    lateinit var adapter: CasesEnquiryListAdapter
    val viewModel: RaiseNewEnquiryViewModel by activityViewModels()
    private var loader: LoaderDialog? = null
    var caseEnquiryList: ArrayList<ServiceRequest> = ArrayList()
    var isViewCreated: Boolean = false
    val apiViewModel: RaiseAPIViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCasesEnquiryHistoryListBinding =
        FragmentCasesEnquiryHistoryListBinding.inflate(inflater, container, false)

    override fun init() {

        binding.includeNoData.messageTv.text=resources.getString(R.string.str_no_enquiries_have_been_raised)
        setBackPressListener(this)
        if (navFlowFrom == Constants.ACCOUNT_CONTACT_US || navFlowFrom == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
            binding.btnNext.visible()
        } else {
            binding.btnNext.gone()
        }
        binding.btnNext.setOnClickListener {
            viewModel.enquiryModel.value= EnquiryModel()
            viewModel.edit_enquiryModel.value= EnquiryModel()
            findNavController().navigate(
                R.id.action_caseEnquiryHistoryListFragment_to_enquiryCategoryFragment,
                getBundleData()
            )
        }
        apiViewModel.getAccountSRList()
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
            if(HomeActivityMain.dataBinding?.titleTxt!!.getText().toString().toLowerCase().contains("contact")){
                HomeActivityMain.dataBinding?.titleTxt!!.contentDescription = getString(R.string.contact_us_upper_case)
            }
            else if(HomeActivityMain.dataBinding?.titleTxt!!.getText().toString().toLowerCase().contains("enquiry")){
                HomeActivityMain.dataBinding?.titleTxt!!.contentDescription = getString(R.string.enquiry_status)
            }
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
    }

    private fun getBundleData(): Bundle {
        return Bundle().apply {
            putString(Constants.NAV_FLOW_FROM, Constants.ACCOUNT_CONTACT_US)
        }
    }

    private fun initAdapter() {
//        adapter = CasesEnquiryListAdapter(caseEnquiryList)
//        binding.casesEnquiryRv.adapter = adapter
//        binding.casesEnquiryRv.layoutManager = LinearLayoutManager(requireActivity())
//        adapter.notifyDataSetChanged()
    }

    override fun initCtrl() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
            HomeActivityMain.dataBinding?.titleTxt!!.contentDescription = getString(R.string.contact_us_upper_case)
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
    }

    override fun observer() {
        if (!isViewCreated) {

            binding.viewModel = viewModel
            binding.lifecycleOwner = this

            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            loader?.show(
                requireActivity().supportFragmentManager,
                Constants.LOADER_DIALOG
            )

            observe(apiViewModel.getAccountSRList, ::getAccountSRListResponse)
        }
        isViewCreated = true

    }

    private fun getAccountSRListResponse(resource: Resource<EnquiryListResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                caseEnquiryList.clear()
                caseEnquiryList = resource.data?.serviceRequestList?.serviceRequest ?: ArrayList()
                if (caseEnquiryList.size > 0) {
                    binding.casesEnquiryRv.visible()
                    binding.includeNoData.noDataCl.gone()
                    binding.noDataCl.gone()
                    binding.casesEnquiryRv.apply {
                        layoutManager =
                            LinearLayoutManager(this@CasesEnquiryHistoryListFragment.requireActivity())
                        adapter = CasesEnquiryListAdapter(
                            this@CasesEnquiryHistoryListFragment,
                            caseEnquiryList
                        )
                    }

                } else {
                    binding.casesEnquiryRv.gone()
                    binding.includeNoData.noDataCl.visible()
                    binding.noDataCl.visible()

                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
                    displaySessionExpireDialog(resource.errorModel)
                }
            }

            else -> {

            }
        }

    }

    override fun onItemClick(details: ServiceRequest?, pos: Int) {
        if (details?.closedDate == null) {
            details?.closedDate = ""
        }
        val bundle = Bundle()
        bundle.putParcelable(Constants.EnquiryResponseModel, details)
        bundle.putString(Constants.NAV_FLOW_FROM, Constants.HISTORY_LIST)
        findNavController().navigate(
            R.id.action_caseEnquiryHistoryListFragment_to_casesEnquiryDetailsFragment,
            bundle
        )
    }

    override fun onBackButtonPressed() {
        if (navFlowFrom == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
            if (requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain).redirectToAccountFragment()
            }
        }
    }

}