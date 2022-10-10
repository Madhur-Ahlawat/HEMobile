package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryRequest
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryResponse
import com.heandroid.data.model.contactdartcharge.CaseHistoryRangeModel
import com.heandroid.data.model.contactdartcharge.ServiceRequest
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.DatePicker
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.*
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CaseHistoryDartChargeFragment : BaseFragment<FragmentCaseHistoryDartChargeBinding>(),
    View.OnClickListener {

    private lateinit var mAdapter: CaseHistoryAdapter
    private var loader: LoaderDialog? = null
    private val viewModel: ContactDartChargeViewModel by viewModels()
    private var dateRangeModel: CaseHistoryRangeModel =
        CaseHistoryRangeModel("", "", "ALL", "")

    private var caseNumber: String? = null
    private var lastName: String? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCaseHistoryDartChargeBinding.inflate(inflater, container, false)

    override fun init() {
        caseNumber = arguments?.getString(Constants.CASE_NUMBER)
        lastName = arguments?.getString(Constants.LAST_NAME)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        requireActivity().customToolbar(getString(R.string.str_enquiry_status))

        if ((requireActivity() as ContactDartChargeActivity).mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            binding.btnGoStart.gone()
            getCasesForLoginUserApi()
        } else {
            getCaseHistoryApiData()
            binding.btnGoStart.visible()
            binding.tvFilter.gone()
        }
    }

    private fun getCasesForLoginUserApi() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getCaseHistoryLoginData(dateRangeModel)
    }


    private fun getCaseHistoryApiData() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        lastName?.let { lastNa ->
            caseNumber?.let { caseNum ->
                val request = CaseEnquiryHistoryRequest(
                    caseNum, lastNa
                )
                viewModel.getCaseHistoryData(request)
            }
        }

    }

    private fun openFilterDrawer() {
        val drawer: DrawerLayout = binding.drawerLayout
        if (!drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.openDrawer(GravityCompat.END)
        } else {
            drawer.closeDrawers()
        }
    }

    fun isFilterDrawerOpen(): Boolean {
        val drawer: DrawerLayout = binding.drawerLayout
        return drawer.isDrawerOpen(GravityCompat.END)
    }

    fun closeFilterDrawer() {
        binding.drawerLayout.closeDrawers()
    }


    override fun initCtrl() {
        binding.apply {
            btnGoStart.setOnClickListener(this@CaseHistoryDartChargeFragment)
            btnRaiseNewQuery.setOnClickListener(this@CaseHistoryDartChargeFragment)
            tvFilter.setOnClickListener(this@CaseHistoryDartChargeFragment)
            closeImage.setOnClickListener(this@CaseHistoryDartChargeFragment)
            clearCaseNumber.setOnClickListener(this@CaseHistoryDartChargeFragment)
            clearAllDateRange.setOnClickListener(this@CaseHistoryDartChargeFragment)
            clearAllSpecificDate.setOnClickListener(this@CaseHistoryDartChargeFragment)
            edFrom.setOnClickListener(this@CaseHistoryDartChargeFragment)
            edTo.setOnClickListener(this@CaseHistoryDartChargeFragment)
            edSpecificDay.setOnClickListener(this@CaseHistoryDartChargeFragment)
            rbDateRange.setOnClickListener(this@CaseHistoryDartChargeFragment)
            applyBtn.setOnClickListener(this@CaseHistoryDartChargeFragment)
            rbSpecificDay.setOnClickListener(this@CaseHistoryDartChargeFragment)
            edFrom.onTextChanged {
                checkFilterApplyBtn()
            }
            edTo.onTextChanged {
                checkFilterApplyBtn()
            }
            edSpecificDay.onTextChanged {
                checkFilterApplyBtn()
            }
            edtCaseNumber.onTextChanged {
                checkFilterApplyBtn()
            }

        }
    }


    override fun observer() {
        observe(viewModel.caseHistoryApiVal, ::handleCaseHistoryListData)
        observe(viewModel.caseHistoryLoginApiVal, ::handleCaseHistoryListData)
    }

    private fun handleCaseHistoryListData(resource: Resource<CaseEnquiryHistoryResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.statusCode == Constants.CASES_GIVEN_DATE_WRONG.toString()) {
                    ErrorUtil.showError(binding.root, resource.data.message)
                } else {
                    resource.data?.serviceRequestList?.let {
                        it.serviceRequest?.let { list ->
                            if (list.isNotEmpty()) {
                                binding.rvCaseHistory.visible()
                                binding.emptyDataMessage.gone()
                                showDataInView(list)
                            } else {
                                binding.emptyDataMessage.visible()
                                binding.rvCaseHistory.gone()
                            }
                        } ?: run {
                            binding.emptyDataMessage.visible()
                            binding.rvCaseHistory.gone()
                        }
                    } ?: run {
                        binding.emptyDataMessage.visible()
                        binding.rvCaseHistory.gone()
                    }
                }
            }
            is Resource.DataError -> {
                binding.emptyDataMessage.visible()
                binding.rvCaseHistory.gone()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun showDataInView(list: List<ServiceRequest?>) {
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
                R.id.tvFilter -> {
                    openFilterDrawer()
                    checkFilterApplyBtn()
                }
                R.id.closeImage -> {
                    closeFilterDrawer()
                }

                R.id.clearCaseNumber -> {
                    binding.edtCaseNumber.text?.clear()
                    checkFilterApplyBtn()
                }
                R.id.clearAllDateRange -> {
                    binding.rbDateRange.isChecked = false
                    binding.edFrom.text?.clear()
                    binding.edTo.text?.clear()
                    checkFilterApplyBtn()
                }
                R.id.clearAllSpecificDate -> {
                    binding.rbSpecificDay.isChecked = false
                    binding.edSpecificDay.text?.clear()
                    checkFilterApplyBtn()
                }
                R.id.edFrom -> {
                    DatePicker(binding.edFrom).show(
                        requireActivity().supportFragmentManager,
                        Constants.DATE_PICKER_DIALOG
                    )
                }
                R.id.edTo -> {
                    DatePicker(binding.edTo).show(
                        requireActivity().supportFragmentManager,
                        Constants.DATE_PICKER_DIALOG
                    )
                }
                R.id.edSpecificDay -> {
                    DatePicker(binding.edSpecificDay).show(
                        requireActivity().supportFragmentManager,
                        Constants.DATE_PICKER_DIALOG
                    )
                }
                R.id.applyBtn -> {
                    closeFilterDrawer()
                    clickApplyBtn()
                }
                R.id.rbSpecificDay -> {
                    binding.rbSpecificDay.isChecked = true
                    binding.rbDateRange.isChecked = false
                    checkFilterApplyBtn()
                }
                R.id.rbDateRange -> {
                    binding.rbDateRange.isChecked = true
                    binding.rbSpecificDay.isChecked = false
                    checkFilterApplyBtn()
                }
                else -> {
                }
            }
        }
    }

    private fun clickApplyBtn() {
        if (binding.edtCaseNumber.text?.isNotEmpty() == true) {
            dateRangeModel.caseNumber = binding.edtCaseNumber.text.toString().trim()
        }
        if (binding.rbSpecificDay.isChecked) {
            if (binding.edSpecificDay.text?.isNotEmpty() == true) {
                val date = binding.edSpecificDay.text.toString().trim()
                dateRangeModel.startDate = DateUtils.convertDateToMonth(date)
                dateRangeModel.endDate = DateUtils.convertDateToMonth(date)
            }

        } else if (binding.rbDateRange.isChecked) {
            if (binding.edFrom.text?.isNotEmpty() == true &&
                binding.edTo.text?.isNotEmpty() == true
            ) {
                val startDate = binding.edFrom.text.toString().trim()
                val endDate = binding.edTo.text.toString().trim()
                dateRangeModel.startDate = DateUtils.convertDateToMonth(startDate)
                dateRangeModel.endDate = DateUtils.convertDateToMonth(endDate)
            }
        } else {
            dateRangeModel.apply {
                startDate = ""
                endDate = ""
            }
        }
        getCasesForLoginUserApi()
    }

    private fun checkFilterApplyBtn() {
        when {
            binding.rbSpecificDay.isChecked -> {
                binding.applyBtnModel =
                    (!binding.edSpecificDay.text.isNullOrEmpty())
            }
            binding.rbDateRange.isChecked -> {
                binding.applyBtnModel =
                    !binding.edFrom.text.isNullOrEmpty()
                            && !binding.edTo.text.isNullOrEmpty()
            }
            else -> {
                binding.applyBtnModel = true
            }
        }

    }


}