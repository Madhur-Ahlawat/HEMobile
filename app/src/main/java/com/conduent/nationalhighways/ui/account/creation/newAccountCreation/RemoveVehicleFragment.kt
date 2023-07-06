package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentRemoveVehicleBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Utils


class RemoveVehicleFragment : BaseFragment<FragmentRemoveVehicleBinding>(), View.OnClickListener {


    private var index: Int? = null
    private lateinit var vehicleList:ArrayList<NewVehicleInfoDetails>
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null
    private var vehicleDetails : VehicleResponse? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRemoveVehicleBinding= FragmentRemoveVehicleBinding.inflate(inflater,container,false)

    override fun init() {
        index = arguments?.getInt(Constants.VEHICLE_INDEX)
        var numberPlate = ""
        when (index){
            -1 -> {
                setData()
                binding.regNum.text = vehicleDetails?.plateInfo?.number.toString()
                binding.isYourVehicle.text = getString(R.string.vehicle_details)
                binding.confirmBtn.visibility  =View.GONE
                binding.notVehicle.visibility  =View.GONE
            }
            -2 -> {
                setData()
                numberPlate = vehicleDetails?.plateInfo?.number.toString()
                binding.regNum.text = numberPlate
                binding.isYourVehicle.text = getString(R.string.are_you_sure_you_want_to_remove_vehicle,numberPlate)
            }
            else -> {
                val accountData = NewCreateAccountRequestModel
                vehicleList = accountData.vehicleList as ArrayList<NewVehicleInfoDetails>
                nonUKVehicleModel = index?.let { vehicleList[it] }
                numberPlate = nonUKVehicleModel?.plateNumber ?: ""
                binding.regNum.text = numberPlate
                binding.typeOfVehicle.text = Utils.getVehicleType(nonUKVehicleModel?.vehicleClass ?: "")
                binding.vehicleMake.text = nonUKVehicleModel?.vehicleMake ?: ""
                binding.vehicleModel.text = nonUKVehicleModel?.vehicleModel ?: ""
                binding.vehicleColor.text = nonUKVehicleModel?.vehicleColor ?: ""
                binding.isYourVehicle.text = getString(R.string.are_you_sure_you_want_to_remove_vehicle,numberPlate)
            }
        }

        binding.strEffectiveDateText.text= Utils.getYesterdayDate()


    }

    private fun setData() {
        vehicleDetails = arguments?.getParcelable(Constants.DATA)
        binding.typeOfVehicle.text = Utils.getVehicleType(vehicleDetails?.plateInfo?.type ?: "")
        binding.vehicleMake.text = vehicleDetails?.vehicleInfo?.make ?: ""
        binding.vehicleModel.text = vehicleDetails?.vehicleInfo?.model ?: ""
        binding.vehicleColor.text = vehicleDetails?.vehicleInfo?.color ?: ""
    }

    override fun initCtrl() {
        binding.confirmBtn.setOnClickListener(this)
        binding.notVehicle.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.confirmBtn -> {
                index?.let { vehicleList.removeAt(it) }
                if(vehicleList.isEmpty()){
                    findNavController().navigate(R.id.action_removeVehicleFragment_to_findVehicleFragment)
                }else {
                    findNavController().popBackStack()
                }

            }

            R.id.notVehicle -> {
                findNavController().popBackStack()
            }

        }
    }

}