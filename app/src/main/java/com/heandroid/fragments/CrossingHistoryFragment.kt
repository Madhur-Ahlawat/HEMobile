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
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.showToast
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory

class CrossingHistoryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var adapter: CrossingHistoryAdapter
    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var dataBinding: FragmentCrossingHistoryBinding

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

        var request = CrossingHistoryRequest().apply {
            startIndex = "1"
            count = "2"
            transactionType = "ALL"
        }
        vehicleMgmtViewModel.crossingHistoryApiCall(request);

        vehicleMgmtViewModel.crossingHistoryVal.observe(requireActivity()!!, { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    var apiResponse = it.data?.body() as CrossingHistoryApiResponse
                    apiResponse?.let {
                        var listData = it.transactionList.transaction

                        adapter.setListData(listData)
                        adapter.notifyDataSetChanged()
                        Log.d("ApiSuccess : ", listData.size.toString())

                        requireActivity()!!.showToast("Vehicle is updated successfully")
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


}