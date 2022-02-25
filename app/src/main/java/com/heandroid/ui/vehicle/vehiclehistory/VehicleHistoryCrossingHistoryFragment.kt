package com.heandroid.ui.vehicle.vehiclehistory

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.request.vehicle.CrossingHistoryRequest
import com.heandroid.data.model.response.vehicle.CrossingHistoryApiResponse
import com.heandroid.data.model.response.vehicle.CrossingHistoryItem
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleHistoryCrossingHistoryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.crossinghistory.CrossingHistoryAdapter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleHistoryCrossingHistoryFragment :
    BaseFragment<FragmentVehicleHistoryCrossingHistoryBinding>(), View.OnClickListener {

    private val viewModel: VehicleMgmtViewModel by activityViewModels()
    private lateinit var mVehicleDetails: VehicleResponse
    private var list: MutableList<CrossingHistoryItem?>? = ArrayList()
    private var isLoading = false
    private var isFirstTime = true
    private var totalCount: Int = 0
    private var startIndex: Long = 1
    private lateinit var request: CrossingHistoryRequest

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
        observe(viewModel.selectedVehicleResponse, ::handleSelectedVehicleResponse)
        observe(viewModel.crossingHistoryVal, ::handleVehicleCrossingHistoryResponse)
    }

    private fun handleVehicleCrossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    totalCount = it.transactionList?.transaction?.size ?: 0
                    it.transactionList?.let { transitionList ->
                        list?.addAll(transitionList.transaction)
                    }
                    isLoading = false
                    Handler(Looper.myLooper()!!).postDelayed({
                        binding.rvVehicleCrossingHistory.adapter?.notifyDataSetChanged()
                    }, 100)
                    binding.progressBar.gone()
                    binding.rvVehicleCrossingHistory.visible()
                    if (list?.size == 0) {
                        binding.tvNoCrossing.visible()
                    }
                    endlessScroll()
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
                // do nothing
            }
        }

    }

    private fun handleSelectedVehicleResponse(vehicleResponse: VehicleResponse?) {
        vehicleResponse?.let {
            mVehicleDetails = it
            getVehicleCrossingHistoryData()
        }
    }

    private fun getVehicleCrossingHistoryData() {
        request = CrossingHistoryRequest(
            startIndex = startIndex,
            count = 5,
            transactionType = Constants.TOLL_TRANSACTION,
            plateNumber = mVehicleDetails.plateInfo.number
        )
        viewModel.crossingHistoryApiCall(request)
        binding.tvNoCrossing.gone()
        binding.rvVehicleCrossingHistory.gone()
        binding.progressBar.visible()
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.download_crossing_history_btn -> {
            }
            R.id.back_to_vehicle_list_btn -> {
                findNavController().popBackStack()
            }
        }
    }


}