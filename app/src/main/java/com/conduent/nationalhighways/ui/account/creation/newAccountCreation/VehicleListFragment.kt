package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentVehicleList2Binding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.ui.vehicle.newVehicleManagement.AddVehicleRequest
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleListFragment : BaseFragment<FragmentVehicleList2Binding>(),VehicleListAdapter.VehicleListCallBack,View.OnClickListener {

    private lateinit var vehicleList:ArrayList<NewVehicleInfoDetails>
    private lateinit var vehicleAdapter:VehicleListAdapter
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var apiRequestCount = 0
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleList2Binding= FragmentVehicleList2Binding.inflate(inflater,container,false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())

    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.btnAddNewVehicle.setOnClickListener(this)

    }

    override fun observer() {
        observe(vehicleMgmtViewModel.addVehicleApiVal, ::addVehicleApiCall)
    }

    override fun onResume() {
        super.onResume()
        invalidateList()
    }

    private fun addVehicleApiCall(status: Resource<EmptyApiResponse?>?) {
        var apiStatus = false
        when (status) {
            is Resource.Success -> {
                apiStatus = true
            }
            is Resource.DataError -> {
                apiStatus = false
            }
            else -> {
            }
        }

        if(indexExists(vehicleList,apiRequestCount)){
            vehicleList[apiRequestCount].status = apiStatus
            apiRequestCount++
        }

        if(apiRequestCount<vehicleList.size){
            apiCall(apiRequestCount)
        }else{
            loader?.dismiss()
            findNavController().navigate(R.id.action_vehicleListFragment_to_vehicleResultFragment)
        }
    }

    fun indexExists(list: List<*>, index: Int): Boolean {
        return index >= 0 && index < list.size
    }


    override fun vehicleListCallBack(
        position: Int,
        value: String,
        plateNumber: String?,
        isDblaAvailable: Boolean?
    ) {
        if (value== Constants.REMOVE_VEHICLE){
            val bundle = Bundle()
            bundle.putInt(Constants.VEHICLE_INDEX, position)
            findNavController().navigate(R.id.action_vehicleListFragment_to_removeVehicleFragment,bundle)
        }else{

            val bundle = Bundle()

            if(isDblaAvailable == true) {
                bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                findNavController().navigate(
                    R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment,
                    bundle
                )
            }else{
                bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                if (isDblaAvailable != null) {
                    bundle.putBoolean(Constants.IS_DBLA_AVAILABLE, isDblaAvailable)
                }
                findNavController().navigate(R.id.action_vehicleListFragment_to_addNewVehicleDetailsFragment,bundle)
            }
        }

    }

    private fun invalidateList() {
        val accountData = NewCreateAccountRequestModel
        vehicleList = accountData.vehicleList as ArrayList<NewVehicleInfoDetails>
        vehicleAdapter= VehicleListAdapter(requireContext(), vehicleList,this)
        val size = vehicleAdapter.itemCount
        var text ="vehicle"
        if(size>1){
            text ="vehicles"
        }
        binding.youHaveAddedVehicle.text = "You've added $size $text"
        if(size == 0){
            binding.btnNext.disable()
        }
        binding.recyclerView.adapter=vehicleAdapter
    }


    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btnAddNewVehicle -> {

                if(NewCreateAccountRequestModel.prePay){
                    if (vehicleList.size >= 10) {
                        NewCreateAccountRequestModel.isMaxVehicleAdded = true
                        findNavController().navigate(R.id.action_vehicleListFragment_to_maximumVehicleFragment)
                    } else {
                        if(NewCreateAccountRequestModel.isVehicleManagementCall){
                            if(vehicleList.size>=5){
                                NewCreateAccountRequestModel.isMaxVehicleAdded = true
                                findNavController().navigate(R.id.action_vehicleListFragment_to_maximumVehicleFragment)
                            }else{
                                findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment)
                            }
                        }else{
                            findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment)
                        }

                    }
                }else {
                    if (vehicleList.size >= 50000) {
                        NewCreateAccountRequestModel.isMaxVehicleAdded = true
                        findNavController().navigate(R.id.action_vehicleListFragment_to_maximumVehicleFragment)
                    } else {
                        if(NewCreateAccountRequestModel.isVehicleManagementCall){
                            if(vehicleList.size>=5){
                                NewCreateAccountRequestModel.isMaxVehicleAdded = true
                                findNavController().navigate(R.id.action_vehicleListFragment_to_maximumVehicleFragment)
                            }else{
                                findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment)
                            }
                        }else{
                            findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment)
                        }
                    }
                }
            }

            R.id.btnNext -> {
                if(NewCreateAccountRequestModel.isVehicleManagementCall){
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    if(vehicleList.size>0){
                        apiCall(apiRequestCount)
                    }


                }else {
                    findNavController().navigate(R.id.action_vehicleListFragment_to_createAccountSummaryFragment)
                }
            }
        }
    }

    private fun apiCall(index : Int) {
        val obj = vehicleList[index]
        val data = AddVehicleRequest()
        data.plateInfo?.number = obj.plateNumber
        if(obj.plateCountry.isNullOrEmpty()){
            data.plateInfo?.country = "UK"
        }else{
            data.plateInfo?.country = obj.plateCountry
        }
        data.plateInfo?.vehicleGroup = " "
        data.plateInfo?.vehicleComments = "new Vehicle"
        data.plateInfo?.planName = ""
        data.plateInfo?.state = "HE"
        data.plateInfo?.type = "STANDARD"

        data.vehicleInfo?.color = obj.vehicleColor
        data.vehicleInfo?.year = 2021
        data.vehicleInfo?.effectiveStartDate = ""
        if(obj.vehicleModel.isNullOrEmpty()){
            data.vehicleInfo?.model = "MODEL"
        }else{
            data.vehicleInfo?.model = obj.vehicleModel
        }
        data.vehicleInfo?.typeId = ""
        data.vehicleInfo?.typeDescription = "REGULAR"
        data.vehicleInfo?.make = obj.vehicleMake
//        if(obj.vehicleClass.isNullOrEmpty()){
            data.vehicleInfo?.vehicleClassDesc = "2"
//        }else{
//            data.vehicleInfo?.vehicleClassDesc = obj.vehicleClass
//        }


        vehicleMgmtViewModel.addVehicleApiNew(data)
    }
}