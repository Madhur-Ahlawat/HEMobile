package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentCreateAccountSummaryBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class CreateAccountSummaryFragment : BaseFragment<FragmentCreateAccountSummaryBinding>(),VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {

    private lateinit var vehicleAdapter: VehicleListAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateAccountSummaryBinding= FragmentCreateAccountSummaryBinding.inflate(inflater,container,false)

    override fun init() {
        binding.btnNext.setOnClickListener(this)
        val dataModel = NewCreateAccountRequestModel
        (dataModel.firstName+ " "+ dataModel.lastName).also { binding.fullName.text = it }
        if(dataModel.communicationTextMessage){
            binding.communications.text = getString(R.string.yes)
        }else{
            binding.communications.text = getString(R.string.no)
        }

        if(dataModel.twoStepVerification){
            binding.twoStepVerification.text = getString(R.string.yes)
        }else{
            binding.twoStepVerification.text = getString(R.string.no)
        }

        binding.address.text = dataModel.addressline1
        binding.emailAddress.text = dataModel.emailAddress
        binding.mobileNumber.text = dataModel.mobileNumber
        if(dataModel.prePay){
            binding.accountType.text = getString(R.string.str_prepay)
        } else if(dataModel.payAsYouGo){
            binding.accountType.text = getString(R.string.str_payg_account)
        }

        binding.recyclerView.layoutManager= LinearLayoutManager(requireContext())
        val vehicleList = dataModel.vehicleList as ArrayList<NewVehicleInfoDetails>
        vehicleAdapter= VehicleListAdapter(requireContext(), vehicleList,this)
        binding.recyclerView.adapter=vehicleAdapter

        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.btnNext.enable()
            }else{
                binding.btnNext.disable()
            }
        }
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnNext->{

            }
        }
    }


    override fun vehicleListCallBack(position: Int, value: String, plateNumber: String?) {
        if (value== Constants.REMOVE_VEHICLE){
            val bundle = Bundle()
            bundle.putInt(Constants.VEHICLE_INDEX, position)
            findNavController().navigate(R.id.action_accountSummaryFragment_to_removeVehicleFragment)
        }else{

        }

    }

}