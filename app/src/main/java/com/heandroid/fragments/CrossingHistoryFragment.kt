package com.heandroid.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.CrossingHistoryAdapter
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.dialog.CrossingHistoryFilterDialog
import com.heandroid.gone
import com.heandroid.listener.CrossingHistoryFilterDialogListener
import com.heandroid.model.DateRangeModel
import com.heandroid.model.crossingHistory.request.CrossingHistoryDownloadRequest
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.showToast
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CrossingHistoryFragment : BaseFragment(), View.OnClickListener, CrossingHistoryFilterDialogListener {

    private lateinit var viewModel: VehicleMgmtViewModel
    private lateinit var binding: FragmentCrossingHistoryBinding
    private var dateRangeModel : DateRangeModel?=DateRangeModel(type = "", from = "",to="", title = "")

    private val startIndex: Long=1
    private val count:Long=5
    private var list: MutableList<CrossingHistoryItem?>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        binding.rvHistory.layoutManager=LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter=CrossingHistoryAdapter()
    }

    private fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }

    }

    private fun observer() {
        val request = CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
        lifecycleScope.launch {
            viewModel.getListData(request).collectLatest {
                ( binding.rvHistory.adapter as CrossingHistoryAdapter).submitData(it)
            }
        }



    }


    private fun downloadCrossingHistory()
    {
        val downloadRequest = CrossingHistoryDownloadRequest().apply {
            startIndex = "1"
            transactionType="ALL"
            downloadType="pdf"
        }
        viewModel.downloadCrossingHistoryApiCall(downloadRequest)
        viewModel.crossingHistoryDownloadVal.observe(requireActivity(), {
            when(it.status)
            {
                Status.SUCCESS->
                {
                    Log.d("Success", it.status.toString())
                }

                Status.ERROR->{
                    Log.d("Success", it.status.toString())
                }
                Status.LOADING->{
                    Log.d("Success", it.status.toString())

                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {
                downloadCrossingHistory()
            }
            R.id.tvFilter -> {
                val dialog = CrossingHistoryFilterDialog()
                dialog.setDateWithListener(dateRangeModel,this)
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                dialog.show(requireActivity().supportFragmentManager, "")
            }
        }
    }

    override fun onRangedApplied(dataModel: DateRangeModel?) {
        Log.e("here ",dateRangeModel?.toString()?:"")
        reloadData(dataModel)
    }

    override fun onClearRange(dataModel: DateRangeModel?) {
        reloadData(dataModel)
    }

    private fun reloadData(dataModel: DateRangeModel?){
        list?.clear()
        dateRangeModel=dataModel
        val request = loadRequest(dataModel)
        lifecycleScope.launch {
            viewModel.getListData(request).collectLatest {
                ( binding.rvHistory.adapter as CrossingHistoryAdapter).submitData(it)
            }
        }
    }


    private fun loadRequest(dataModel: DateRangeModel?) : CrossingHistoryRequest{
        return when (dataModel?.type) {
            "Toll_Transaction" -> { CrossingHistoryRequest(startIndex = startIndex,
                                                           count = count,
                                                           transactionType = dataModel.type?:"",
                                                           searchDate = "Transaction Date",
                                                           startDate="11/01/2021"/*dataModel.from?:""*/,
                                                           endDate = "11/30/2021"/*dataModel.to?:""*/) }


            else -> { CrossingHistoryRequest(startIndex = startIndex,
                                             count = count,
                                             transactionType = dataModel?.type?:"") }
        }
    }

}