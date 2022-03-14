package com.heandroid.ui.vehicle.crossinghistory

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.DateRangeModel
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.DateUtils
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
class CrossingHistoryFragment : BaseFragment<FragmentCrossingHistoryBinding>(),
    View.OnClickListener,
    CrossingHistoryFilterDialogListener, DownloadFilterDialogListener {

    private val viewModel: VehicleMgmtViewModel by viewModels()
    private var dateRangeModel: DateRangeModel? =
        DateRangeModel(type = Constants.ALL_TRANSACTION, from = "", to = "", title = "")

    private val startOne = "1"
    private var startIndex: Long = 1
    private val count: Long = 5
    private var isLoading = false
    private var isFirstTime = true
    private var list: MutableList<CrossingHistoryItem?>? = ArrayList()
    private var totalCount: Int = 0
    private lateinit var request: CrossingHistoryRequest
    private var selectionType: String = Constants.PDF
    private var isDownload = false
    private var isCrossingHistory = false
    private var crossingHistoryAdapter: CrossingHistoryAdapter? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCrossingHistoryBinding.inflate(inflater, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crossingHistoryAdapter = CrossingHistoryAdapter(this, list)
        request =
            CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
        isCrossingHistory = true
        viewModel.crossingHistoryApiCall(request)
    }


    override fun init() {
        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter = crossingHistoryAdapter

        binding.rvHistory.gone()
        binding.tvNoCrossing.gone()
        binding.progressBar.visible()
    }

    override fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
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
        binding.rvHistory.visible()
        binding.progressBar.gone()
        if (isCrossingHistory) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        val response = resource.data as CrossingHistoryApiResponse
                        totalCount = response.transactionList?.transaction?.size ?: 0
                        if (response.transactionList != null) {
                            list?.addAll(response.transactionList.transaction)
                        }
                        isLoading = false
//                    isLoading = list?.size?:0 != totalCount
                        Handler(Looper.myLooper()!!).postDelayed({
                            binding.rvHistory.adapter?.notifyDataSetChanged()
                        }, 100)

                        if (list?.size == 0) {
                            binding.rvHistory.gone()
                            binding.tvNoCrossing.visible()
                            binding.progressBar.gone()
                        } else {
                            binding.rvHistory.visible()
                            binding.progressBar.gone()
                            binding.tvNoCrossing.gone()

                        }
                        endlessScroll()
                    }
                }
                is Resource.DataError -> {
                    binding.rvHistory.gone()
                    binding.progressBar.gone()
                    binding.tvNoCrossing.gone()
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
                return@async writeResponseBodyToDisk(body)
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

    private fun endlessScroll() {
        if (isFirstTime) {
            isFirstTime = false
            binding.rvHistory.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        if (!isLoading) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == ((list?.size
                                    ?: 0) - 1) && totalCount > 4
                            ) {
                                startIndex += count
                                isLoading = true
                                request.startIndex = startIndex
                                binding.progressBar.visible()
                                isCrossingHistory = true
                                viewModel.crossingHistoryApiCall(request)
                            }
                        }
                    }
                }

            })
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        val fileExtension = if (selectionType == "pdf") ".pdf" else ".csv"
        try {
            val filePath =
                "${requireActivity().getExternalFilesDir(null)}${File.separator}${
                    System.currentTimeMillis()
                }$fileExtension"

            val currentFile = File(filePath)
//            val currentFile = File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                System.currentTimeMillis().toString() + fileExtension
//            )
            if (!currentFile.exists())
                currentFile.parentFile?.mkdirs()

            return if (selectionType == "pdf") {
                savePdf(body, currentFile)
            } else {
                saveSpreadSheet(body, filePath)
            }
        } catch (e: Exception) {
            Log.d(
                "writeResponseBodyToDisk",
                "failed to create file : ${e.localizedMessage}"
            )
            return false
        }

    }

    private fun savePdf(body: ResponseBody, futurePdfFile: File): Boolean {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val pdfReader = ByteArray(4096)
            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0
            inputStream = body.byteStream()
            outputStream = FileOutputStream(futurePdfFile)

            while (true) {
                val read = inputStream.read(pdfReader)
                Log.d("writeResponseBodyToDisk", "file download: of read $read")
                if (read == -1) {
                    break
                }
                outputStream.write(pdfReader, 0, read)
                fileSizeDownloaded += read
                Log.d(
                    "writeResponseBodyToDisk",
                    "file download: $fileSizeDownloaded of $fileSize"
                )
            }
            outputStream.flush()
            return true
        } catch (e: IOException) {
            Log.d(
                "writeResponseBodyToDisk",
                "file download: first IOException callee ${e.localizedMessage}"
            )
            return false
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    private fun saveSpreadSheet(body: ResponseBody, pathToSaveFile: String): Boolean {
        val input = body.byteStream()

        try {
            val fos = FileOutputStream(pathToSaveFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also {
                        read = it
                    }
                    != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return true
        } catch (e: Exception) {
            Log.e("writeResponseBodyToDisk", e.toString())
            return false
        } finally {
            input.close()
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
                        dialog.show(requireActivity().supportFragmentManager, "")
                    }
                }
            }
            R.id.tvFilter -> {
                val model = DateRangeModel (
                    dateRangeModel?.type,
                    DateUtils.convertDateToDate(dateRangeModel?.from ?: ""),
                    DateUtils.convertDateToDate(dateRangeModel?.to ?: ""),
                    dateRangeModel?.title,
                )
                val dialog = CrossingHistoryFilterDialog()
                dialog.setDateWithListener(model, this)
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                dialog.show(requireActivity().supportFragmentManager, "")
            }
        }
    }

    override fun onRangedApplied(dataModel: DateRangeModel?) {
        reloadData(dataModel)
    }

    override fun onClearRange(dataModel: DateRangeModel?) {
        reloadData(dataModel)
    }

    private fun reloadData(dataModel: DateRangeModel?) {
        binding.rvHistory.gone()
        binding.tvNoCrossing.gone()
        binding.progressBar.visible()

        startIndex = 1
        totalCount = 0
        isLoading = false
        list?.clear()
        binding.rvHistory.adapter = CrossingHistoryAdapter(this, list)
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

    private fun loadDownloadRequest(): CrossingHistoryDownloadRequest {
        return when (dateRangeModel?.type) {
            Constants.TOLL_TRANSACTION -> {
                CrossingHistoryDownloadRequest().apply {
                    startIndex = startOne
                    downloadType = selectionType
                    transactionType = dateRangeModel?.type ?: ""
                    searchDate = Constants.TRANSACTION_DATE
                    startDate = dateRangeModel?.from ?: "" //"11/01/2021" mm/dd/yyyy
                    endDate = dateRangeModel?.to ?: "" //"11/30/2021" mm/dd/yyyy
                }
            }
            else -> {
                CrossingHistoryDownloadRequest().apply {
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


