package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentAddVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.AddVehicleListener
import com.heandroid.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleFragment : BaseFragment(), View.OnClickListener, AddVehicleListener {

    private lateinit var dataBinding: FragmentAddVehicleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
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

    override fun onClick(v: View?) {}

    override fun onAddClick(details: VehicleResponse) {
        val bundle = Bundle().apply {
            putSerializable(Constants.DATA, details)
        }
        findNavController().navigate(R.id.addVehicleDetailsFragment, bundle)
    }
}