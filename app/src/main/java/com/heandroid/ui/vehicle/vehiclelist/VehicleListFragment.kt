package com.heandroid.ui.vehicle.vehiclelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentVehicleListBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.AddVehicleListener
import com.heandroid.ui.vehicle.ItemClickListener
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.utils.Constants
import com.heandroid.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleListFragment : BaseFragment(), View.OnClickListener, ItemClickListener,
    AddVehicleListener {

    private lateinit var dataBinding: FragmentVehicleListBinding
    private val mList = ArrayList<VehicleResponse>()
    private lateinit var mAdapter: VehicleListAdapter
    private val vehicleMgmtViewModel : VehicleMgmtViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dataBinding = FragmentVehicleListBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
        getVehicleListApiCall()
    }

    private fun init() {
    }

    private fun initCtrl() {
        dataBinding.addVehicleBtn.setOnClickListener {
            AddVehicleDialog.newInstance(
                getString(R.string.str_title),
                getString(R.string.str_sub_title),
                this
            ).show(childFragmentManager, AddVehicleDialog.TAG)
        }
    }

    private fun getVehicleListApiCall() {
        vehicleMgmtViewModel.getVehicleInformationApi()
        vehicleMgmtViewModel.vehicleListVal.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data!!.body()?.let {
                        mList.clear()
                        mList.addAll(it)
                        setVehicleListAdapter()
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

    private fun setVehicleListAdapter() {
        mAdapter = VehicleListAdapter(requireContext())
        mAdapter.setList(mList)
        dataBinding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onClick(v: View?) { }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) { }

    override fun onItemClick(details: VehicleResponse, pos: Int) {
        details.isExpanded = !details.isExpanded
        mList[pos].isExpanded = details.isExpanded
        mAdapter.notifyItemChanged(pos)
    }

    override fun onAddClick(details: VehicleResponse) {
        val bundle = Bundle().apply {
            putSerializable(Constants.DATA, details)
        }
        findNavController().navigate(R.id.addVehicleDetailsFragment, bundle)
    }
}