package com.heandroid.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.dialog.CrossingHistoryFilterDialog
import com.heandroid.dialog.DownloadFormatSelectionFilterDialog
import com.heandroid.gone
import com.heandroid.listener.CrossingHistoryFilterDialogListener
import com.heandroid.listener.DownloadFilterDialogListener
import com.heandroid.model.DateRangeModel
import com.heandroid.model.crossingHistory.request.CrossingHistoryDownloadRequest
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.showToast
import com.heandroid.utils.Constants
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.visible
import java.io.*


class CrossingHistoryFragment : BaseFragment(), View.OnClickListener,
    CrossingHistoryFilterDialogListener, DownloadFilterDialogListener {

    private lateinit var viewModel: VehicleMgmtViewModel
    private lateinit var binding: FragmentCrossingHistoryBinding
    private var dateRangeModel: DateRangeModel? =
        DateRangeModel(type = "", from = "", to = "", title = "")

    private var selectionType: String = Constants.PDF
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
       // binding.rvHistory.adapter = CrossingHistoryAdapter(this)
    }

    private fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }
    }

    private fun observer() {
//        val request =
//            CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
//        lifecycleScope.launch {
//            viewModel.getListData(request).collectLatest {
//                sectionVisibility()
//                (binding.rvHistory.adapter as CrossingHistoryAdapter).submitData(it)
//            }
//        }


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {

                val dialog = DownloadFormatSelectionFilterDialog()

                dialog.setListener(this)

                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

                dialog.show(requireActivity().supportFragmentManager, "")
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
//        val request = loadRequest(dataModel)
//        lifecycleScope.launch {
//            viewModel.getListData(request).collectLatest {
//                sectionVisibility()
//                (binding.rvHistory.adapter as CrossingHistoryAdapter).submitData(it)
//            }
//        }
    }

    private fun sectionVisibility() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.rvHistory.visible()
            binding.progressBar.gone()
        }, 1750)
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
        viewModel.crossingHistoryDownloadVal.observe(requireActivity(), {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("Success", it.status.toString())
                    requireActivity().showToast("Document downloaded successfully")
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
        })
    }


    override fun onCancelClicked() {
        Log.d("cancel", "close")
    }
}