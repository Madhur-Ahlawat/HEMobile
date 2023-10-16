package com.conduent.nationalhighways.ui.bottomnav.account.payments.history

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.FragmentAccountPaymentHistoryBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.vehicle.crossinghistory.dialog.DownloadFilterDialogListener
import com.conduent.nationalhighways.ui.vehicle.crossinghistory.dialog.DownloadFormatSelectionFilterDialog
import com.conduent.nationalhighways.utils.StorageHelper
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.ui.bottomnav.account.payments.history.adapter.AccountPaymentHistoryAdapter
import com.conduent.nationalhighways.ui.bottomnav.account.payments.history.adapter.AccountPaymentHistoryPaginationAdapter
import com.conduent.nationalhighways.ui.bottomnav.account.payments.history.adapter.FilterVehicleNamesAdapter
import com.conduent.nationalhighways.utils.DatePicker
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.onTextChanged

@AndroidEntryPoint
class AccountPaymentHistoryFragment : BaseFragment<FragmentAccountPaymentHistoryBinding>(),
    View.OnClickListener, DownloadFilterDialogListener {

    private val viewModel: AccountPaymentHistoryViewModel by viewModels()
    private var paymentHistoryListData: MutableList<TransactionData?> = ArrayList()
    private var vehicleNamesList: ArrayList<VehicleResponse?> = ArrayList()
    private var vehicleNamesAdapter: FilterVehicleNamesAdapter? = null
    private var paymentHistoryAdapter: AccountPaymentHistoryAdapter? = null
    private var paginationNumberAdapter: AccountPaymentHistoryPaginationAdapter? = null
    private var paginationLinearLayoutManager: LinearLayoutManager? = null
    private var dateRangeModel: PaymentDateRangeModel =
        PaymentDateRangeModel(filterType = Constants.PAYMENT_FILTER_SPECIFIC, null, null, "")
    private var selectionType: String = Constants.PDF
    private val countPerPage = 10
    private var startIndex = 1
    private var noOfPages = 1
    private var selectedPosition = 1
    private var isDownload = false
    private var isFilterVehicleResponse = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAccountPaymentHistoryBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFilterVehicleResponse = true
        dateRangeModel =
        PaymentDateRangeModel(filterType = Constants.PAYMENT_FILTER_SPECIFIC,
            DateUtils.lastPriorDate(-30), DateUtils.currentDate(), "")
        paymentHistoryAdapter = AccountPaymentHistoryAdapter(this, paymentHistoryListData)
        paginationNumberAdapter =
            AccountPaymentHistoryPaginationAdapter(this, noOfPages, selectedPosition)
        getDataForPage(startIndex)
    }

    override fun init() {
        binding.prevBtnModel = false
        binding.nextBtnModel = true
        binding.progressBar.visible()
        binding.paymentRecycleView.gone()
        binding.paginationLayout.gone()
        binding.tvNoHistory.gone()
        binding.rbSpecificDay.isChecked = true
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        paginationLinearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.paymentRecycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentHistoryAdapter
        }

        binding.paginationNumberRecyclerView.apply {
            layoutManager = paginationLinearLayoutManager
            adapter = paginationNumberAdapter
        }
    }

    override fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@AccountPaymentHistoryFragment)
            tvFilter.setOnClickListener(this@AccountPaymentHistoryFragment)
            previousBtn.setOnClickListener(this@AccountPaymentHistoryFragment)
            nextBtn.setOnClickListener(this@AccountPaymentHistoryFragment)
            closeImage.setOnClickListener(this@AccountPaymentHistoryFragment)
            applyBtn.setOnClickListener(this@AccountPaymentHistoryFragment)
            edFrom.setOnClickListener(this@AccountPaymentHistoryFragment)
            edTo.setOnClickListener(this@AccountPaymentHistoryFragment)
            rbSpecificDay.setOnClickListener(this@AccountPaymentHistoryFragment)
            rbDateRange.setOnClickListener(this@AccountPaymentHistoryFragment)
            edSpecificDay.setOnClickListener(this@AccountPaymentHistoryFragment)
            clearAllDateRange.setOnClickListener(this@AccountPaymentHistoryFragment)
            clearAllSpecificDate.setOnClickListener(this@AccountPaymentHistoryFragment)
            clearAllNumber.setOnClickListener(this@AccountPaymentHistoryFragment)
            edFrom.onTextChanged {
                checkFilterApplyBtn()
            }
            edTo.onTextChanged {
                checkFilterApplyBtn()
            }
            edSpecificDay.onTextChanged {
                checkFilterApplyBtn()
            }
        }
    }

    override fun observer() {
        observe(viewModel.paymentHistoryLiveData, ::handlePaymentResponse)
        observe(viewModel.paymentHistoryDownloadVal, ::handleDownloadPaymentHistoryData)
        observe(viewModel.vehicleListVal, ::handleVehicleListData)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {
                if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    if (paymentHistoryListData.isEmpty()) {
                        requireContext().showToast("No payment history to download")
                    } else {
                        val dialog = DownloadFormatSelectionFilterDialog()
                        dialog.setListener(this)
                        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                        dialog.show(requireActivity().supportFragmentManager, Constants.DOWNLOAD_FORMAT_SELECTION_DIALOG)
                    }
                }
            }
            R.id.tvFilter -> {
                openFilterDrawer()
                checkFilterApplyBtn()
                if (isFilterVehicleResponse)
                viewModel.getVehicleInformationApi()
            }
            R.id.closeImage -> {
                closeFilterDrawer()
            }
            R.id.clearAllNumber -> {
                dateRangeModel.vehicleNumber = ""
                vehicleNamesAdapter?.clearCheckedButtons()
                checkFilterApplyBtn()
            }
            R.id.clearAllDateRange -> {
                dateRangeModel.startDate = DateUtils.lastPriorDate(-30)
                dateRangeModel.endDate = DateUtils.currentDate()
                binding.rbDateRange.isChecked = false
                checkFilterApplyBtn()
            }
            R.id.clearAllSpecificDate -> {
                dateRangeModel.startDate = DateUtils.lastPriorDate(-30)
                dateRangeModel.endDate = DateUtils.currentDate()
                binding.rbSpecificDay.isChecked = false
                checkFilterApplyBtn()
            }
            R.id.edFrom -> {
                DatePicker(binding.edFrom).show(requireActivity().supportFragmentManager, Constants.DATE_PICKER_DIALOG)
            }
            R.id.edTo -> {
                DatePicker(binding.edTo).show(requireActivity().supportFragmentManager, Constants.DATE_PICKER_DIALOG)
            }
            R.id.edSpecificDay -> {
                DatePicker(binding.edSpecificDay).show(requireActivity().supportFragmentManager, Constants.DATE_PICKER_DIALOG)
            }
            R.id.applyBtn -> {
                binding.tvNoHistory.gone()
                closeFilterDrawer()
                callFilterPaymentHistoryData()
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
            R.id.previousBtn -> {
                if (selectedPosition >= 2) {
                    binding.nextBtnModel = true
                    selectedPosition--
                    paginationNumberAdapter?.apply {
                        setSelectedPosit(selectedPosition)
                    }
                    binding.paginationNumberRecyclerView.adapter = paginationNumberAdapter
                    scrollToPosition()
                    if (selectedPosition == 1) {
                        binding.prevBtnModel = false
                    }
                    val newPos = (selectedPosition * countPerPage) - (countPerPage - 1)
                    binding.paymentRecycleView.gone()
                    binding.progressBar.visible()
                    getDataForPage(newPos)
                }
            }
            R.id.nextBtn -> {
                if (selectedPosition < noOfPages) {
                    binding.prevBtnModel = true
                    selectedPosition++
                    paginationNumberAdapter?.apply {
                        setSelectedPosit(selectedPosition)
                    }
                    binding.paginationNumberRecyclerView.adapter = paginationNumberAdapter
                    scrollToPosition()
                    if (selectedPosition == noOfPages) {
                        binding.nextBtnModel = false
                    }
                    val newPos = (selectedPosition * countPerPage) - (countPerPage - 1)
                    binding.paymentRecycleView.gone()
                    binding.progressBar.visible()
                    getDataForPage(newPos)
                }
            }
        }
    }

    private fun callFilterPaymentHistoryData() {
        dateRangeModel.startDate = DateUtils.lastPriorDate(-30)
        dateRangeModel.endDate = DateUtils.currentDate()
        if (binding.rbSpecificDay.isChecked) {
            dateRangeModel.startDate =
                DateUtils.convertDateToMonth(binding.edSpecificDay.getText().toString())
            dateRangeModel.endDate =
                DateUtils.convertDateToMonth(binding.edSpecificDay.getText().toString())
        } else if (binding.rbDateRange.isChecked){
            dateRangeModel.startDate = DateUtils.convertDateToMonth(binding.edFrom.getText().toString())
            dateRangeModel.endDate = DateUtils.convertDateToMonth(binding.edTo.getText().toString())
        }
        binding.paymentRecycleView.gone()
        binding.progressBar.visible()
        selectedPosition = 1
        getDataForPage(1)
    }

    private fun getDataForPage(
        index: Int
    ) {
        val request = AccountPaymentHistoryRequest(
            index,
            Constants.PAYMENT,
            countPerPage
        )
        viewModel.paymentHistoryDetails(request)
    }

    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        binding.progressBar.gone()
        binding.paymentRecycleView.visible()
        //binding.paginationLayout.visible()
        when (resource) {
            is Resource.Success -> {
                resource.data?.transactionList?.count?.let {
                    noOfPages = if (it.toInt() % countPerPage == 0) {
                        it.toInt() / countPerPage
                    } else {
                        (it.toInt() / countPerPage) + 1
                    }

                }
                resource.data?.transactionList?.transaction?.let {
                    binding.nextBtnModel = selectedPosition != noOfPages
                    binding.prevBtnModel = selectedPosition != 1
                    if (it.isNotEmpty()) {
                        paymentHistoryListData.clear()
                        paymentHistoryListData.addAll(it)
                        binding.paymentRecycleView.adapter = paymentHistoryAdapter
//                        binding.paginationLayout.visible()

                        paginationNumberAdapter?.apply {
                            setCount(noOfPages)
                            setSelectedPosit(selectedPosition)
                        }
                        binding.paginationNumberRecyclerView.adapter = paginationNumberAdapter
                    } else {
                        binding.paymentRecycleView.gone()
                        binding.tvNoHistory.visible()
                        binding.paginationLayout.gone()
                    }
                } ?: run {
                    binding.paymentRecycleView.gone()
                    binding.tvNoHistory.visible()
                    binding.paginationLayout.gone()
                }
            }
            is Resource.DataError -> {
                binding.tvNoHistory.visible()
                binding.paymentRecycleView.gone()
                binding.paginationLayout.gone()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handleDownloadPaymentHistoryData(resource: Resource<ResponseBody?>?) {
        if (isDownload) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        callCoroutines(resource.data)
                    }
                }
                is Resource.DataError -> {
                    requireContext().showToast("failed to download the document")
                }
                else -> {

                }
            }
            isDownload = false
        }
    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        if (isFilterVehicleResponse) {
            binding.filterProgressBar.gone()
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        if (!it.isNullOrEmpty()) {
                            vehicleNamesList.clear()
                            vehicleNamesList.addAll(it)
                            setVehicleNamesAdapter(vehicleNamesList)
                        }
                    }
                }
                is Resource.DataError -> {
                    binding.tvNoVehicle.visible()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                }
            }
            isFilterVehicleResponse = false
        }
    }

    private fun setVehicleNamesAdapter(namesList: ArrayList<VehicleResponse?>) {
        binding.filterProgressBar.gone()
        binding.rvVehicleNumber.visible()
        vehicleNamesList = namesList
        vehicleNamesAdapter = FilterVehicleNamesAdapter(this)
        vehicleNamesAdapter?.setList(vehicleNamesList)
        binding.rvVehicleNumber.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = vehicleNamesAdapter
        }
    }

    private fun callCoroutines(body: ResponseBody) {
        lifecycleScope.launch(Dispatchers.IO) {
            val ret = async {
                return@async StorageHelper.writeResponseBodyToDisk(
                    requireActivity(),
                    selectionType,
                    body
                )
            }.await()

            if (ret) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    requireActivity().showToast("Document downloaded successfully")
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    requireActivity().showToast("Document download failed")
                }
            }
        }
    }

    fun setSelectedPosition(pos: Int) {
        if (selectedPosition != pos) {
            selectedPosition = pos
            paginationNumberAdapter?.apply {
                setSelectedPosit(selectedPosition)
            }
            binding.paginationNumberRecyclerView.adapter = paginationNumberAdapter
            scrollToPosition()
            binding.nextBtnModel = selectedPosition != noOfPages
            binding.prevBtnModel = selectedPosition != 1

            val newPos = (pos * countPerPage) - (countPerPage - 1)
            binding.paymentRecycleView.gone()
            binding.progressBar.visible()
            getDataForPage(newPos)
        }
    }

    private fun scrollToPosition() {
        val lastPos =
            (binding.paginationNumberRecyclerView.layoutManager as LinearLayoutManager)
                .findLastCompletelyVisibleItemPosition() + 1
        val firstPos =
            (binding.paginationNumberRecyclerView.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition() + 1
        if (selectedPosition > lastPos) {
            binding.paginationNumberRecyclerView.scrollToPosition(selectedPosition - 1)
        }
        if (selectedPosition < firstPos) {
            binding.paginationNumberRecyclerView.scrollToPosition(selectedPosition - 1)
        }
    }

    fun setSelectedVehicleName(name: String) {
        dateRangeModel.vehicleNumber = name
        checkFilterApplyBtn()
    }

    override fun onOkClickedListener(type: String) {
        selectionType = type
        downloadPaymentHistory()
    }

    override fun onCancelClicked() {}

    private fun downloadPaymentHistory() {
        val downloadRequest = loadDownloadRequest()
        requireContext().showToast("Document download started")
        isDownload = true
        viewModel.downloadPaymentHistoryApiCall(downloadRequest)
    }

    private fun loadDownloadRequest(): TransactionHistoryDownloadRequest {
        return TransactionHistoryDownloadRequest().apply {
            startIndex = "1"
            downloadType = selectionType
            transactionType = Constants.PAYMENT
            startDate = dateRangeModel.startDate//"11/01/2021" mm/dd/yyyy
            endDate = dateRangeModel.endDate //"11/30/2021" mm/dd/yyyy
            plateNumber = dateRangeModel.vehicleNumber
        }
    }

    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.tvDownload.performClick()
            }
        }

    private var onPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permission = true
            permissions.entries.forEach {
                if (!it.value) {
                    permission = it.value
                }
            }
            when (permission) {
                true -> {
                    binding.tvDownload.performClick()
                }
                else -> {
                    requireActivity().showToast("Please enable permission to download")
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

    fun isFilterDrawerOpen() : Boolean {
        val drawer: DrawerLayout = binding.drawerLayout
        return drawer.isDrawerOpen(GravityCompat.END)
    }

    fun closeFilterDrawer() {
        binding.drawerLayout.closeDrawers()
    }

    private fun checkFilterApplyBtn() {
        when {
            binding.rbSpecificDay.isChecked -> {
                binding.applyBtnModel =
                    (!binding.edSpecificDay.getText().isNullOrEmpty())
            }
            binding.rbDateRange.isChecked -> {
                binding.applyBtnModel =
                    !binding.edFrom.getText().isNullOrEmpty()
                            && !binding.edTo.getText().isNullOrEmpty()
            }
            else -> {
                binding.applyBtnModel = true
            }
        }
//        binding.applyBtnModel =
//            (binding.rbSpecificDay.isChecked && !binding.edSpecificDay.getText().isNullOrEmpty()) ||
//                    (binding.rbDateRange.isChecked && !binding.edFrom.getText().isNullOrEmpty()
//                            && !binding.edTo.getText().isNullOrEmpty() ||
//                            dateRangeModel.vehicleNumber?.isNotEmpty() == true)
    }

}

