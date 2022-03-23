package com.heandroid.ui.vehicle.vehiclehistory

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleHistoryCrossingHistoryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.SelectedVehicleViewModel
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.crossinghistory.CrossingHistoryAdapter
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
import java.io.*

@AndroidEntryPoint
class VehicleHistoryCrossingHistoryFragment :
    BaseFragment<FragmentVehicleHistoryCrossingHistoryBinding>(), View.OnClickListener,
    DownloadFilterDialogListener {

    private val viewModel: VehicleMgmtViewModel by viewModels()
    private val selectedViewModel: SelectedVehicleViewModel by activityViewModels()
    private lateinit var mVehicleDetails: VehicleResponse
    private var list: MutableList<CrossingHistoryItem?>? = ArrayList()
    private var isLoading = false
    private var isFirstTime = true
    private val count: Long = 5
    private var totalCount: Int = 0
    private var startIndex: Long = 1
    private lateinit var request: CrossingHistoryRequest
    private var isCrossingHistory = false
    private var selectionType: String = Constants.PDF
    private var isDownload = false
    private val startOne = "1"

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleHistoryCrossingHistoryBinding.inflate(inflater, container, false)

    override fun init() {
        binding.rvVehicleCrossingHistory.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = CrossingHistoryAdapter(this@VehicleHistoryCrossingHistoryFragment, list)
        }
    }

    override fun initCtrl() {
        binding.apply {
            downloadCrossingHistoryBtn.setOnClickListener(this@VehicleHistoryCrossingHistoryFragment)
            backToVehicleListBtn.setOnClickListener(this@VehicleHistoryCrossingHistoryFragment)
        }
    }

    override fun observer() {
        observe(selectedViewModel.selectedVehicleResponse, ::handleSelectedVehicleResponse)
        observe(viewModel.crossingHistoryVal, ::handleVehicleCrossingHistoryResponse)
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

    private fun handleVehicleCrossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        binding.rvVehicleCrossingHistory.visible()
        binding.progressBar.gone()
        if (isCrossingHistory) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        val response = resource.data
                        totalCount = response.transactionList?.transaction?.size ?: 0
                        if (response.transactionList != null) {
                            list?.addAll(response.transactionList.transaction)
                        }
                        isLoading = false
                        Handler(Looper.myLooper()!!).postDelayed({
                            binding.rvVehicleCrossingHistory.adapter?.notifyDataSetChanged()
                        }, 100)
                        if (list?.size == 0) {
                            binding.downloadCrossingHistoryBtn.gone()
                            binding.rvVehicleCrossingHistory.gone()
                            binding.tvNoCrossing.visible()
                            binding.progressBar.gone()
                        } else {
                            binding.downloadCrossingHistoryBtn.visible()
                            binding.rvVehicleCrossingHistory.visible()
                            binding.progressBar.gone()
                            binding.tvNoCrossing.gone()
                        }
                        endlessScroll()
                    }
                }
                is Resource.DataError -> {
                    binding.rvVehicleCrossingHistory.gone()
                    binding.progressBar.gone()
                    binding.tvNoCrossing.visible()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {

                }
            }
        }
        isCrossingHistory = false
    }

    private fun handleSelectedVehicleResponse(vehicleResponse: VehicleResponse?) {
        vehicleResponse?.let {
            mVehicleDetails = it
            getVehicleCrossingHistoryData()
        }
    }

    private fun getVehicleCrossingHistoryData() {
        // todo check this individual vehicle crossing history request
        request = CrossingHistoryRequest(
            startIndex = startIndex,
            count = count,
            transactionType = Constants.ALL_TRANSACTION,  //Constants.TOLL_TRANSACTION
            plateNumber = ""  //mVehicleDetails.plateInfo.number
        )
        isCrossingHistory = true
        viewModel.crossingHistoryApiCall(request)
        binding.tvNoCrossing.gone()
        binding.rvVehicleCrossingHistory.gone()
        binding.progressBar.visible()
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

    //todo we can move these to utils files
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


    private fun endlessScroll() {
        if (isFirstTime) {
            isFirstTime = false
            binding.rvVehicleCrossingHistory.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val linearLayoutManager =
                            recyclerView.layoutManager as LinearLayoutManager?
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.download_crossing_history_btn -> {
                if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
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
            R.id.back_to_vehicle_list_btn -> {
                findNavController().popBackStack(R.id.vehicleHistoryListFragment, false)
            }
        }
    }

    override fun onOkClickedListener(type: String) {
        selectionType = type
        downloadCrossingHistory()
    }

    override fun onCancelClicked() { }

    private fun downloadCrossingHistory() {
        val downloadRequest =  CrossingHistoryDownloadRequest().apply {
            startIndex = startOne
            downloadType = selectionType
            transactionType = Constants.ALL_TRANSACTION
        }
        isDownload = true
        requireContext().showToast("Document download started")
        viewModel.downloadCrossingHistoryApiCall(downloadRequest)
    }


    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.downloadCrossingHistoryBtn.performClick()
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
                    binding.downloadCrossingHistoryBtn.performClick()
                }
                else -> {
                    requireActivity().showToast("Please enable permission to download")
                }
            }
        }

}