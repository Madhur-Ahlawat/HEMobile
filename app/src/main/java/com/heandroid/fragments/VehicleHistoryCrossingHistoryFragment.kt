package com.heandroid.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.CrossingHistoryAdapter
import com.heandroid.databinding.FragmentCrossingHistoryMakePaymentBinding
import com.heandroid.databinding.FragmentVehicleHistoryCrossingHistoryBinding
import com.heandroid.gone
import com.heandroid.model.VehicleResponse
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.Utils
import com.heandroid.utils.Utils.getDirection
import com.heandroid.utils.Utils.loadStatus
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.visible
import kotlinx.android.synthetic.main.fragment_vehicle_history_crossing_history.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VehicleHistoryCrossingHistoryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var dataBinding: FragmentVehicleHistoryCrossingHistoryBinding
    private lateinit var viewModel: VehicleMgmtViewModel
    private lateinit var mVehicleDetails: VehicleResponse
    private var list : MutableList<CrossingHistoryItem?>? = ArrayList()

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
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 10,
            transactionType = Constants.ALL_TRANSACTION
//            plateNumber = mVehicleDetails.newPlateInfo.number
        )
        viewModel.crossingHistoryApiCall(request)
        observer()
    }

    private fun init() {
            val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
            viewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
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
        viewModel.crossingHistoryVal.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS ->{
                    val response = it.data?.body() as CrossingHistoryApiResponse
                    list?.addAll(response.transactionList.transaction)
                    dataBinding.rvVehicleCrossingHistory.adapter?.notifyDataSetChanged()
                }

                Status.ERROR ->{

                }
            }
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