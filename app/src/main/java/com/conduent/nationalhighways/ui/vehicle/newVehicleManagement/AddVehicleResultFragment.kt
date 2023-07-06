package com.conduent.nationalhighways.ui.vehicle.newVehicleManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentVehicleList2Binding
import com.conduent.nationalhighways.databinding.VehicleSuccessFragmentBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class AddVehicleResultFragment : BaseFragment<VehicleSuccessFragmentBinding>(),View.OnClickListener {

    private lateinit var vehicleList:ArrayList<NewVehicleInfoDetails>

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VehicleSuccessFragmentBinding= VehicleSuccessFragmentBinding.inflate(inflater,container,false)

    override fun init() {
        binding.recyclerViewSuccess.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerViewFaied.layoutManager=LinearLayoutManager(requireContext())

    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onResume() {
        super.onResume()
        invalidateList()
    }


    private fun invalidateList() {
        val accountData = NewCreateAccountRequestModel
        vehicleList = accountData.vehicleList as ArrayList<NewVehicleInfoDetails>
        val tempSuccessList : ArrayList<NewVehicleInfoDetails> =  arrayListOf()
        val tempFailedList :ArrayList<NewVehicleInfoDetails> =  arrayListOf()
        for(obj in vehicleList){
            if(obj.status == true){
                tempSuccessList.add(obj)
            }else {
                tempFailedList.add(obj)
            }
        }
        if(tempSuccessList.isEmpty()) {
            binding.vehicleAddedNote.visibility = View.GONE
            binding.recyclerViewSuccess.visibility = View.GONE
            binding.warningIcon2.visibility = View.GONE
        }else{
            val vehicleAdapterSuccess= VehiclesResultAdapter(requireContext(), tempSuccessList)
            binding.recyclerViewSuccess.adapter = vehicleAdapterSuccess
        }
        if(tempFailedList.isEmpty()) {
            binding.vehicleNotAdded.visibility = View.GONE
            binding.recyclerViewFaied.visibility = View.GONE
            binding.warningIcon2.visibility = View.GONE
        }else{
            val vehicleAdapterFailed = VehiclesResultAdapter(requireContext(), tempFailedList)
            binding.recyclerViewFaied.adapter = vehicleAdapterFailed
        }
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnContinue -> {
                NewCreateAccountRequestModel.vehicleList.clear()
                findNavController().navigate(R.id.action_vehicleResultFragment_to_vehicleHistoryListFragment)
            }
        }
    }
}