package com.heandroid.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.CrossingHistoryAdapter
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.dialog.CrossingHistoryFilterDialog
import com.heandroid.listener.CrossingHistoryFilterDialogListener
import com.heandroid.model.DateRangeModel
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.showToast
import com.heandroid.utils.Constants
import com.heandroid.utils.DateUtils
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory

class CrossingHistoryFragment : BaseFragment(), View.OnClickListener,
    CrossingHistoryFilterDialogListener {

    private lateinit var request: CrossingHistoryRequest
    private lateinit var adapter: CrossingHistoryAdapter
    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var dataBinding: FragmentCrossingHistoryBinding
    private var needDataType = Constants.VIEW_ALL
    private var totalCount = 0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentCrossingHistoryBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init() {
        dataBinding.apply {
            rvHistory.layoutManager = LinearLayoutManager(requireActivity())
            adapter = CrossingHistoryAdapter(requireActivity())
            rvHistory.adapter = adapter
            rvHistory.hasFixedSize()
        }
    }

    private fun initCtrl() {
        setupViewModel()
        setObservers()

        dataBinding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {
            }
            R.id.tvFilter -> {
                val dialog = CrossingHistoryFilterDialog()
                dialog.setListener(this)
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                dialog.show(requireActivity().supportFragmentManager, "")
            }
        }
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        Log.d("ViewModelSetUp: ", "Setup")
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setObservers() {

        request = CrossingHistoryRequest().apply {
            startIndex = "1"
            count = "5"
            transactionType = "Toll_Transaction"
            searchDate = "Transaction Date"
            startDate = "11/01/2021"
            endDate = "11/30/2021"

        }
        // date range
        /**
         * {

        "searchDate": "Transaction Date",

        "startDate": "11/01/2021",

        "endDate": "11/30/2021",

        "startIndex": "1",

        "transactionType": "Toll_Transaction",

        "count": "1"

        }
         */
        vehicleMgmtViewModel.crossingHistoryApiCall(request);

        vehicleMgmtViewModel.crossingHistoryVal.observe(requireActivity()!!, { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    var apiResponse = it.data?.body() as CrossingHistoryApiResponse
                    apiResponse?.let { it1 ->
                        it1.transactionList?.let {
                            var listData = it.transaction
                            totalCount = it.count.toInt()
                            adapter.setListData(listData)
                            Log.d("ApiSuccess : ", listData?.size?.toString())
                        }


                    }

                }
                Status.LOADING -> {
                    requireActivity()!!.showToast("Data is loading")
                }
                Status.ERROR -> {
                    requireActivity()!!.showToast(it.message.toString())
                }
            }
        })
    }

    override fun onApplyBtnClicked(dataModel: DateRangeModel) {
        Log.d("dataModel", dataModel.type.toString())
        when (dataModel.type) {
            getString(R.string.last_30_days) ,
                getString(R.string.last_90_days),
                getString(R.string.custom)-> {
                request.run {
                    startIndex = "1"
                    count = "5"
                    transactionType = "Toll_Transaction"
                    searchDate = "Transaction Date"
                    startDate = "11/01/2021"
                    endDate = "11/30/2021"

                }

            }
            getString(R.string.view_all) -> {
                request.run {
                    startIndex = "1"
                    count = totalCount.toString()
                    transactionType = "ALL"

                }
            }

        }
        setObservers()
    }

    override fun onCancelBtnClicked() {
        Log.d("dialog", "Dismiss")
    }


}