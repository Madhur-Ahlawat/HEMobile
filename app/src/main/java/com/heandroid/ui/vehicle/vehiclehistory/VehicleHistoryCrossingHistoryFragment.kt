package com.heandroid.ui.vehicle.vehiclehistory

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.CrossingHistoryApiResponse
import com.heandroid.data.model.response.vehicle.CrossingHistoryItem
import com.heandroid.data.model.response.vehicle.CrossingHistoryRequest
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleHistoryCrossingHistoryBinding
import com.heandroid.gone
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.crossinghistory.CrossingHistoryAdapter
import com.heandroid.utils.Constants
import com.heandroid.utils.Resource
import com.heandroid.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleHistoryCrossingHistoryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var dataBinding: FragmentVehicleHistoryCrossingHistoryBinding
    private val viewModel: VehicleMgmtViewModel by viewModels()
    private lateinit var mVehicleDetails: VehicleResponse
    private var list : MutableList<CrossingHistoryItem?>? = ArrayList()
    private var isLoading = false
    private var isFirstTime=true
    private var totalCount: Int=0
    private var startIndex: Long=1
    private lateinit var request: CrossingHistoryRequest


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dataBinding = FragmentVehicleHistoryCrossingHistoryBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
        getVehicleCrossingHistoryData()
    }

    private fun getVehicleCrossingHistoryData() {
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        request = CrossingHistoryRequest(
            startIndex = startIndex,
            count = 5,
            transactionType = Constants.TOLL_TRANSACTION,
            plateNumber = mVehicleDetails.plateInfo.number
        )
        viewModel.crossingHistoryApiCall(request)
        dataBinding.tvNoCrossing.gone()
        dataBinding.rvVehicleCrossingHistory.gone()
        dataBinding.progressBar.visible()
        observer()
    }

    private fun init() {
            dataBinding.rvVehicleCrossingHistory.apply{
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = CrossingHistoryAdapter(this@VehicleHistoryCrossingHistoryFragment, list)
            }
    }

    private fun initCtrl() {
        dataBinding.apply {
            downloadCrossingHistoryBtn.setOnClickListener(this@VehicleHistoryCrossingHistoryFragment)
            backToVehicleListBtn.setOnClickListener(this@VehicleHistoryCrossingHistoryFragment)
        }
    }

    private fun observer() {
        viewModel.crossingHistoryVal.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                 is Resource.Success -> {
                    val response = resource.data?.body() as CrossingHistoryApiResponse
                        totalCount=response.transactionList?.transaction?.size?:0
                        if(response.transactionList!=null){
                            list?.addAll(response.transactionList.transaction)
                        }
                        isLoading=false

                        Handler(Looper.myLooper()!!).postDelayed( {
                            dataBinding.rvVehicleCrossingHistory.adapter?.notifyDataSetChanged()
                        },100)
                        dataBinding.progressBar.gone()
                        dataBinding.rvVehicleCrossingHistory.visible()

                        if(list?.size==0) {
                                dataBinding.tvNoCrossing.visible()
                            }

                        endlessScroll()

                }

                is Resource.DataError ->{

                }

                is Resource.Loading ->{

                }
                else -> {

                }
            }
        }
    }

    private fun endlessScroll() {
        if(isFirstTime) {
            isFirstTime=false
            dataBinding.rvVehicleCrossingHistory.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val linearLayoutManager =
                            recyclerView.layoutManager as LinearLayoutManager?
                        if (!isLoading) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == ((list?.size?:0)-1)  && totalCount>4) {
                                startIndex += 5
                                isLoading = true
                                request.startIndex = startIndex
                                dataBinding.progressBar.visible()
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
            R.id.download_crossing_history_btn -> {   }
            R.id.back_to_vehicle_list_btn -> {
                requireActivity().finish()
            }
        }
    }
}