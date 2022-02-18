package com.heandroid.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
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
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.showToast
import com.heandroid.utils.Constants
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.visible
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import okhttp3.ResponseBody
import java.io.*

class CrossingHistoryFragment : BaseFragment(), View.OnClickListener,
    CrossingHistoryFilterDialogListener,
    DownloadFilterDialogListener {

    private lateinit var viewModel: VehicleMgmtViewModel
    private lateinit var binding: FragmentCrossingHistoryBinding
    private var dateRangeModel: DateRangeModel? =
        DateRangeModel(type = "", from = "", to = "", title = "")

    private val startIndex: Long = 1
    private val count: Long = 5

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

        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter = CrossingHistoryAdapter(this)


//        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest!!.id).observe(this, Observer {

//        })
    }

    private fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }
    }

    private fun observer() {
        val request =
            CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
        lifecycleScope.launch {
            viewModel.getListData(request).collectLatest {
                sectionVisibility()
                (binding.rvHistory.adapter as CrossingHistoryAdapter).submitData(it)
            }
        }
    }


    private fun downloadCrossingHistory() {

        binding.progressBar.visibility = View.VISIBLE
        val downloadRequest = loadDownloadRequest()
        viewModel.downloadCrossingHistoryApiCall(downloadRequest)
        viewModel.crossingHistoryDownloadVal.observe(requireActivity(), {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.d("writeResponseBodyToDisk", it.status.toString())
                    Log.d("writeResponseBodyToDisk", "it.data?.body()  ${it.data?.body()}")

                    callCoroutines(it.data?.body() as ResponseBody)
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    requireContext().showToast("Error ${it.data?.errorBody().toString()}")

                }

                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE

                }

            }
        })

    }

    private fun callCoroutines(body: ResponseBody) {
        lifecycleScope.launch(Dispatchers.IO) {

           val ret= async {
              return@async writeResponseBodyToDisk(body)


            }.await()
            if(ret){

                withContext(Dispatchers.Main){
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "PDF File saved successfully", Toast.LENGTH_SHORT).show()
                }
            }else{

                withContext(Dispatchers.Main){
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "PDF File not saved successfully", Toast.LENGTH_SHORT).show()

                }

            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private  fun writeResponseBodyToDisk(body: ResponseBody): Boolean {

        try {
            val path: String =
                requireActivity().getExternalFilesDir("").toString()

            Log.d("writeResponseBodyToDisk", "file download:  path $path");

            val futurePdfFile =
                File(path, "transaction.PDF")
            if (!futurePdfFile.exists())
                futurePdfFile.mkdirs()

            Log.d(
                "writeResponseBodyToDisk",
                "file download:  futurePdfFile ${futurePdfFile.absolutePath}"
            );

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

                    Log.d("writeResponseBodyToDisk", "file download: of read $read");

                    if (read == -1) {
                        break
                    }

                    outputStream.write(pdfReader, 0, read)
                    fileSizeDownloaded += read
                    Log.d(
                        "writeResponseBodyToDisk",
                        "file download: $fileSizeDownloaded of $fileSize"
                    );

                }

                outputStream.flush()
            } catch (e: IOException) {
                Log.d("writeResponseBodyToDisk", "file download: first IOException callee ${e.localizedMessage}");

                return false

            } finally {
                inputStream?.close()
                outputStream?.close()

            }

            return true
        } catch (e: IOException) {
            Log.d("writeResponseBodyToDisk", "file download: second IOException callee ${e.localizedMessage}");

            return false
        }

    }


    private var selectionType = ""
    private fun loadDownloadRequest(): CrossingHistoryDownloadRequest {
        return when (dateRangeModel?.type) {
            "Toll_Transaction" -> {
                CrossingHistoryDownloadRequest().apply {
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
                    startIndex = "1"
                    downloadType = selectionType
                    transactionType = dateRangeModel?.type ?: Constants.ALL_TRANSACTION
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {

                val dialog = DownloadFormatSelectionFilterDialog()
                dialog.setListener(this)
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                dialog.show(
                    requireActivity().supportFragmentManager,
                    "DownloadFormatSelectionFilterDialog"
                )


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
        binding.progressBar.visible()
        binding.rvHistory.gone()
        dateRangeModel = dataModel
        val request = loadRequest(dataModel)
        lifecycleScope.launch {
            viewModel.getListData(request).collectLatest {
                sectionVisibility()
                (binding.rvHistory.adapter as CrossingHistoryAdapter).submitData(it)
            }
        }
    }

    private fun sectionVisibility() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.rvHistory.visible()
            binding.progressBar.gone()
        }, 1750)
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

    override fun onOkClickedListener(type: String) {

        selectionType = type
        downloadCrossingHistory()
    }

    override fun onCancelClicked() {
    }

}