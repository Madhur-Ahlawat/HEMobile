package com.heandroid.ui.vehicle.vehiclehistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleHistoryListBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.SelectedVehicleViewModel
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleHistoryListFragment : BaseFragment<FragmentVehicleHistoryListBinding>(),
    ItemClickListener {

    private val mList: ArrayList<VehicleResponse?> = ArrayList()
    private var loader: LoaderDialog? = null
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private val selectedViewModel: SelectedVehicleViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleHistoryListBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        loader?.show(requireActivity().supportFragmentManager, "")
        vehicleMgmtViewModel.getVehicleInformationApi()
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleHistoryListData)
    }

    private fun handleVehicleHistoryListData(resource: Resource<List<VehicleResponse?>?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (!resource.data.isNullOrEmpty()) {
                    mList.clear()
                    mList.addAll(resource.data)
                    setHistoryAdapter()
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun setHistoryAdapter() {
        val mAdapter = VrmHistoryAdapter(this)
        mAdapter.setList(mList)
        binding.rvVehicleHistoryList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {}

    override fun onItemClick(details: VehicleResponse, pos: Int) {
//        val bundle = Bundle().apply {
//            putSerializable(Constants.DATA, details)
//        }
//        (requireActivity() as VehicleMgmtActivity).setVehicleItem(details)
        selectedViewModel.setSelectedVehicleResponse(details)
        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_vehicleHistoryVehicleDetailsFragment)
    }

}