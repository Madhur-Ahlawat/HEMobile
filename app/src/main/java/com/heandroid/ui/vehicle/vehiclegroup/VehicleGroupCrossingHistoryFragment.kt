package com.heandroid.ui.vehicle.vehiclegroup

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.DateRangeModel
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.databinding.FragmentVehicleGroupCrossingHistoryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.account.payments.history.AccountPaymentHistoryPaginationAdapter
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.crossinghistory.CrossingHistoryFilterDialog
import com.heandroid.ui.vehicle.crossinghistory.CrossingHistoryFilterDialogListener
import com.heandroid.ui.vehicle.crossinghistory.DownloadFilterDialogListener
import com.heandroid.ui.vehicle.crossinghistory.DownloadFormatSelectionFilterDialog
import com.heandroid.utils.DateUtils
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.StorageHelper.checkStoragePermissions
import com.heandroid.utils.StorageHelper.requestStoragePermission
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
import java.io.*

@AndroidEntryPoint
class VehicleGroupCrossingHistoryFragment :
    BaseFragment<FragmentVehicleGroupCrossingHistoryBinding>(),
    View.OnClickListener, DownloadFilterDialogListener {

    private val viewModel: VehicleMgmtViewModel by viewModels()
    private var dateRangeModel: DateRangeModel? =
        DateRangeModel(type = Constants.ALL_TRANSACTION, from = "", to = "", title = "")

    private val startOne = "1"
    private var startIndex: Long = 1
    private val count: Long = 100
    private var isLoading = false
    private var isFirstTime = true
    private var list: MutableList<CrossingHistoryItem?>? = ArrayList()
    private var totalCount: Int = 0
    private lateinit var request: CrossingHistoryRequest
    private var selectionType: String = Constants.PDF
    private var isDownload = false
    private var isCrossingHistory = false
    private var crossingHistoryAdapter: VehicleGroupCrossingHistoryAdapter? = null
    private var paginationNumberAdapter: AccountPaymentHistoryPaginationAdapter? = null
    private var paginationLinearLayoutManager: LinearLayoutManager? = null
    private var noOfPages = 1
    private var selectedPosition = 1

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleGroupCrossingHistoryBinding.inflate(inflater, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crossingHistoryAdapter = VehicleGroupCrossingHistoryAdapter(list)
        request =
            CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
        isCrossingHistory = true
        paginationNumberAdapter =
            AccountPaymentHistoryPaginationAdapter(this, noOfPages, selectedPosition)
        viewModel.crossingHistoryApiCall(request)
    }


    override fun init() {
        binding.rvCrossings.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCrossings.adapter = crossingHistoryAdapter

        paginationLinearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.paginationNumberRecyclerView.apply {
            layoutManager = paginationLinearLayoutManager
            adapter = paginationNumberAdapter
        }
        binding.rvCrossings.gone()
        binding.tvNoCrossing.gone()
        binding.progressBar.visible()
        binding.tvDownload.visible()
    }

    override fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@VehicleGroupCrossingHistoryFragment)
            tvFilter.setOnClickListener(this@VehicleGroupCrossingHistoryFragment)
            backBtn.setOnClickListener(this@VehicleGroupCrossingHistoryFragment)
        }
    }

    override fun observer() {
        observe(viewModel.crossingHistoryVal, ::handleCrossingHistoryData)
        observe(viewModel.crossingHistoryDownloadVal, ::handleDownloadCrossingHistoryData)
    }

    private fun handleDownloadCrossingHistoryData(resource: Resource<ResponseBody?>?) {
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

    private fun handleCrossingHistoryData(resource: Resource<CrossingHistoryApiResponse?>?) {
        binding.rvCrossings.visible()
        binding.progressBar.gone()
        if (isCrossingHistory) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        val response = resource.data
                        totalCount = response.transactionList?.transaction?.size ?: 0
                        if (response.transactionList != null) {
                            list?.addAll(response.transactionList.transaction!!)
                        }
                        isLoading = false

                        if (list?.size == 0) {
                            binding.rvCrossings.gone()
                            binding.tvNoCrossing.visible()
                            binding.tvDownload.gone()
                            binding.progressBar.gone()
                        } else {
                            binding.rvCrossings.visible()
                            binding.progressBar.gone()
                            binding.tvNoCrossing.gone()
                            binding.tvDownload.visible()
                            binding.rvCrossings.adapter = VehicleGroupCrossingHistoryAdapter(list)
                        }
                    }
                }
                is Resource.DataError -> {
                    binding.rvCrossings.gone()
                    binding.progressBar.gone()
                    binding.tvNoCrossing.visible()
                    binding.tvDownload.gone()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {

                }
            }

        }
        isCrossingHistory = false
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {
                if (!checkStoragePermissions(requireActivity())) {
                    requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    if (list?.isEmpty() == true) {
                        requireContext().showToast("No crossings to download")
                    } else {
                        val dialog = DownloadFormatSelectionFilterDialog()
                        dialog.setListener(this)
                        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                        dialog.show(requireActivity().supportFragmentManager, Constants.DOWNLOAD_FORMAT_SELECTION_DIALOG)
                    }
                }
            }
            R.id.tvFilter -> {
            }
            R.id.backBtn -> {
                findNavController().popBackStack()
            }
        }
    }


    private fun reloadData(dataModel: DateRangeModel?) {
        binding.rvCrossings.gone()
        binding.tvNoCrossing.gone()
        binding.progressBar.visible()

        startIndex = 1
        totalCount = 0
        isLoading = false
        list?.clear()
        binding.rvCrossings.adapter = VehicleGroupCrossingHistoryAdapter(list)
        dateRangeModel = dataModel
        request = loadRequest(dateRangeModel)
        isCrossingHistory = true
        isFirstTime = true
        viewModel.crossingHistoryApiCall(request)
    }


    private fun loadRequest(dataModel: DateRangeModel?): CrossingHistoryRequest {
        return when (dataModel?.type) {
            Constants.TOLL_TRANSACTION -> {
                CrossingHistoryRequest(
                    startIndex = startIndex,
                    count = count,
                    transactionType = dataModel.type ?: "",
                    searchDate = Constants.TRANSACTION_DATE,
                    startDate = dataModel.from ?: "", //"11/01/2021" mm/dd/yyyy
                    endDate = dataModel.to ?: ""  //"11/30/2021" mm/dd/yyyy
                )
            }
            else -> {
                CrossingHistoryRequest(
                    startIndex = startIndex,
                    count = count,
                    transactionType = dataModel?.type ?: ""
                )
            }
        }
    }

    override fun onCancelClicked() {}

    private fun loadDownloadRequest(): TransactionHistoryDownloadRequest {
        return when (dateRangeModel?.type) {
            Constants.TOLL_TRANSACTION -> {
                TransactionHistoryDownloadRequest().apply {
                    startIndex = startOne
                    downloadType = selectionType
                    transactionType = dateRangeModel?.type ?: ""
                    searchDate = Constants.TRANSACTION_DATE
                    startDate = dateRangeModel?.from ?: "" //"11/01/2021" mm/dd/yyyy
                    endDate = dateRangeModel?.to ?: "" //"11/30/2021" mm/dd/yyyy
                }
            }
            else -> {
                TransactionHistoryDownloadRequest().apply {
                    startIndex = startOne
                    downloadType = selectionType
                    transactionType = dateRangeModel?.type ?: Constants.ALL_TRANSACTION
                }
            }
        }
    }

    override fun onOkClickedListener(type: String) {
        selectionType = type
        downloadCrossingHistory()
    }

    private fun downloadCrossingHistory() {
        val downloadRequest = loadDownloadRequest()
        isDownload = true
        requireContext().showToast("Document download started")
        viewModel.downloadCrossingHistoryApiCall(downloadRequest)
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


