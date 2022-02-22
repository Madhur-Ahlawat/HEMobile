package com.heandroid.ui.vehicle.vehiclehistory

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentVehicleHistoryCrossingHistoryBinding
import com.heandroid.databinding.FragmentVehicleHistoryListBinding
import com.heandroid.gone
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.ItemClickListener
import com.heandroid.ui.vehicle.VehicleMgmtActivity
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.crossinghistory.CrossingHistoryAdapter
import com.heandroid.utils.Constants
import com.heandroid.utils.Resource
import com.heandroid.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleHistoryListFragment : BaseFragment(), View.OnClickListener, ItemClickListener {

    private lateinit var dataBinding: FragmentVehicleHistoryListBinding
    private val mList = ArrayList<VehicleResponse>()
    private val vehicleMgmtViewModel : VehicleMgmtViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dataBinding = FragmentVehicleHistoryListBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
        getVehicleListApiCall()
//              setHistoryAdapter()
    }

    private fun init() {}

    private fun initCtrl() { }

    private fun getVehicleListApiCall() {
        vehicleMgmtViewModel.getVehicleInformationApi()
        vehicleMgmtViewModel.vehicleListVal.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data!!.body()?.let {
                        mList.clear()
                        mList.addAll(it)
                        setHistoryAdapter()
                    }
                }
                is Resource.DataError -> {
                    Toast.makeText(requireContext(), resource.errorMsg, Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {

                }
            }
        })
    }

    private fun setHistoryAdapter() {
        val list = listOf(VehicleResponse(PlateInfoResponse(), PlateInfoResponse(), VehicleInfoResponse("make",
            "audi", "2002", "type", "aa",
            "desc", "red", "class a", "12, 01, 3003"), false
        ))
        val mAdapter = VrmHistoryAdapter(this)
        mAdapter.setList(mList)
        dataBinding.rvVehicleHistoryList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onClick(v: View?) { }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) { }

    override fun onItemClick(details: VehicleResponse, pos: Int) {
        val bundle = Bundle().apply {
            putSerializable(Constants.DATA, details)
        }
        (requireActivity() as VehicleMgmtActivity).setVehicleItem(details)
        findNavController().navigate(R.id.vehicleHistoryVehicleDetailsFragment, bundle)
    }
}