package com.heandroid.ui.vehicle.crossinghistory

import android.app.Activity
import android.os.Environment
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
import com.heandroid.utils.StorageHelper.checkStoragePermissions
import com.heandroid.utils.StorageHelper.requestStoragePermission
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
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
        DateRangeModel(type = "", from = "", to = "", title = "")

    private var startIndex: Long = 1
    private val count: Long = 5
    private var isLoading = false
    private var isFirstTime = true
    private var list: MutableList<CrossingHistoryItem?>? = ArrayList()
    private var totalCount: Int = 0
    private lateinit var request: CrossingHistoryRequest
    private var selectionType: String = Constants.PDF

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCrossingHistoryBinding.inflate(inflater, container, false)


    override fun init() {
        request =
            CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
        viewModel.crossingHistoryApiCall(request)

        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter = CrossingHistoryAdapter(this, list)
    }

    override fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }
    }

    override fun observer() {

        viewModel.crossingHistoryVal.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.data as CrossingHistoryApiResponse
                    totalCount = response.transactionList?.transaction?.size ?: 0
                    Log.e("totalCount", "" + totalCount)
                    if (response.transactionList != null) {
                        list?.addAll(response.transactionList.transaction)
                    }
                    isLoading = false
//                    isLoading = list?.size?:0 != totalCount
                    Handler(Looper.myLooper()!!).postDelayed({
                        binding.rvHistory.adapter?.notifyDataSetChanged()
                    }, 2000)

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


                is Resource.DataError -> {
                    binding.rvHistory.gone()
                    binding.progressBar.gone()
                    binding.tvNoCrossing.visible()
                }

                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.rvHistory.gone()
                    binding.tvNoCrossing.gone()
                }

            }
        }
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
                                startIndex += 5
                                isLoading = true
                                request.startIndex = startIndex
                                binding.progressBar.visible()
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
                        read = it }
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
                    val dialog = DownloadFormatSelectionFilterDialog()
                    dialog.setListener(this)
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                    dialog.show(requireActivity().supportFragmentManager, "")
                }
            }
            R.id.tvFilter -> {
                val dialog = CrossingHistoryFilterDialog()
                dialog.setDateWithListener(dateRangeModel, this)
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
        binding.rvHistory.adapter?.notifyDataSetChanged()
        dateRangeModel = dataModel
        request = loadRequest(dateRangeModel)
        viewModel.crossingHistoryApiCall(request)
    }


    private fun loadRequest(dataModel: DateRangeModel?): CrossingHistoryRequest {
        return when (dataModel?.type) {
            "Toll_Transaction" -> {
                CrossingHistoryRequest(
                    startIndex = startIndex,
                    count = count,
                    transactionType = dataModel.type ?: "",
                    searchDate = "Transaction Date",
                    startDate = "11/01/2021"/*dataModel.from?:""*/,
                    endDate = "11/30/2021"/*dataModel.to?:""*/
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


    override fun onCancelClicked() {
    }


    private fun loadDownloadRequest(): CrossingHistoryDownloadRequest {

        return when (dateRangeModel?.type) {

            "Toll_Transaction" -> {

                CrossingHistoryDownloadRequest().apply {
                    // todo here we need the latest startIndex value
                    startIndex = "1"
                    downloadType = selectionType
                    transactionType = dateRangeModel?.type ?: ""
                    searchDate = "Transaction Date"
                    startDate = "11/01/2021"/*dateRangeModel.from?:""*/
                    endDate = "11/30/2021"/*dateRangeModel.to?:""*/
                }
            }


            else -> {

                CrossingHistoryDownloadRequest().apply {
                    // todo here we need the latest startIndex value
                    startIndex = "1"
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
        Log.d("writeResponseBodyToDisk", downloadRequest.toString())
        viewModel.downloadCrossingHistoryApiCall(downloadRequest)
        viewModel.crossingHistoryDownloadVal.observe(requireActivity()) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        callCoroutines(resource.data)
                    }
                }
                is Resource.DataError -> {
                    requireActivity().showToast("failed to download the document")
                }
                else -> {

                }
            }
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


