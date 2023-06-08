package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentVehicleList2Binding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil


class VehicleListFragment : BaseFragment<FragmentVehicleList2Binding>(),VehicleListAdapter.VehicleListCallBack,View.OnClickListener {

    private lateinit var vehicleList:ArrayList<NewVehicleInfoDetails>
    private lateinit var vehicleAdapter:VehicleListAdapter


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleList2Binding= FragmentVehicleList2Binding.inflate(inflater,container,false)

    override fun init() {
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())

    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.btnAddNewVehicle.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onResume() {
        super.onResume()
        invalidateList()
    }


    override fun vehicleListCallBack(position: Int, value: String) {
        if (value== Constants.REMOVE_VEHICLE){
            if(vehicleList.size==1){
                ErrorUtil.showError(binding.root, getString(R.string.it_is_required_that_you_have_two_vehicles_before_you_are_able_to_delete_one_of_them))
            }else {
                val bundle = Bundle()
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                findNavController().navigate(R.id.action_vehicleListFragment_to_removeVehicleFragment)
            }
        }else{

        }

    }

    private fun invalidateList() {
        val accountData = NewCreateAccountRequestModel
        vehicleList = accountData.vehicleList as ArrayList<NewVehicleInfoDetails>
        vehicleAdapter= VehicleListAdapter(requireContext(), vehicleList,this)
            val size = vehicleAdapter.itemCount
            var text ="vehicle"
            if(size>1){
                text ="vehicles"
            }
            binding.youHaveAddedVehicle.text = "You've added $size $text"
            if(size == 0){
                binding.btnNext.disable()
            }
        binding.recyclerView.adapter=vehicleAdapter
    }


    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btnAddNewVehicle -> {

                if(NewCreateAccountRequestModel.prePay){
                    if (vehicleList.size >= 10) {
                        findNavController().navigate(R.id.action_vehicleListFragment_to_maximumVehicleFragment)
                    } else {
                        findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment)
                    }
                }else {
                    if (vehicleList.size >= 50000) {
                        findNavController().navigate(R.id.action_vehicleListFragment_to_maximumVehicleFragment)
                    } else {
                        findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment)
                    }
                }
            }

            R.id.btnNext -> {
                findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountSummaryFragment)
            }
        }
        }
    }