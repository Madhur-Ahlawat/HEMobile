package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleFragment : BaseFragment<FragmentAddVehicleBinding>(), View.OnClickListener, AddVehicleListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleBinding.inflate(inflater, container, false)

    override fun observer() { }

    override fun init() { }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.addVehicleBtn -> {
                AddVehicleDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                ).show(childFragmentManager, AddVehicleDialog.TAG)
            }
        }
    }

    override fun onAddClick(details: VehicleResponse) {
        val bundle = Bundle().apply {
            putSerializable(Constants.DATA, details)
        }
        findNavController().navigate(R.id.addVehicleDetailsFragment, bundle)
    }


}