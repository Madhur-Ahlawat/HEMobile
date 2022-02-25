package com.heandroid.ui.vehicle.vehiclelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleListBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleListFragment : BaseFragment<FragmentVehicleListBinding>(), View.OnClickListener, ItemClickListener,
    AddVehicleListener {

    private val mList : ArrayList<VehicleResponse?> = ArrayList()
    private lateinit var mAdapter: VehicleListAdapter
    private var loader: LoaderDialog?=null
    private val vehicleMgmtViewModel : VehicleMgmtViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleListBinding.inflate(inflater, container, false)

    override fun init() {
        loader= LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        vehicleMgmtViewModel.getVehicleInformationApi()
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

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleListData)
    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
//        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (!it.isNullOrEmpty()){
                        mList.clear()
                        mList.addAll(it)
                        setVehicleListAdapter()
                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun setVehicleListAdapter() {
        mAdapter = VehicleListAdapter(requireContext())
        mAdapter.setList(mList)
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }


    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {

    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {
        details.isExpanded = !details.isExpanded
        mList[pos]?.isExpanded = details.isExpanded
        mAdapter.notifyItemChanged(pos)
    }

    override fun onAddClick(details: VehicleResponse) {
        val bundle = Bundle().apply {
            putSerializable(Constants.DATA, details)
        }
        findNavController().navigate(R.id.addVehicleDetailsFragment, bundle)
    }

}