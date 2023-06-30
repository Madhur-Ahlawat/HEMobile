package com.conduent.nationalhighways.ui.vehicle.vehiclehistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentVehicleList2Binding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.SelectedVehicleViewModel
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.dialog.ItemClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleHistoryListFragment : BaseFragment<FragmentVehicleList2Binding>(),
    ItemClickListener {

    private val mList: ArrayList<VehicleResponse?> = ArrayList()
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private val selectedViewModel: SelectedVehicleViewModel by activityViewModels()
    private lateinit var mAdapter: VrmHistoryAdapter
    private var loader: LoaderDialog? = null
    private var startIndex: Long = 1
    private val count: Long = Constants.ITEM_COUNT
    private var totalCount: Int = 0
    private var isLoading = false
    private var isFirstTime = true
    private var isVehicleHistory = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleList2Binding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = VrmHistoryAdapter(this)
        isVehicleHistory = true
        vehicleMgmtViewModel.getVehicleInformationApi(startIndex.toString(), count.toString())
    }

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = mAdapter
    }

    override fun initCtrl() {}

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleHistoryListData)
    }

    private fun handleVehicleHistoryListData(resource: Resource<List<VehicleResponse?>?>?) {
        binding.recyclerView.visible()
        hideLoader()
        if (isVehicleHistory) {
            isVehicleHistory = false
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        val response = resource.data
                        totalCount = response.size
                        mList.addAll(response)
                        isLoading = false
                        mAdapter.setList(mList)
                        binding.recyclerView.adapter?.notifyDataSetChanged()

                        if (mList.size == 0) {
                            binding.recyclerView.gone()
//                            binding.tvNoVehicles.visible()
                            hideLoader()
                        } else {
                            binding.recyclerView.visible()
                            hideLoader()
//                            binding.tvNoVehicles.gone()
                        }
                        endlessScroll()
                    }
                }
                is Resource.DataError -> {
                    binding.recyclerView.gone()
                    hideLoader()
//                    binding.tvNoVehicles.visible()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                }
            }
        }

    }

    private fun endlessScroll() {
        if (isFirstTime) {
            isFirstTime = false
            binding.recyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        if (!isLoading) {
                            if (linearLayoutManager != null &&
                                linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                (mList.size - 1) && totalCount > count - 1
                            ) {
                                isVehicleHistory = true
                                startIndex += count
                                isLoading = true
                                showLoader()
                                vehicleMgmtViewModel.getVehicleInformationApi(
                                    startIndex.toString(),
                                    count.toString()
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {}

    override fun onItemClick(details: VehicleResponse?, pos: Int) {
        selectedViewModel.setSelectedVehicleResponse(details)
        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_vehicleHistoryVehicleDetailsFragment)
    }

    private fun showLoader() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
    }

    private fun hideLoader() {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
    }

}