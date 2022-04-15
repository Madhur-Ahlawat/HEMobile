package com.heandroid.ui.bottomnav.account.payments.history

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
import com.heandroid.R
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryResponse
import com.heandroid.data.model.accountpayment.TransactionData
import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.model.payment.PaymentDateRangeModel
import com.heandroid.databinding.FragmentAccountPaymentHistoryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.crossinghistory.DownloadFilterDialogListener
import com.heandroid.ui.vehicle.crossinghistory.DownloadFormatSelectionFilterDialog
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

@AndroidEntryPoint
class AccountPaymentHistoryFragment : BaseFragment<FragmentAccountPaymentHistoryBinding>(),
    View.OnClickListener, DownloadFilterDialogListener, AccountPaymentHistoryFilterListener {

    private val viewModel: AccountPaymentHistoryViewModel by viewModels()
    private var listData: MutableList<TransactionData?> = ArrayList()
    private var paymentHistoryAdapter: AccountPaymentHistoryAdapter? = null
    private var paginationNumberAdapter: AccountPaymentHistoryPaginationAdapter? = null
    private var paginationLinearLayoutManager: LinearLayoutManager? = null
    private var dateRangeModel: PaymentDateRangeModel? =
        PaymentDateRangeModel(filterType = Constants.PAYMENT_FILTER_SPECIFIC, null, null, null)
    private var selectionType: String = Constants.PDF
    private val countPerPage = 10
    private var startIndex = 1
    private var noOfPages = 1
    private var selectedPosition = 1
    private var isDownload = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAccountPaymentHistoryBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentHistoryAdapter = AccountPaymentHistoryAdapter(this, listData)
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
        paginationLinearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
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
        }
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
                    if (listData.isEmpty()) {
                        requireContext().showToast("No payment history to download")
                    } else {
                        val dialog = DownloadFormatSelectionFilterDialog()
                        dialog.setListener(this)
                        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                        dialog.show(requireActivity().supportFragmentManager, "")
                    }
                }
            }
            R.id.tvFilter -> {
                AccountPaymentHistoryFilterDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                ).show(childFragmentManager, AddVehicleDialog.TAG)
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

    override fun observer() {
        observe(viewModel.paymentHistoryLiveData, ::handlePaymentResponse)
        observe(viewModel.paymentHistoryDownloadVal, ::handleDownloadPaymentHistoryData)
    }


    private fun getDataForPage(index: Int) {
        val request = AccountPaymentHistoryRequest(index, Constants.PAYMENT, countPerPage)
        viewModel.paymentHistoryDetails(request)
    }

    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        binding.progressBar.gone()
        binding.paymentRecycleView.visible()
        when (resource) {
            is Resource.Success -> {
                resource.data?.transactionList?.count?.let {
                    noOfPages = if (it.toInt() % countPerPage == 0) {
                        it.toInt() / countPerPage
                    } else {
                        (it.toInt() / countPerPage) + 1
                    }
                    if (noOfPages == 1)
                        binding.nextBtnModel = false
                }
                resource.data?.transactionList?.transaction?.let {
                    if (it.isNotEmpty()) {
                        listData.clear()
                        listData.addAll(it)
                        binding.paymentRecycleView.adapter = paymentHistoryAdapter
                        binding.paginationLayout.visible()
                        paginationNumberAdapter?.apply {
                            setCount(noOfPages)
                            setSelectedPosit(selectedPosition)
                        }
                        binding.paginationNumberRecyclerView.adapter = paginationNumberAdapter
                    } else {
                        binding.tvNoHistory.visible()
                    }
                } ?: run {
                    binding.tvNoHistory.visible()
                }
            }
            is Resource.DataError -> {
                binding.tvNoHistory.visible()
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

    private fun callCoroutines(body: ResponseBody) {
        lifecycleScope.launch(Dispatchers.IO) {
            val ret = async {
                return@async return@async StorageHelper.writeResponseBodyToDisk(
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

    override fun onApplyFilterClick(vehicleName: String?, startDate: String?, endDate: String?) {

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
            startDate = dateRangeModel?.startDate//"11/01/2021" mm/dd/yyyy
            endDate = dateRangeModel?.endDate //"11/30/2021" mm/dd/yyyy
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
}

