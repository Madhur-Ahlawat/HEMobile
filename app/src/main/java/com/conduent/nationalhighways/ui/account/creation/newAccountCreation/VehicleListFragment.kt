package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentVehicleList2Binding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class VehicleListFragment : BaseFragment<FragmentVehicleList2Binding>(),VehicleListAdapter.VehicleListCallBack,View.OnClickListener {

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
        vehicleAdapter= VehicleListAdapter(requireContext(), vehicleList,this)
        binding.recyclerView.adapter=vehicleAdapter


    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)

    }

    override fun observer() {
    }



    override fun vehicleListCallBack(position: Int, value: String) {
        if (value== Constants.REMOVE_VEHICLE){
            vehicleList.removeAt(position)
            vehicleAdapter.notifyDataSetChanged()

        }else{

        }
    }


    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnNext->{
                if (vehicleList.size>=5){
                    findNavController().navigate(R.id.action_vehicleListFragment_to_maximumVehicleFragment)

                }else{
                    findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment)

                }
            }
        }
    }

}