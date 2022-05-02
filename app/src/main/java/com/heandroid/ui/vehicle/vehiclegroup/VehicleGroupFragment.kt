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
    private var paginationNumberAdapter: AccountPaymentHistoryPaginationAdapter? = null
    private var paginationLinearLayoutManager: LinearLayoutManager? = null
    private var vehicleGroup: VehicleGroupResponse? = null
    private val countPerPage = 10
    private var loader: LoaderDialog? = null
    private var searchVehicleNumber: String? = null
    private var startIndex = 1
    private var noOfPages = 1
    private var selectedPosition = 1


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleGroupBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vehicleGroup = arguments?.getParcelable(Constants.DATA)

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        paginationNumberAdapter =
            AccountPaymentHistoryPaginationAdapter(this, noOfPages, selectedPosition)

        vehiclesAdapter = VehicleGroupVehiclesAdapter(this, vehicleResponseList)
        vehicleGroup?.let { vehicleGroupMgmtViewModel.getVehiclesOfGroupApi(it) }

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
        }
        binding.paginationLayout.visible()
        binding.progressBar.visible()
        binding.tvNoVehicles.gone()
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = vehiclesAdapter
        }
        paginationLinearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.paginationNumberRecyclerView.apply {
            layoutManager = paginationLinearLayoutManager
            adapter = paginationNumberAdapter
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
        observe(vehicleMgmtViewModel.updateVehicleApiVal, ::handleUpdatedVehicle)
        observe(vehicleGroupMgmtViewModel.vehicleListVal, ::handleVehicleListData)
        observe(vehicleGroupMgmtViewModel.searchVehicleVal, ::handleVehicleListData)
    }

    private fun handleUpdatedVehicle(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                requireActivity().showToast("vehicle removed successfully")
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        binding.progressBar.gone()
        binding.tvNoVehicles.gone()
        binding.rvVehicleList.visible()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (!it.isNullOrEmpty()) {
                        checkedVehicleList.clear()
                        vehicleResponseList.clear()
                        vehicleResponseList.addAll(it)
                        setVehicleListAdapter()
                    } else {
                        searchVehicleNumber?.let {
                            binding.apply {
                                rvVehicleList.gone()
                                paginationLayout.gone()
                                tvNoVehicles.visible()
                                tvNoVehicles.text = getString(R.string.no_vehicles_found, it)
                            }
                        } ?: run {
                            binding.apply {
                                rvVehicleList.gone()
                                paginationLayout.gone()
                                tvNoVehicles.visible()
                                tvNoVehicles.text = getString(R.string.str_no_vehicles)
                            }
                        }
                    }
                }
            }
            is Resource.DataError -> {
                searchVehicleNumber?.let {
                    binding.apply {
                        rvVehicleList.gone()
                        paginationLayout.gone()
                        tvNoVehicles.visible()
                        tvNoVehicles.text = getString(R.string.no_vehicles_found, it)
                    }
                } ?: run {
                    binding.apply {
                        rvVehicleList.gone()
                        paginationLayout.gone()
                        tvNoVehicles.visible()
                        tvNoVehicles.text = getString(R.string.str_no_vehicles)
                    }
                }
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
        searchVehicleNumber = null
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
        vehicleGroup?.let { vehicleGroupMgmtViewModel.getVehiclesOfGroupApi(it) }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvFilter -> {
                SearchVehicleDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                ).show(childFragmentManager, "")
            }
            R.id.addVehicleBtn -> {
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
                if (checkedVehicleList.size > 1) {
                    requireActivity().showToast("multiple vehicles cant be removed, it is in dev")
                } else if (checkedVehicleList.size == 1) {
                    checkedVehicleList[0]?.let {
                        val request = it.apply {
                            newPlateInfo = plateInfo
                            newPlateInfo?.vehicleGroup = ""
                        }
                        loader?.show(requireActivity().supportFragmentManager, "")
                        vehicleMgmtViewModel.updateVehicleApi(request)
                    }
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
                    it.groupName,
                    plateNumber
                )
            }
        }

    }
}
