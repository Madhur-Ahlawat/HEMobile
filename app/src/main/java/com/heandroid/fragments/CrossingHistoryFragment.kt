package com.heandroid.fragments

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.adapter.CrossingHistoryAdapter
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.dialog.CrossingHistoryFilterDialog
import com.heandroid.dialog.DownloadFormatSelectionFilterDialog
import com.heandroid.gone
import com.heandroid.listener.CrossingHistoryFilterDialogListener
import com.heandroid.listener.DownloadFilterDialogListener
import com.heandroid.model.DateRangeModel
import com.heandroid.model.crossingHistory.request.CrossingHistoryDownloadRequest
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.showToast
import com.heandroid.utils.Constants
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.StorageHelper.checkStoragePermissions
import com.heandroid.utils.StorageHelper.requestStoragePermission
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.*

class CrossingHistoryFragment : BaseFragment(), View.OnClickListener,
    CrossingHistoryFilterDialogListener, DownloadFilterDialogListener {

    private lateinit var viewModel: VehicleMgmtViewModel
    private lateinit var binding: FragmentCrossingHistoryBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrossingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
        observer()
    }

    private fun init() {
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]


        request =
            CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
        viewModel.crossingHistoryApiCall(request)

        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter = CrossingHistoryAdapter(this, list)
    }

    private fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }
    }

    private fun observer() {

        viewModel.crossingHistoryVal.observe(viewLifecycleOwner)
        {
            when (it.status) {

                Status.SUCCESS -> {

                    val response = it.data?.body() as CrossingHistoryApiResponse
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


                Status.ERROR -> {
                    binding.rvHistory.visible()
                    binding.progressBar.gone()
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
                    requireActivity().showToast("Document  not downloaded ")

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

        try {
            var path=""
            path = if (selectionType == "pdf") {
                ".pdf"

            }else{
                ".txt"
            }
            val futurePdfFile =
                File("${requireActivity().getExternalFilesDir(null)}${File.separator}${System.currentTimeMillis()}$path")

            Log.d("writeResponseBodyToDisk", "file download:  path $futurePdfFile")

            if (!futurePdfFile.exists())
                futurePdfFile.parentFile.mkdirs()

            Log.d(
                "writeResponseBodyToDisk",
                "file download:  futurePdfFile ${futurePdfFile.absolutePath}"
            )

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

            return true
        } catch (e: IOException) {
            Log.d(
                "writeResponseBodyToDisk",
                "file download: second IOException callee ${e.localizedMessage}"
            )

            return false
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {
                if (!checkStoragePermissions(requireActivity())) {
                    requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLaucher,
                        onPermissionlaucher = onPermissionlaucher
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
        viewModel.downloadCrossingHistoryApiCall(downloadRequest)
        viewModel.crossingHistoryDownloadVal.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("Success", it.status.toString())
                    callCoroutines(it.data?.body() as ResponseBody)
                }

                Status.ERROR -> {
                    Log.d("Success", it.status.toString())
                    requireActivity().showToast("Document downloaded stopped due to error ")
                }
                Status.LOADING -> {
                    Log.d("Success", it.status.toString())
                    requireActivity().showToast("Document downloaded is loading")

                }
            }
        }
    }

    private var onScopeResultLaucher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.tvDownload.performClick()
            }
        }


    private var onPermissionlaucher =
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


