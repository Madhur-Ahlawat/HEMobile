package com.conduent.nationalhighways.ui.vehicle.vehiclehistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.SelectedVehicleViewModel
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.dialog.ItemClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VehicleHistoryListFragment : BaseFragment<FragmentVehicleList2Binding>(),
    ItemClickListener,View.OnClickListener {

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
    private var isBusinessAccount = false
    private var needRefresh = true

    @Inject
    lateinit var sessionManager: SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleList2Binding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = VrmHistoryAdapter(activity,this)
        isVehicleHistory = true

    }

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.btnNext.setOnClickListener(this)
        binding.btnNext.setText(getString(R.string.add_a_vehicle))
        binding.btnAddNewVehicle.visibility = View.GONE
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = mAdapter
        sessionManager.fetchAccountType()?.let {
            if (it == Constants.BUSINESS_ACCOUNT||it==Constants.EXEMPT_ACCOUNT) {
                isBusinessAccount = true
            }
        }
        binding.youHaveAddedVehicle.text = getString(R.string.vehicle_list)
        NewCreateAccountRequestModel.isVehicleManagementCall = false
    }

    override fun initCtrl() {}

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleHistoryListData)
    }

    override fun onResume() {
        super.onResume()
        if(needRefresh) {
            showLoader()
            vehicleMgmtViewModel.getVehicleInformationApi(startIndex.toString(), count.toString())
            needRefresh = false
        }
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

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {
        needRefresh = true
        val bundle = Bundle()
        bundle.putInt(Constants.VEHICLE_INDEX, -2)
        bundle.putParcelable(Constants.DATA, details)
        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_removeVehicleFragment,bundle)
    }

    override fun onItemClick(details: VehicleResponse?, pos: Int) {
        selectedViewModel.setSelectedVehicleResponse(details)
        val bundle = Bundle()
        bundle.putInt(Constants.VEHICLE_INDEX, -1)
        bundle.putParcelable(Constants.DATA, details)
        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_removeVehicleFragment,bundle)
    }

    private fun showLoader() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
    }

    private fun hideLoader() {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
    }


    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btnNext -> {
                if(isBusinessAccount.not()){
                    if (mList.size >= 10) {
                        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_maximumVehicleFragment)
                    } else {
                        NewCreateAccountRequestModel.isVehicleManagementCall = true
                        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_createAccountFindVehicleFragment)
                    }
                }else {
                    if (mList.size >= 50000) {
                        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_maximumVehicleFragment)
                    } else {
                        NewCreateAccountRequestModel.isVehicleManagementCall = true
                        findNavController().navigate(R.id.action_vehicleHistoryListFragment_to_createAccountFindVehicleFragment)
                    }
                }            }
        }
    }
}

