package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.BuildConfig
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
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleListFragment : BaseFragment<FragmentVehicleList2Binding>(),
    VehicleListAdapter.VehicleListCallBack, View.OnClickListener {

    private lateinit var vehicleList: ArrayList<NewVehicleInfoDetails>
    private lateinit var vehicleAdapter: VehicleListAdapter
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()

    private var loader: LoaderDialog? = null
    private var apiRequestCount = 0
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleList2Binding = FragmentVehicleList2Binding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.btnAddNewVehicle.setOnClickListener(this)

    }

    override fun observer() {
        observe(vehicleMgmtViewModel.addVehicleApiVal, ::addVehicleApiCall)

    }

    private fun heartBeatApiResponse(resource: Resource<EmptyApiResponse?>?) {

    }

    override fun onResume() {
        super.onResume()
        invalidateList()
    }

    private fun addVehicleApiCall(status: Resource<EmptyApiResponse?>?) {
        var apiStatus = false
        var sessionExpiry = false
        when (status) {
            is Resource.Success -> {
                apiStatus = true
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    sessionExpiry = true
                    displaySessionExpireDialog(status.errorModel)
                }
                apiStatus = false
            }

            else -> {
            }
        }
        if (!sessionExpiry) {
            if (indexExists(vehicleList, apiRequestCount)) {
                vehicleList[apiRequestCount].status = apiStatus
                apiRequestCount++
            }

            if (apiRequestCount < vehicleList.size) {
                apiCall(apiRequestCount)
            } else {
                loader?.dismiss()
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.EDIT_SUMMARY)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_vehicleListFragment_to_vehicleResultFragment,
                    bundle
                )
            }
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
        if (value == Constants.REMOVE_VEHICLE) {
            val bundle = Bundle()
            bundle.putInt(Constants.VEHICLE_INDEX, position)
            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            findNavController().navigate(
                R.id.action_vehicleListFragment_to_removeVehicleFragment,
                bundle
            )
        } else {

            val bundle = Bundle()

            if (isDblaAvailable == true) {
                bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                findNavController().navigate(
                    R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment,
                    bundle
                )
            } else {
                bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                if (isDblaAvailable != null) {
                    bundle.putBoolean(Constants.IS_DBLA_AVAILABLE, isDblaAvailable)
                }
                findNavController().navigate(
                    R.id.action_vehicleListFragment_to_addNewVehicleDetailsFragment,
                    bundle
                )
            }
        }

    }

    private fun invalidateList() {
        val accountData = NewCreateAccountRequestModel
        vehicleList = accountData.vehicleList as ArrayList<NewVehicleInfoDetails>
        Log.e("TAG", "onClick:  vehicleList " + vehicleList)

        vehicleAdapter = VehicleListAdapter(requireContext(), vehicleList, this)
        val size = vehicleAdapter.itemCount
        var text = "vehicle"
        if (size > 1) {
            text = "vehicles"
        }
        binding.youHaveAddedVehicle.text = "You've added $size $text"
        if (size == 0) {
            binding.btnNext.disable()
        }
        binding.recyclerView.adapter = vehicleAdapter
    }


    override fun onClick(v: View?) {
        val navCall = navFlowCall.equals(Constants.VEHICLE_MANAGEMENT, true)
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        when (v?.id) {

            R.id.btnAddNewVehicle -> {
                if (NewCreateAccountRequestModel.personalAccount) {
                    if (vehicleList.size >= BuildConfig.PERSONAL.toInt()) {
                        NewCreateAccountRequestModel.isMaxVehicleAdded = true
                        findNavController().navigate(
                            R.id.action_vehicleListFragment_to_maximumVehicleFragment,
                            bundle
                        )
                    } else {
                        if (navCall) {

                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment,
                                bundle
                            )

                        } else {
                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment,
                                bundle
                            )
                        }

                    }
                } else {
                    if (vehicleList.size >= BuildConfig.BUSINESS.toInt()) {
                        NewCreateAccountRequestModel.isMaxVehicleAdded = true
                        findNavController().navigate(
                            R.id.action_vehicleListFragment_to_maximumVehicleFragment,
                            bundle
                        )
                    } else {
                        if (navCall) {

                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment,
                                bundle
                            )

                        } else {
                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_createAccountFindVehicleFragment,
                                bundle
                            )
                        }
                    }
                }
            }

            R.id.btnNext -> {
                if (navCall) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    if (vehicleList.size > 0) {
                        apiCall(apiRequestCount)
                    }


                } else {
                    if (NewCreateAccountRequestModel.personalAccount) {
                        if (vehicleList.size > BuildConfig.PERSONAL.toInt()) {
                            NewCreateAccountRequestModel.isMaxVehicleAdded = true
                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_maximumVehicleFragment,
                                bundle
                            )
                        } else {
                            emailHeartBeatApi()
                            smsHeartBeatApi()
                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_createAccountSummaryFragment,
                                bundle
                            )

                        }

                    } else {

                        if (vehicleList.size > BuildConfig.BUSINESS.toInt()) {
                            NewCreateAccountRequestModel.isMaxVehicleAdded = true
                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_maximumVehicleFragment,
                                bundle
                            )
                        } else {
                            findNavController().navigate(
                                R.id.action_vehicleListFragment_to_createAccountSummaryFragment,
                                bundle
                            )
                        }

                    }
                }
            }
        }
    }

    private fun apiCall(index: Int) {
        val obj = vehicleList[index]
        val data = AddVehicleRequest()
        data.plateInfo?.number = obj.plateNumber
        if (obj.plateCountry.isNullOrEmpty()) {
            data.plateInfo?.country = "UK"
        } else {
            data.plateInfo?.country = obj.plateCountry
        }
        data.plateInfo?.vehicleGroup = " "
        data.plateInfo?.vehicleComments = ""
        data.plateInfo?.planName = ""
        data.plateInfo?.state = "HE"
        data.plateInfo?.type = "STANDARD"

        data.vehicleInfo?.year = 2023
        data.vehicleInfo?.effectiveStartDate = ""
        if (obj.vehicleModel.isNullOrEmpty()) {
            data.vehicleInfo?.model = "Not Provided"
        } else {
            data.vehicleInfo?.model = obj.vehicleModel
        }
        if (obj.vehicleMake.isNullOrEmpty()) {
            data.vehicleInfo?.make = "Not Provided"
        } else {
            data.vehicleInfo?.make = obj.vehicleMake
        }
        if (obj.vehicleColor.isNullOrEmpty()) {
            data.vehicleInfo?.color = "Not Provided"
        } else {
            data.vehicleInfo?.color = obj.vehicleColor
        }
        data.vehicleInfo?.typeId = ""
        data.vehicleInfo?.typeDescription = "REGULAR"
        if (obj.vehicleClass.isNullOrEmpty()) {
            data.vehicleInfo?.vehicleClassDesc = "2"
        } else {
            data.vehicleInfo?.vehicleClassDesc = Utils.getVehicleTypeNumber(obj.vehicleClass?:"")
        }


        vehicleMgmtViewModel.addVehicleApiNew(data)
    }
}