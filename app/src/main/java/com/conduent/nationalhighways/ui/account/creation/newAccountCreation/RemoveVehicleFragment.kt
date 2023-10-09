package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentRemoveVehicleBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveVehicleFragment : BaseFragment<FragmentRemoveVehicleBinding>(), View.OnClickListener {


    private var index: Int? = null
    private lateinit var vehicleList: ArrayList<NewVehicleInfoDetails>
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null
    private var vehicleDetails: VehicleResponse? = null
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var loader: LoaderDialog? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRemoveVehicleBinding =
        FragmentRemoveVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        index = arguments?.getInt(Constants.VEHICLE_INDEX)
        var numberPlate = ""
        when (index) {
            -1 -> {
                setData()
                binding.regNum.text = vehicleDetails?.plateInfo?.number.toString()
                binding.isYourVehicle.text = getString(R.string.vehicle_details)
                binding.confirmBtn.visibility = View.GONE
                binding.notVehicle.visibility = View.GONE
            }

            -2 -> {
                setData()
                numberPlate = vehicleDetails?.plateInfo?.number.toString()
                binding.regNum.text = numberPlate
                binding.isYourVehicle.text =
                    getString(R.string.are_you_sure_you_want_to_remove_vehicle, numberPlate)
            }

            else -> {
                val accountData = NewCreateAccountRequestModel
                vehicleList = accountData.vehicleList as ArrayList<NewVehicleInfoDetails>
                nonUKVehicleModel = index?.let { vehicleList[it] }
                numberPlate = nonUKVehicleModel?.plateNumber ?: ""
                binding.regNum.text = numberPlate
                binding.typeOfVehicle.text =
                    Utils.getVehicleType(requireActivity(),nonUKVehicleModel?.vehicleClass ?: "")
                binding.vehicleMake.text = nonUKVehicleModel?.vehicleMake ?: ""
                binding.vehicleModel.text = nonUKVehicleModel?.vehicleModel ?: ""
                binding.vehicleColor.text = nonUKVehicleModel?.vehicleColor ?: ""
                binding.isYourVehicle.text =
                    getString(R.string.are_you_sure_you_want_to_remove_vehicle, numberPlate)
            }
        }

        binding.strEffectiveDateText.text = Utils.getYesterdayDate()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    private fun setData() {
        vehicleDetails = arguments?.getParcelable(Constants.DATA)
        binding.typeOfVehicle.text =
            Utils.getVehicleType(
                requireActivity(),
                vehicleDetails?.vehicleInfo?.vehicleClassDesc ?: ""
            )
        binding.vehicleMake.text = vehicleDetails?.vehicleInfo?.make ?: ""
        binding.vehicleModel.text = vehicleDetails?.vehicleInfo?.model ?: ""
        binding.vehicleColor.text = vehicleDetails?.vehicleInfo?.color ?: ""
    }

    override fun initCtrl() {
        binding.confirmBtn.setOnClickListener(this)
        binding.notVehicle.setOnClickListener(this)
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.deleteVehicleApiVal, ::handleDeleteVehicle)
    }

    private fun handleDeleteVehicle(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.REMOVE_VEHICLE)
                bundle.putParcelable(Constants.NAV_DATA_KEY, vehicleDetails)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_removeVehicleFragment_to_resetForgotPassword,
                    bundle
                )
            }

            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {

            }
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.confirmBtn -> {
                if (index == -2) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    val selectedVehicleList = mutableListOf<String?>()
                    selectedVehicleList.add(vehicleDetails?.vehicleInfo?.rowId)
                    vehicleMgmtViewModel.deleteVehicleApi(selectedVehicleList)
                } else {
                    index?.let { vehicleList.removeAt(it) }
                    if (vehicleList.isEmpty()) {
                        findNavController().navigate(R.id.action_removeVehicleFragment_to_findVehicleFragment)
                    } else {
                        findNavController().popBackStack()
                    }
                }

            }

            R.id.notVehicle -> {
                findNavController().popBackStack()
            }

        }
    }

}