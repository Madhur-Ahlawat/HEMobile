package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.databinding.FragmentVehicleList2Binding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment


class VehicleListFragment : BaseFragment<FragmentVehicleList2Binding>() {

    private lateinit var vehicleList:ArrayList<String>
    private lateinit var vehicleAdapter:VehicleListAdapter


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleList2Binding= FragmentVehicleList2Binding.inflate(inflater,container,false)

    override fun init() {
        vehicleList= ArrayList()
        vehicleList.add("s")



        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        vehicleAdapter= VehicleListAdapter(requireContext(), vehicleList)
        binding.recyclerView.adapter=vehicleAdapter


    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}