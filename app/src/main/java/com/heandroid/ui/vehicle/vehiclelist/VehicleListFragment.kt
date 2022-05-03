package com.heandroid.ui.vehicle.vehiclelist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleListBinding
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class VehicleListFragment : BaseFragment<FragmentVehicleListBinding>(), View.OnClickListener,
    ItemClickListener, AddVehicleListener, RemoveVehicleListener {

    private var mList: ArrayList<VehicleResponse?> = ArrayList()
    private lateinit var mAdapter: VehicleListAdapter
    private var loader: LoaderDialog? = null
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var isAccountVehicle: Boolean? = false
    private var pos: Int = 0
    private var isBusinessAccount = false
    @Inject
    lateinit var sessionManager: SessionManager
    private val createAccVehicleViewModel: CreateAccountVehicleViewModel by viewModels()
    private var currentPos: Int = 0

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleListBinding.inflate(inflater, container, false)

    override fun init() {

        val buttonVisibility = arguments?.getBoolean(Constants.DATA, false) == true
        isAccountVehicle = arguments?.getBoolean("IsAccountVehicle")

        if (buttonVisibility) {
            binding.addVehicleBtn.gone()
            binding.removeVehicleBtn.gone()
        }
        sessionManager.fetchAccountType()?.let {
            if (it == Constants.BUSINESS_ACCOUNT){
                isBusinessAccount = true
            }
        }

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        binding.removeVehicleBtn.setOnClickListener(this)

        binding.addVehicleBtn.text = resources.getString(R.string.str_add_another_vehicle)
        binding.removeVehicleBtn.text = resources.getString(R.string.str_remove_vehicle)
        getVehicleListData()
    }

    private fun getVehicleListData() {

        loader?.show(requireActivity().supportFragmentManager, "")
        vehicleMgmtViewModel.getVehicleInformationApi()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addVehicleBtn -> {

                AddVehicleDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                ).show(childFragmentManager, AddVehicleDialog.TAG)


            }
            R.id.removeVehicleBtn -> {
                RemoveVehicleDialog.newInstance(
                    mList,
                    this
                ).show(childFragmentManager, AddVehicleDialog.TAG)
            }
        }
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleListData)
        observe(vehicleMgmtViewModel.deleteVehicleApiVal, ::handleDeleteVehicle)
    }


    private fun handleDeleteVehicle(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()

        when (resource) {
            is Resource.Success -> {
                requireContext().showToast("vehicle deleted successfully")
                getVehicleListData()
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }

    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        loader?.dismiss()

        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (!it.isNullOrEmpty()) {
                        mList.clear()
                        mList.addAll(it)
                        setVehicleListAdapter(mList)
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

    private fun setVehicleListAdapter(mList: ArrayList<VehicleResponse?>) {
        this.mList = mList
        mAdapter = VehicleListAdapter(requireContext(), this, isBusinessAccount)
        mAdapter.setList(mList)
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {

    }

    override fun onItemClick(details: VehicleResponse?, pos: Int) {

        details?.isExpanded = details?.isExpanded != true
        mList[pos]?.isExpanded = details?.isExpanded

        mAdapter.notifyItemChanged(pos)
    }

    override fun onAddClick(details: VehicleResponse) {
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, details)
            putParcelable(
                Constants.CREATE_ACCOUNT_DATA,
                arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
            )
        }
        findNavController().navigate(R.id.addVehicleDetailsFragment, bundle)
    }

    override fun onRemoveClick(selectedVehicleList: List<String?>) {
        loader?.show(requireActivity().supportFragmentManager, "")
        vehicleMgmtViewModel.deleteVehicleApi(DeleteVehicleRequest(selectedVehicleList[0]))
    }
}