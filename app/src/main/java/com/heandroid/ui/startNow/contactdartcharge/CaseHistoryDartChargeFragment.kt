package com.heandroid.ui.startNow.contactdartcharge

import android.text.Editable
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
import com.heandroid.data.model.payment.PaymentDateRangeModel
import com.heandroid.databinding.*
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.DatePicker
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.*
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CaseHistoryDartChargeFragment : BaseFragment<FragmentCaseHistoryDartChargeBinding>(),
    View.OnClickListener {

    private lateinit var mAdapter: CaseHistoryAdapter
    private var loader: LoaderDialog? = null
    private val viewModel: ContactDartChargeViewModel by viewModels()
    private var dateRangeModel: CaseHistoryRangeModel =
        CaseHistoryRangeModel("", "", "", "")

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
        Logg.logging(
            "CaseHistoryDartChargeFragment",
            "mValue ${(requireActivity() as ContactDartChargeActivity).mValue}"
        )
        if ((requireActivity() as ContactDartChargeActivity).mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            binding.btnGoStart.gone()
            // getCaseHistoryData()
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.getCaseHistoryLoginData(dateRangeModel)
        } else {
            getCaseHistoryApiData()
            binding.btnGoStart.visible()

        }

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

    private fun closeFilterDrawer() {
        binding.drawerLayout.closeDrawers()
    }


    override fun initCtrl() {
        binding.apply {
            btnGoStart.setOnClickListener(this@CaseHistoryDartChargeFragment)
            btnRaiseNewQuery.setOnClickListener(this@CaseHistoryDartChargeFragment)
            tvFilter.setOnClickListener(this@CaseHistoryDartChargeFragment)
            closeImage.setOnClickListener(this@CaseHistoryDartChargeFragment)
            clearAllNumber.setOnClickListener(this@CaseHistoryDartChargeFragment)
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

    private fun getCaseHistoryData() {
        val data1 = ServiceRequest(
            "I-230495",
            "Jan 20, 2022, 13:45",
            "Closed",
            "c",
            "s",
            "This is the description of the request",
            null, null,
            "Jan 20, 2022, 13:45", "response",""
        )
        val data2 = ServiceRequest(
            "I-2345",
            "Feb 22, 2022, 13:45",
            "Open",
            "c",
            "s",
            "This is the description of the request",
            null, null,
            "Jan 20, 2022, 13:45", "response",""
        )
        val data3 = ServiceRequest(
            "I-2343455",
            "Feb 22, 2022, 13:45",
            "Submitted",
            "c",
            "s",
            "This is the description of the request",
            null, null,
            "Jan 20, 2022, 13:45", "response",""
        )
        val list = arrayListOf(data1, data2, data3)
        showDataInView(list)
    }

    override fun observer() {
        observe(viewModel.caseHistoryApiVal, ::handleCaseHistoryListData)
        observe(viewModel.caseHistoryLoginApiVal, ::handleCaseHistoryListData)
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

                R.id.clearAllNumber -> {
                    binding.edtCaseNumber.text?.clear()
                    checkFilterApplyBtn()
                }
                R.id.clearAllDateRange -> {
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
                    binding.edtCaseNumber.text?.clear()
                    binding.edSpecificDay.text?.clear()
                    binding.edFrom.text?.clear()
                    binding.edTo.text?.clear()
                    binding.rbDateRange.isChecked = false
                    binding.rbSpecificDay.isChecked = false


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

        if (binding.edtCaseNumber.text!!.isNotEmpty()) {

            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.getCaseHistoryLoginData(
                CaseHistoryRangeModel(
                    "",
                    "",
                    "",
                    binding.edtCaseNumber.text.toString()
                )
            )

        } else if (binding.rbSpecificDay.isChecked) {
            if (binding.edSpecificDay.text!!.isNotEmpty()) {

                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.getCaseHistoryLoginData(
                    CaseHistoryRangeModel(
                        binding.edSpecificDay.text.toString(),
                        "",
                        "",
                        binding.edtCaseNumber.text.toString()
                    )
                )

            }

        } else if (binding.rbDateRange.isChecked) {
            if (binding.edFrom.text!!.isNotEmpty() && binding.edTo.text!!.isNotEmpty()) {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.getCaseHistoryLoginData(
                    CaseHistoryRangeModel(
                        binding.edFrom.text.toString(),
                        binding.edTo.text.toString(),
                        "",
                        binding.edtCaseNumber.text.toString()
                    )
                )
            }

        } else {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.getCaseHistoryLoginData(
                CaseHistoryRangeModel(
                    "",
                    "",
                    Constants.ALL,
                    ""
                )
            )
        }

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