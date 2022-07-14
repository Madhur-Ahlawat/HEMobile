package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.VehicleGroupResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleGroupBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.account.payments.history.AccountPaymentHistoryPaginationAdapter
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleGroupFragment : BaseFragment<FragmentVehicleGroupBinding>(),
    View.OnClickListener, SearchVehicleListener {

    private var vehicleResponseList: ArrayList<VehicleResponse?> = ArrayList()
    private lateinit var vehiclesAdapter: VehicleGroupVehiclesAdapter
    private val vehicleGroupMgmtViewModel: VehicleGroupMgmtViewModel by viewModels()
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var checkedVehicleList: ArrayList<VehicleResponse?> = ArrayList()
    var vehicleGroup: VehicleGroupResponse? = null
    private var loader: LoaderDialog? = null
    private var searchVehicleNumber: String? = null
    private var isRemoved = false
    private var isReloadVehicleData = false
    private var isGetVehicleListData = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleGroupBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        if (!isGetVehicleListData) {
            binding.progressBar.gone()
        }
        if (isReloadVehicleData) {
            binding.progressBar.visible()
            binding.tvNoVehicles.gone()
            binding.rvVehicleList.gone()
            checkedVehicleList.clear()
            vehicleResponseList.clear()
            getVehiclesData()
            isReloadVehicleData = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vehicleGroup = arguments?.getParcelable(Constants.DATA)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        vehiclesAdapter = VehicleGroupVehiclesAdapter(this, vehicleResponseList)
        getVehiclesData()
    }

    private fun getVehiclesData() {
        vehicleGroup?.let {
            if (it.groupName.equals(getString(R.string.unallocated_vehicle), true)
                && it.groupId?.isEmpty() == true
            ) {
                isGetVehicleListData = true
                vehicleMgmtViewModel.getVehicleInformationApi()
            } else {
                isGetVehicleListData = true
                vehicleGroupMgmtViewModel.getVehiclesOfGroupApi(it)
            }
        }
    }

    override fun init() {
        checkedVehicleList.clear()
        binding.addVehicleModel = true
        binding.removeVehicleModel = false
        binding.bulkUploadModel = false

        binding.prevBtnModel = false
        binding.nextBtnModel = false
        vehicleGroup?.let {
            binding.groupName.text = it.groupName
            if (it.groupName.equals(getString(R.string.unallocated_vehicle), true)
                && it.groupId?.isEmpty() == true
            ) {
                binding.removeVehicleBtn.gone()
                binding.addVehicleBtn.gone()
                binding.bulkUploadBtn.gone()
            }
        }
        binding.tvNoVehicles.gone()
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = vehiclesAdapter
        }
        checkButtons()
    }

    override fun initCtrl() {
        binding.apply {
            tvFilter.setOnClickListener(this@VehicleGroupFragment)
            addVehicleBtn.setOnClickListener(this@VehicleGroupFragment)
            removeVehicleBtn.setOnClickListener(this@VehicleGroupFragment)
            bulkUploadBtn.setOnClickListener(this@VehicleGroupFragment)
        }
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.removeVehiclesFromGroupApiVal, ::handleUpdatedVehicle)
        observe(vehicleGroupMgmtViewModel.vehicleListVal, ::handleVehicleListData)
        observe(vehicleGroupMgmtViewModel.searchVehicleVal, ::handleVehicleListData)
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleUnallocatedVehicleListData)
    }

    private fun handleUnallocatedVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        if (isGetVehicleListData) {
            binding.progressBar.gone()
            binding.tvNoVehicles.gone()
            binding.rvVehicleList.visible()
            checkedVehicleList.clear()
            vehicleResponseList.clear()
            checkButtons()
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        if (!it.isNullOrEmpty()) {
                            it.forEach { vehicle ->
                                if (vehicle?.vehicleInfo?.groupName.isNullOrEmpty()) {
                                    vehicleResponseList.add(vehicle)
                                }
                            }
                            if (vehicleResponseList.isEmpty()) {
                                handleVehicleData()
                            } else {
                                setVehicleListAdapter()
                            }
                        } else {
                            handleVehicleData()
                        }
                    }
                }
                is Resource.DataError -> {
                    handleVehicleData()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                    handleVehicleData()
                }
            }
            isGetVehicleListData = false
        }

        searchVehicleNumber = null
    }

    private fun handleUpdatedVehicle(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        if (isRemoved) {
            when (resource) {
                is Resource.Success -> {
                    requireActivity().showToast("vehicle(s) removed successfully")
                    getVehicleListData()
                }
                is Resource.DataError -> {
                    getVehicleListData()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                }
            }
            isRemoved = false
        }

    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        if (isGetVehicleListData) {
            binding.progressBar.gone()
            binding.tvNoVehicles.gone()
            binding.rvVehicleList.visible()
            checkedVehicleList.clear()
            vehicleResponseList.clear()
            checkButtons()
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        if (!it.isNullOrEmpty()) {
                            vehicleResponseList.addAll(it)
                            setVehicleListAdapter()
                        } else {
                            handleVehicleData()
                        }
                    }
                }
                is Resource.DataError -> {
                    handleVehicleData()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                    handleVehicleData()
                }
            }
            isGetVehicleListData = false
        }

        searchVehicleNumber = null
    }

    private fun handleVehicleData() {
        searchVehicleNumber?.let {
            binding.apply {
                rvVehicleList.gone()
                tvNoVehicles.visible()
                tvNoVehicles.text = getString(R.string.no_vehicles_found, it)
            }
        } ?: run {
            binding.apply {
                rvVehicleList.gone()
                tvNoVehicles.visible()
                tvNoVehicles.text = getString(R.string.str_no_vehicles)
            }
        }
    }

    private fun setVehicleListAdapter() {
        vehiclesAdapter = VehicleGroupVehiclesAdapter(this, vehicleResponseList)
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vehiclesAdapter
        }
    }

    private fun getVehicleListData() {
        binding.progressBar.visible()
        binding.rvVehicleList.gone()
        vehicleGroup?.let {
            if (it.groupName.equals(getString(R.string.unallocated_vehicle), true)
                && it.groupId!!.isEmpty()
            ) {
                isGetVehicleListData = true
                vehicleMgmtViewModel.getVehicleInformationApi()
            } else {
                isGetVehicleListData = true
                vehicleGroupMgmtViewModel.getVehiclesOfGroupApi(it)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvFilter -> {
                SearchVehicleDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                ).show(childFragmentManager, Constants.SEARCH_VEHICLE_DIALOG)
            }
            R.id.addVehicleBtn -> {
                isReloadVehicleData = true
                vehicleGroup?.let {
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, it)
                    }
                    findNavController().navigate(
                        R.id.action_vehicleGroupFragment_to_vehicleGroupAddVehicleFragment,
                        bundle
                    )
                }
            }
            R.id.removeVehicleBtn -> {
                if (checkedVehicleList.size >= 1) {
                    isRemoved = true
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    vehicleMgmtViewModel.removeVehiclesFromGroup(checkedVehicleList)
                }
            }
            R.id.bulkUploadBtn -> {

            }
        }
    }

    fun setSelectedVehicle(vehicle: VehicleResponse?) {
        vehicle?.let {
            if (!checkedVehicleList.contains(it)) {
                checkedVehicleList.add(it)
            } else {
                checkedVehicleList.remove(it)
            }
            checkButtons()
        }
    }

    private fun checkButtons() {
        when {
            checkedVehicleList.size > 0 -> {
                binding.removeVehicleModel = true
            }
            else -> {
                binding.removeVehicleModel = false
            }
        }
    }

    override fun onClick(plateNumber: String) {
        vehicleGroup?.let {
            binding.progressBar.visible()
            binding.rvVehicleList.gone()
            searchVehicleNumber = plateNumber
            vehicleGroup?.let {
                vehicleGroupMgmtViewModel.getSearchVehiclesForGroup(
                    it.groupName!!,
                    plateNumber
                )
            }
        }

    }
}
